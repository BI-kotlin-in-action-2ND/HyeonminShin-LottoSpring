package com.hm.hyeonminshinlottospring.domain.winninglotto.dto

import com.hm.hyeonminshinlottospring.domain.lotto.domain.LottoNumber
import java.util.SortedSet

class WinningLottoMatchResponse(
    val numbers: SortedSet<LottoNumber>,
    val matched: List<Int>,
    val rank: Int,
    val rankString: String,
    val prize: Int,
) {
    companion object {
        fun from(
            numbers: SortedSet<LottoNumber>,
            matched: List<Int>,
            rank: Int,
            rankString: String,
            prize: Int,
        ) = WinningLottoMatchResponse(
            numbers = numbers,
            matched = matched,
            rank = rank,
            rankString = rankString,
            prize = prize,
        )
    }
}
