package com.hm.hyeonminshinlottospring.support

import com.hm.hyeonminshinlottospring.domain.user.domain.User
import com.hm.hyeonminshinlottospring.domain.user.domain.UserRole
import com.hm.hyeonminshinlottospring.domain.user.dto.UserCreateRequest

const val TEST_USER_ID = 11L
const val TEST_OTHER_USER_ID = 12L
const val TEST_ANOTHER_USER_ID = 13L
const val TEST_ADMIN_USER_ID = 14L
const val TEST_NOT_EXIST_USER_ID = 15L
const val TEST_INVALID_USER_ID = -1L
const val TEST_USERNAME = "testUsername"
const val TEST_OTHER_USERNAME = "testOtherUsername"
const val TEST_ADMIN_USERNAME = "testAdmin"
const val TEST_NOT_EXIST_USERNAME = "testNotExist"
const val TEST_MONEY_ZERO = 0
const val TEST_MONEY_10 = 10
const val TEST_INVALID_MONEY = -1
val TEST_USER_ROLE_USER = UserRole.ROLE_USER
val TEST_USER_ROLE_ADMIN = UserRole.ROLE_ADMIN

fun createUser() =
    User(
        userName = TEST_USERNAME,
        userRole = TEST_USER_ROLE_USER,
        money = TEST_MONEY_10,
//        id = TEST_USER_ID,
    )

fun createOtherUser() =
    User(
        userName = TEST_OTHER_USERNAME,
        userRole = TEST_USER_ROLE_USER,
        money = TEST_MONEY_10,
//        id = TEST_OTHER_USER_ID,
    )

fun createAdmin() =
    User(
        userName = TEST_ADMIN_USERNAME,
        userRole = TEST_USER_ROLE_ADMIN,
        money = TEST_MONEY_ZERO,
//        id = TEST_ADMIN_USER_ID,
    )

fun createUserCreateRequest(
    userName: String,
    money: Int,
    userRole: UserRole,
) = UserCreateRequest(
    userName = userName,
    money = money,
    userRole = userRole,
)
