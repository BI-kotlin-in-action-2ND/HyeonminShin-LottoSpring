package com.hm.hyeonminshinlottospring.domain.lotto.dto

data class LottoCreateResponse(
    val userId: Long,
    val round: Int,
    val createdLottoCount: Int,
)
