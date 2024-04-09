package com.hm.hyeonminshinlottospring.domain.winninglotto.domain

import com.hm.hyeonminshinlottospring.domain.lotto.repository.LottoRepository
import kotlinx.coroutines.sync.Mutex
import org.springframework.stereotype.Component

@Component
class WinningLottoInformation(
    lottoRepository: LottoRepository,
) {
    val roundMutex = Mutex()
    var round: Int = (lottoRepository.findFirstByOrderByRoundDesc()?.round ?: 0) + 1
        protected set

    fun increaseRound() {
        round += 1
    }
}
