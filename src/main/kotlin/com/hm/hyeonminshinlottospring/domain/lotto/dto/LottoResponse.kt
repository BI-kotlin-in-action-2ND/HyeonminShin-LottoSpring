package com.hm.hyeonminshinlottospring.domain.lotto.dto

import com.hm.hyeonminshinlottospring.domain.lotto.domain.Lotto
import com.hm.hyeonminshinlottospring.domain.lotto.domain.LottoNumber
import java.util.SortedSet

data class LottoResponse(
    val userId: Long,
    val round: Int,
    val numbers: SortedSet<LottoNumber>,
    val lottoId: Long?,
) {
    companion object {
        fun from(
            userId: Long,
            lotto: Lotto,
        ) = LottoResponse(
            userId,
            lotto.round,
            lotto.numbers,
            lotto.id,
        )
    }
}
