package com.hm.hyeonminshinlottospring.domain.winninglotto.dto

import com.hm.hyeonminshinlottospring.domain.lotto.domain.LottoNumber
import java.util.SortedSet

class WinningLottoRoundResponse(
    val round: Int,
    val numbers: SortedSet<LottoNumber>,
) {
    companion object {
        fun from(
            round: Int,
            numbers: SortedSet<LottoNumber>,
        ) = WinningLottoRoundResponse(
            round = round,
            numbers = numbers,
        )
    }
}
