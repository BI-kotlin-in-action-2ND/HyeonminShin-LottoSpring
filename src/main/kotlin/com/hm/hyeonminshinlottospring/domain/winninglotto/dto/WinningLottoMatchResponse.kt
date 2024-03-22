package com.hm.hyeonminshinlottospring.domain.winninglotto.dto

import com.hm.hyeonminshinlottospring.domain.lotto.domain.Lotto
import com.hm.hyeonminshinlottospring.domain.lotto.domain.LottoNumber
import com.hm.hyeonminshinlottospring.domain.lotto.domain.info.LottoRank

class WinningLottoMatchResponse(
    val numbers: LottoNumber,
    val matched: List<Int>,
    val rankString: String,
    val prize: Int,
) {
    companion object {
        fun from(
            lotto: Lotto,
            matched: List<Int>,
            rank: LottoRank,
        ) = WinningLottoMatchResponse(
            numbers = lotto.numbers,
            matched = matched,
            rankString = rank.rankString,
            prize = rank.prize,
        )
    }
}
