package com.hm.hyeonminshinlottospring.domain.winninglotto.service

import com.hm.hyeonminshinlottospring.domain.lotto.domain.Lotto
import com.hm.hyeonminshinlottospring.domain.lotto.domain.info.LottoRank
import com.hm.hyeonminshinlottospring.domain.lotto.repository.LottoRepository
import com.hm.hyeonminshinlottospring.domain.user.domain.UserRole
import com.hm.hyeonminshinlottospring.domain.user.repository.UserRepository
import com.hm.hyeonminshinlottospring.domain.user.repository.findByUserId
import com.hm.hyeonminshinlottospring.domain.winninglotto.domain.WinningLottoInformation
import com.hm.hyeonminshinlottospring.domain.winninglotto.dto.TotalMatchResponse
import com.hm.hyeonminshinlottospring.domain.winninglotto.dto.WinningLottoMatchResponse
import com.hm.hyeonminshinlottospring.domain.winninglotto.dto.WinningLottoRoundResponse
import com.hm.hyeonminshinlottospring.domain.winninglotto.repository.WinningLottoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.withLock
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WinningLottoService(
    private val winningLottoInformation: WinningLottoInformation,
    private val lottoRepository: LottoRepository,
    private val winningLottoRepository: WinningLottoRepository,
    private val userRepository: UserRepository,
) {
    // Admin can access the current winning lotto numbers.
    @Transactional(readOnly = true)
    fun getWinningLottoByRound(
        userId: Long,
        round: Int,
    ): WinningLottoRoundResponse = runBlocking {
        val user = userRepository.findByUserId(userId)
        launch(Dispatchers.Default) { validateRoundWithUseRole(user.userRole, round) }.join()

        val winningLotto = getWinningLottoByRound(round)
        WinningLottoRoundResponse.from(winningLotto)
    }

    /**
     * 상금을 얻은 로또만을 리턴한다.
     */
    @Transactional(readOnly = true)
    fun matchUserLottoByRound(
        userId: Long,
        round: Int,
    ): TotalMatchResponse = runBlocking {
        val user = userRepository.findByUserId(userId)
        check(user.userRole == UserRole.ROLE_USER) { "주어진 유저 ID는 매칭할 수 없습니다." }
        launch(Dispatchers.Default) { validateRoundWithUseRole(user.userRole, round) }.join()
        val resultDeferred = async { lottoRepository.findListByUserIdAndRound(userId, round) }
        check(resultDeferred.await().isNotEmpty()) { "$userId: 해당 유저가 $round 라운드에 구매한 로또가 존재하지 않습니다." }
        val winningLotto = getWinningLottoByRound(round)
        getTotalMatchResponse(resultDeferred.await(), winningLotto.lotto)
    }

    private fun getWinningLottoByRound(round: Int) =
        winningLottoRepository.findByRound(round) ?: throw NoSuchElementException("$round 라운드에 해당하는 당첨 번호가 존재하지 않습니다.")

    private suspend fun getTotalMatchResponse(
        userLottos: List<Lotto>,
        winLotto: Lotto,
    ): TotalMatchResponse = TotalMatchResponse.from(
        userLottos.asFlow()
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

    private suspend fun validateRoundWithUseRole(
        userRole: UserRole,
        round: Int,
    ) {
        winningLottoInformation.roundMutex.withLock {
            if (userRole == UserRole.ROLE_USER) {
                require(round != winningLottoInformation.round) { "현재 진행중인 라운드는 조회할 수 없습니다." }
            }
            require(round in 1..winningLottoInformation.round) { "존재하지 않는 라운드는 조회할 수 없습니다." }
        }
    }
}
