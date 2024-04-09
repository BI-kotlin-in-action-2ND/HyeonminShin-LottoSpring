package com.hm.hyeonminshinlottospring.domain.lotto.dto

import com.hm.hyeonminshinlottospring.domain.lotto.domain.Lotto
import com.hm.hyeonminshinlottospring.domain.user.domain.User
import org.springframework.data.domain.Slice

data class SliceLottoNumberResponse(
    val userId: Long?,
    val round: Int?,
    val hasNext: Boolean,
    val numberOfElements: Int,
    val numberList: List<String>,
) {
    companion object {
        fun from(slice: Slice<Lotto>): SliceLottoNumberResponse {
            val lotto1: Lotto? = if (slice.numberOfElements != 0) slice.content[0] else null
            val user: User? = lotto1?.user
            return SliceLottoNumberResponse(
                userId = user?.id,
                round = lotto1?.round,
                hasNext = slice.hasNext(),
                numberOfElements = slice.numberOfElements,
                numberList = slice.content.map {
                    it.numbers.toString()
                },
            )
        }
    }
}
