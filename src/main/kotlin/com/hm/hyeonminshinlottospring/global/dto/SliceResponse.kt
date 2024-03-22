package com.hm.hyeonminshinlottospring.global.dto

import org.springframework.data.domain.Slice

class SliceResponse<T>(
    val hasNext: Boolean,
    val numberOfElements: Int,
    val content: List<T>,
) {
    constructor(slice: Slice<T>) : this(
        hasNext = slice.hasNext(),
        numberOfElements = slice.numberOfElements,
        content = slice.content,
    )
}
