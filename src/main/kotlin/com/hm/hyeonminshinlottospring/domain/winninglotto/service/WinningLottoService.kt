package com.hm.hyeonminshinlottospring.domain.winninglotto.service

import com.hm.hyeonminshinlottospring.domain.lotto.domain.Lotto
import com.hm.hyeonminshinlottospring.domain.lotto.domain.info.LottoRank
import com.hm.hyeonminshinlottospring.domain.lotto.repository.LottoRepository
import com.hm.hyeonminshinlottospring.domain.lotto.repository.getByUserAndRound
import com.hm.hyeonminshinlottospring.domain.lotto.service.generator.RandomLottoNumbersGenerator
import com.hm.hyeonminshinlottospring.domain.user.domain.User
import com.hm.hyeonminshinlottospring.domain.user.domain.UserRole
import com.hm.hyeonminshinlottospring.domain.user.repository.UserRepository
import com.hm.hyeonminshinlottospring.domain.user.repository.existsByUserId
import com.hm.hyeonminshinlottospring.domain.user.repository.getByUserId
import com.hm.hyeonminshinlottospring.domain.winninglotto.domain.WinningLotto
import com.hm.hyeonminshinlottospring.domain.winninglotto.domain.WinningLottoInformation
import com.hm.hyeonminshinlottospring.domain.winninglotto.dto.WinningLottoMatchResponse
import com.hm.hyeonminshinlottospring.domain.winninglotto.dto.WinningLottoRoundResponse
import com.hm.hyeonminshinlottospring.domain.winninglotto.repository.WinningLottoRepository
import com.hm.hyeonminshinlottospring.domain.winninglotto.repository.getByRound
import com.hm.hyeonminshinlottospring.global.support.dto.SliceResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WinningLottoService(
    private val winningLottoInformation: WinningLottoInformation,
    private val randomLottoNumbersGenerator: RandomLottoNumbersGenerator,
    private val lottoRepository: LottoRepository,
    private val winningLottoRepository: WinningLottoRepository,
    private val userRepository: UserRepository,
) {
    // Can only be called from Spring Scheduler
    @Transactional
    fun createWinningLotto() {
        val round = winningLottoInformation.round
        val admin =
            userRepository.save(
                User(
                    userName = "LG-R$round",
                    userRole = UserRole.ROLE_ADMIN,
                ),
            )
        val savedRandomLotto =
            lottoRepository.save(
                Lotto(
                    round = round,
                    user = admin,
                    numbers = randomLottoNumbersGenerator.generate(),
                ),
            )
        val savedWinningLotto =
            winningLottoRepository.save(
                WinningLotto(round, savedRandomLotto),
            )
        // Round increase
        winningLottoInformation.increaseRound()
    }

    @Transactional(readOnly = true)
    fun getWinningLottoByRound(
        userId: Long,
        round: Int,
    ): WinningLottoRoundResponse {
        // Admin can access the current winning lotto numbers.
        val userRole = userRepository.getByUserId(userId).userRole
        if (userRole == UserRole.ROLE_USER) {
            validateNotCurrentRound(round)
        }

        val winningLotto = winningLottoRepository.getByRound(round)
        return getWinningLottoResponse(winningLotto)
    }

    /**
     * 상금을 얻은 로또만을 리턴한다.
     */
    @Transactional(readOnly = true)
    fun matchUserLottoByRound(
        userId: Long,
        round: Int,
    ): SliceResponse<WinningLottoMatchResponse> {
        validateUserExistence(userId)
        validateNotCurrentRound(round)
        val user = userRepository.getByUserId(userId)
        val userLottos = lottoRepository.getByUserAndRound(user, round)
        validateUserHasLotto(userLottos, userId, round)
        val winLotto = winningLottoRepository.getByRound(round)
        return getSliceMatchResponse(userLottos, winLotto)
    }

    private fun getWinningLottoResponse(winningLotto: Lotto) =
        WinningLottoRoundResponse.from(
            winningLotto.round,
            winningLotto.numbers,
        )

    private fun getSliceMatchResponse(
        userLottos: List<Lotto>,
        winLotto: Lotto,
    ): SliceResponse<WinningLottoMatchResponse> {
        val content =
            userLottos.asSequence()
                .filter { lotto -> winLotto.numbers.count { it in lotto.numbers } > 0 }
                .map { lotto ->
                    val matched = lotto.match(winLotto)
                    val rank = LottoRank.getRank(matched.size)
                    WinningLottoMatchResponse.from(
                        lotto.numbers,
                        matched,
                        matched.size,
                        rank.rankString,
                        rank.prize,
                    )
                }.toList()

        return SliceResponse(
            size = content.size,
            content = content,
        )
    }

    private fun validateNotCurrentRound(round: Int) {
        require(round != winningLottoInformation.round) { "현재 진행중인 라운드는 조회할 수 없습니다." }
    }

    private fun validateUserExistence(userId: Long) {
        if (!userRepository.existsByUserId(userId)) {
            throw NoSuchElementException("$userId: 해당 사용자가 존재하지 않습니다.")
        }
    }

    private fun validateUserHasLotto(
        userLottos: List<Lotto>,
        userId: Long,
        round: Int,
    ) {
        if (userLottos.isEmpty()) {
            throw NoSuchElementException("$userId: $round 라운드에 구매한 로또가 존재하지 않습니다.")
        }
    }
}
