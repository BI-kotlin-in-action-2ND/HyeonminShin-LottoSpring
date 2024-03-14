package com.hm.hyeonminshinlottospring.domain.lotto.dto

data class LottoSimpleResponse(
    val userId: Long,
    val round: Int,
    val size: Int,
) {
    companion object {
        fun from(
            userId: Long,
            round: Int,
            size: Int,
        ) = LottoSimpleResponse(
            userId = userId,
            round = round,
            size = size,
        )
    }
}
