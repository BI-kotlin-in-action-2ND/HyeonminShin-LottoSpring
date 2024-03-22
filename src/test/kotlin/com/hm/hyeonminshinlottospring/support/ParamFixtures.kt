package com.hm.hyeonminshinlottospring.support

import org.springframework.data.domain.PageRequest

const val USER_ID_PARAM = "userId"
const val CHARGE_PARAM = "charge"
const val ROUND_PARAM = "round"
const val PAGE_PARAM = "page"
const val SIZE_PARAM = "size"
const val TEST_DEFAULT_PAGE = 0
const val TEST_DEFAULT_SIZE = 20
const val TEST_PAGE = 1
const val TEST_SIZE = 1
const val TEST_INVALID_SIZE: Int = -1
val TEST_PAGEABLE = PageRequest.of(TEST_PAGE, TEST_SIZE)
val TEST_DEFAULT_PAGEABLE = PageRequest.of(TEST_DEFAULT_PAGE, TEST_DEFAULT_SIZE)
