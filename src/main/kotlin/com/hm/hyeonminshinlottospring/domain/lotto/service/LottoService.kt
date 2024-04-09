package com.hm.hyeonminshinlottospring.domain.lotto.service

import com.hm.hyeonminshinlottospring.domain.lotto.domain.GenerateMode
import com.hm.hyeonminshinlottospring.domain.lotto.domain.Lotto
import com.hm.hyeonminshinlottospring.domain.lotto.domain.LottoNumbers
import com.hm.hyeonminshinlottospring.domain.lotto.domain.info.LottoPrice
import com.hm.hyeonminshinlottospring.domain.lotto.dto.LottoCreateRequest
import com.hm.hyeonminshinlottospring.domain.lotto.dto.LottoCreateResponse
import com.hm.hyeonminshinlottospring.domain.lotto.dto.SliceLottoNumberResponse
import com.hm.hyeonminshinlottospring.domain.lotto.repository.LottoRepository
import com.hm.hyeonminshinlottospring.domain.lotto.service.generator.RandomLottoNumbersGenerator
import com.hm.hyeonminshinlottospring.domain.user.domain.User
import com.hm.hyeonminshinlottospring.domain.user.domain.UserRole
import com.hm.hyeonminshinlottospring.domain.user.repository.UserRepository
import com.hm.hyeonminshinlottospring.domain.user.repository.findByUserId
import com.hm.hyeonminshinlottospring.domain.winninglotto.domain.WinningLottoInformation
import com.hm.hyeonminshinlottospring.global.config.LOOM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.withLock
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LottoService(
    private val randomLottoNumbersGenerator: RandomLottoNumbersGenerator,
    private val winningLottoInformation: WinningLottoInformation,
    private val lottoRepository: LottoRepository,
    private val userRepository: UserRepository,
) {
    /**
     * ### 주의해야할 점
     *
     * Spring의 Transactional은 AOP 기반으로 동작하는데 내부적으로 자동 커밋을 비활성화하고 트랜잭션을 시작한다.
     * 하지만 해당 커넥션은 하나의 스레드에서 공유할 수 있는 ThreadLocal 변수를 사용하는데,
     * 이것은 '단일 스레드' 내에서만 유효하다.
     *
     * 따라서 이를 해결하기 위한 방법으로 다음 2가지가 있을 것이다.
     * 1. 트랜잭션이 발생하는 부분과 비즈니스 로직(비동기 부분)을 분리하기
     *    - @Transactional이 붙으면 해당 함수를 suspend로 만들어선 안된다. 그렇지 않으면 Dirty Checking 없다.
     * 2. 두 부분을 합친다면, 트랜잭션을 다루는 부분(롤백을 포함함)과 DB lock 부분을 세밀히 조정할 것
     *
     * 아래는 로또 번호 생성 부분만을 coroutine으로 비동기 처리했다.
     */
    @Transactional
    fun createLottos(
        request: LottoCreateRequest,
    ): LottoCreateResponse = runBlocking {
        val userId = request.userId
        val user = userRepository.findByUserId(userId)
        check(request.insertedMoney <= user.money) { "${request.userId}: 현재 소지한 금액(${user.money}${LottoPrice.UNIT})보다 적은 금액을 입력해주세요." }
        val generateCount = request.insertedMoney / LottoPrice.PER_PRICE

        val round = winningLottoInformation.roundMutex.withLock {
            winningLottoInformation.round
        }
        val lottoListDeferred = async(Dispatchers.LOOM) {
            when (request.mode) {
                GenerateMode.MANUAL -> createManualLottos(request, round, user)
                GenerateMode.RANDOM -> createRandomLottos(generateCount, round, user)
            }
        }

        val savedLottos = lottoRepository.saveAll(lottoListDeferred.await())
        val successCount = savedLottos.size
        user.withdrawMoney(successCount * LottoPrice.PER_PRICE)
        LottoCreateResponse(userId, round, successCount)
    }

    @Transactional(readOnly = true)
    fun getLottosByUserAndRound(
        userId: Long,
        round: Int,
        pageable: Pageable,
    ): SliceLottoNumberResponse {
        val user = userRepository.findByUserId(userId)
        check(user.userRole != UserRole.ROLE_ADMIN) { "$userId: 주어진 유저 ID는 조회할 수 없습니다." }
        val slice = lottoRepository.findSliceByUserIdAndRound(userId, round, pageable)
        return SliceLottoNumberResponse.from(slice)
    }

    private suspend fun createManualLottos(
        request: LottoCreateRequest,
        round: Int,
        user: User,
    ) = request.numbers?.asFlow()
        ?.mapNotNull {
            try {
                Lotto(
                    round = round,
                    user = user,
                    numbers = it,
                )
            } catch (e: Exception) {
                null
            }
        }
        ?.toList()
        ?: emptyList()

    private suspend fun createRandomLottos(
        generateCount: Int,
        round: Int,
        user: User,
    ): List<Lotto> = coroutineScope {
        val resultDeferred = List(generateCount) {
            async {
                Lotto(
                    round = round,
                    user = user,
                    numbers = randomLottoNumbersGenerator.generate(),
                )
            }
        }
        resultDeferred.awaitAll()
    }

    /**
     * 테스트 시 성능 비교용 레거시 코드.
     * pr merge 되기 전에 지울 예정!
     */
    @Transactional
    @Deprecated("Blocking IO method")
    fun createLottosLegacy(
        request: LottoCreateRequest,
    ): LottoCreateResponse {
        val userId = request.userId
        val user = userRepository.findByUserId(userId)
        user.withdrawMoney(request.insertedMoney)
        val round = winningLottoInformation.round
        val savedLottos = lottoRepository.saveAll(request.toEntities(user, round))
        return LottoCreateResponse(userId, round, savedLottos.size)
    }

    /**
     * 테스트용 레거시 코드 2.
     * 지울 예정!
     */
    @Deprecated("Blocking IO method")
    private fun LottoCreateRequest.toEntities(
        user: User,
        round: Int,
    ): List<Lotto> {
        val result = mutableListOf<Lotto>()
        val generateCount = this.insertedMoney / LottoPrice.PER_PRICE
        when (this.mode) {
            GenerateMode.RANDOM ->
                repeat(generateCount) {
                    result.add(
                        Lotto(
                            round = round,
                            user = user,
                            numbers =
                            (LottoNumbers.LOTTO_MIN_NUMBER..LottoNumbers.LOTTO_MAX_NUMBER)
                                .shuffled()
                                .take(LottoNumbers.NUM_OF_LOTTO_NUMBERS),
                        ),
                    )
                }

            GenerateMode.MANUAL -> {
                val numbersIterator = this.numbers?.listIterator() ?: listOf<List<Int>>().listIterator()
                while (numbersIterator.hasNext()) {
                    result.add(
                        Lotto(
                            round = round,
                            user = user,
                            numbers = numbersIterator.next(),
                        ),
                    )
                }
            }
        }
        return result.toList()
    }
}
