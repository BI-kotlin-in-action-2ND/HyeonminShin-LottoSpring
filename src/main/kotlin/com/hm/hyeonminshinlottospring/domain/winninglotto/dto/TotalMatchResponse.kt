package com.hm.hyeonminshinlottospring.domain.winninglotto.dto

data class TotalMatchResponse(
    val size: Int,
    val matchResult: List<WinningLottoMatchResponse>,
) {
    companion object {
        fun from(matchResult: List<WinningLottoMatchResponse>) =
            TotalMatchResponse(
                size = matchResult.size,
                matchResult = matchResult,
            )
    }
}
