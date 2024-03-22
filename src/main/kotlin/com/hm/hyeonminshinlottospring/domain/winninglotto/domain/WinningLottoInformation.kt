package com.hm.hyeonminshinlottospring.domain.winninglotto.domain

import org.springframework.stereotype.Component

@Component
class WinningLottoInformation {
    var round: Int = 1
        protected set

    fun increaseRound() {
        round += 1
    }
}
