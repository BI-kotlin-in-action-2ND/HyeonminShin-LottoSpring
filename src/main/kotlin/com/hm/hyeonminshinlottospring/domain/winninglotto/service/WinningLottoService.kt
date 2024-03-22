package com.hm.hyeonminshinlottospring.domain.winninglotto.service

import com.hm.hyeonminshinlottospring.domain.lotto.domain.Lotto
import com.hm.hyeonminshinlottospring.domain.lotto.domain.info.LottoRank
import com.hm.hyeonminshinlottospring.domain.lotto.repository.LottoRepository
import com.hm.hyeonminshinlottospring.domain.lotto.service.generator.RandomLottoNumbersGenerator
import com.hm.hyeonminshinlottospring.domain.user.domain.User
import com.hm.hyeonminshinlottospring.domain.user.domain.UserRole
import com.hm.hyeonminshinlottospring.domain.user.repository.UserRepository
import com.hm.hyeonminshinlottospring.domain.user.repository.findByUserId
import com.hm.hyeonminshinlottospring.domain.winninglotto.domain.WinningLotto
import com.hm.hyeonminshinlottospring.domain.winninglotto.domain.WinningLottoInformation
import com.hm.hyeonminshinlottospring.domain.winninglotto.dto.TotalMatchResponse
import com.hm.hyeonminshinlottospring.domain.winninglotto.dto.WinningLottoMatchResponse
import com.hm.hyeonminshinlottospring.domain.winninglotto.dto.WinningLottoRoundResponse
import com.hm.hyeonminshinlottospring.domain.winninglotto.repository.WinningLottoRepository
import org.springframework.scheduling.annotation.Scheduled
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
    @Scheduled(cron = "\${schedule.cron}")
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
        winningLottoRepository.save(WinningLotto(round, savedRandomLotto))
        winningLottoInformation.increaseRound()
    }

    // Admin can access the current winning lotto numbers.
    @Transactional(readOnly = true)
    fun getWinningLottoByRound(
        userId: Long,
        round: Int,
    ): WinningLottoRoundResponse {
        val user = userRepository.findByUserId(userId)
        validateRoundWithUseRole(user.userRole, round)

        val winningLotto = getWinningLottoByRound(round)
        return WinningLottoRoundResponse.from(winningLotto)
    }

    /**
     * 상금을 얻은 로또만을 리턴한다.
     */
    @Transactional(readOnly = true)
    fun matchUserLottoByRound(
        userId: Long,
        round: Int,
    ): TotalMatchResponse {
        val user = userRepository.findByUserId(userId)
        check(user.userRole == UserRole.ROLE_USER) { "주어진 유저 ID는 매칭할 수 없습니다." }
        validateRoundWithUseRole(user.userRole, round)
        val result = lottoRepository.findListByUserIdAndRound(userId, round)
        check(result.isNotEmpty()) { "$userId: 해당 유저가 $round 라운드에 구매한 로또가 존재하지 않습니다." }
        val winningLotto = getWinningLottoByRound(round)
        return getTotalMatchResponse(result, winningLotto.lotto)
    }

    private fun getWinningLottoByRound(round: Int) =
        winningLottoRepository.findByRound(round) ?: throw NoSuchElementException("$round 라운드에 해당하는 당첨 번호가 존재하지 않습니다.")

    private fun getTotalMatchResponse(
        userLottos: List<Lotto>,
        winLotto: Lotto,
    ): TotalMatchResponse = TotalMatchResponse.from(
        userLottos.asSequence()
            .filter { lotto -> winLotto.numbers.count(lotto.numbers) > 0 }
            .map { lotto ->
                val matched = lotto.match(winLotto)
                val rank = LottoRank.getRank(matched.size)
                WinningLottoMatchResponse.from(
                    lotto,
                    matched,
                    rank,
                )
            }
            .toList(),
    )

    private fun validateRoundWithUseRole(
        userRole: UserRole,
        round: Int,
    ) {
        if (userRole == UserRole.ROLE_USER) {
            require(round != winningLottoInformation.round) { "현재 진행중인 라운드는 조회할 수 없습니다." }
            require(round in 1..<winningLottoInformation.round) { "존재하지 않는 라운드는 조회할 수 없습니다." }
        } else if (userRole == UserRole.ROLE_ADMIN) {
            require(round in 1..winningLottoInformation.round) { "존재하지 않는 라운드는 조회할 수 없습니다." }
        }
    }
}
