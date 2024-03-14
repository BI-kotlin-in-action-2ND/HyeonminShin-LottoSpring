package com.hm.hyeonminshinlottospring.global.support.dto

import org.springframework.data.domain.Pageable

open class SliceResponse<T>(
    open val hasNext: Boolean,
    open val content: List<T>,
) {
    constructor(size: Int, content: List<T>) : this(
        content.size > size,
        if (content.size > size) content.dropLast(1) else content,
    )

    constructor(pageable: Pageable, content: List<T>) : this(
        pageable.pageSize,
        content,
    )
}
