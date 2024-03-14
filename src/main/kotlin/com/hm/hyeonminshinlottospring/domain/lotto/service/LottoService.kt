package com.hm.hyeonminshinlottospring.domain.lotto.service

import com.hm.hyeonminshinlottospring.domain.lotto.domain.GenerateMode
import com.hm.hyeonminshinlottospring.domain.lotto.dto.LottoCreateRequest
import com.hm.hyeonminshinlottospring.domain.lotto.dto.LottoResponse
import com.hm.hyeonminshinlottospring.domain.lotto.dto.LottoSimpleResponse
import com.hm.hyeonminshinlottospring.domain.lotto.repository.LottoRepository
import com.hm.hyeonminshinlottospring.domain.lotto.repository.getByUserAndRound
import com.hm.hyeonminshinlottospring.domain.lotto.service.generator.RandomLottoNumbersGenerator
import com.hm.hyeonminshinlottospring.domain.user.domain.User
import com.hm.hyeonminshinlottospring.domain.user.domain.UserRole
import com.hm.hyeonminshinlottospring.domain.user.repository.UserRepository
import com.hm.hyeonminshinlottospring.domain.user.repository.existsByUserId
import com.hm.hyeonminshinlottospring.domain.user.repository.getByUserId
import com.hm.hyeonminshinlottospring.domain.winninglotto.domain.WinningLottoInformation
import com.hm.hyeonminshinlottospring.global.support.dto.SliceResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LottoService(
    private val randomLottoNumbersGenerator: RandomLottoNumbersGenerator,
    private val winningLottoInformation: WinningLottoInformation,
    private val lottoRepository: LottoRepository,
    private val userRepository: UserRepository,
) {
    @Transactional
    fun createLottos(
        userId: Long,
        request: LottoCreateRequest,
    ): LottoSimpleResponse {
        val user = userRepository.getByUserId(userId)
        val round = request.round
        val successCount = createLottosWithMode(request, round, user)
        return getLottoSimpleResponse(userId, round, successCount)
    }

    @Transactional(readOnly = true)
    fun getLottosByUserAndRound(
        userId: Long,
        round: Int,
        size: Int? = 1,
    ): SliceResponse<LottoResponse> {
        validateUserExistence(userId)
        validateUserIsNotAdmin(userId)
        validateSizeAndRoundIsPositive(size, round)
        return getSliceLottosByUserAndRound(userId, round, size)
    }

    private fun createLottosWithMode(
        request: LottoCreateRequest,
        round: Int,
        user: User,
    ): Int {
        var successCount = 0
        when (request.mode) {
            GenerateMode.RANDOM ->
                repeat(request.generateCount) {
                    runCatching {
                        lottoRepository.save(
                            request.toEntity(
                                round,
                                user,
                                randomLottoNumbersGenerator.generate(),
                            ),
                        )
                    }.onSuccess { successCount += 1 }
                }

            GenerateMode.MANUAL -> {
                val numbersIterator = request.numbers?.listIterator() ?: listOf<List<Int>>().listIterator()
                while (numbersIterator.hasNext()) {
                    runCatching {
                        lottoRepository.save(
                            request.toEntity(
                                round,
                                user,
                                numbersIterator.next(),
                            ),
                        )
                    }.onSuccess { successCount += 1 }
                }
            }
        }
        return successCount
    }

    private fun getLottoSimpleResponse(
        userId: Long,
        round: Int,
        successCount: Int,
    ) = LottoSimpleResponse(userId, round, successCount)

    private fun getSliceLottosByUserAndRound(
        userId: Long,
        round: Int,
        size: Int?,
    ): SliceResponse<LottoResponse> {
        val user = userRepository.getByUserId(userId)
        val lottos = lottoRepository.getByUserAndRound(user, round)
        return SliceResponse(
            size = size ?: 1, // TODO: 해당 조건 없애고 QueryFactory 적용한거로 바꾸기
            content =
                lottos.map { lotto ->
                    LottoResponse.from(userId, lotto)
                },
        )
    }

    private fun validateUserExistence(userId: Long) {
        if (!userRepository.existsByUserId(userId)) {
            throw NoSuchElementException("$userId: 해당 사용자가 존재하지 않습니다.")
        }
    }

    // TODO: 사실 이 정보도 주면 안됨! 보안에 위협적.
    private fun validateUserIsNotAdmin(userId: Long) {
        val user = userRepository.getByUserId(userId)
        check(user.userRole != UserRole.ROLE_ADMIN) { "주어진 유저 ID는 Admin이 아니어야 합니다." }
    }

    private fun validateSizeAndRoundIsPositive(
        size: Int?,
        round: Int,
    ) {
        if (size != null) {
            require(size > 0) { "조회하려는 로또의 개수는 하나 이상이어야 합니다." }
        }
        require(round > 0) { "라운드는 양의 정수여야 합니다." }
    }
}
