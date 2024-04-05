package com.hm.hyeonminshinlottospring.domain.winninglotto.dto

import com.hm.hyeonminshinlottospring.domain.lotto.domain.LottoNumbers
import com.hm.hyeonminshinlottospring.domain.winninglotto.domain.WinningLotto

class WinningLottoRoundResponse(
    val round: Int,
    val numbers: LottoNumbers,
) {
    companion object {
        fun from(winningLotto: WinningLotto) =
            WinningLottoRoundResponse(
                round = winningLotto.round,
                numbers = winningLotto.lotto.numbers,
            )
    }
}
