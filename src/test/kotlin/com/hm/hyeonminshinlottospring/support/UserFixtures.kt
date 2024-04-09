package com.hm.hyeonminshinlottospring.support

import com.hm.hyeonminshinlottospring.domain.user.domain.User
import com.hm.hyeonminshinlottospring.domain.user.domain.UserRole
import com.hm.hyeonminshinlottospring.domain.user.dto.UserCreateRequest
import com.hm.hyeonminshinlottospring.domain.user.dto.UserMoneyPatchRequest
import com.hm.hyeonminshinlottospring.domain.user.dto.UserResponse

const val TEST_USER_ID = 1001L
const val TEST_OTHER_USER_ID = 1002L
const val TEST_ANOTHER_USER_ID = 1003L
const val TEST_ADMIN_USER_ID = 1004L
const val TEST_NOT_EXIST_USER_ID = 1005L
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

fun createUser(
    userName: String = TEST_USERNAME,
    userRole: UserRole = TEST_USER_ROLE_USER,
    money: Int = TEST_MONEY_10,
) = User(
    userName = userName,
    userRole = userRole,
    money = money,
)

fun createOtherUser(
    userName: String = TEST_OTHER_USERNAME,
    userRole: UserRole = TEST_USER_ROLE_USER,
    money: Int = TEST_MONEY_10,
) = User(
    userName = userName,
    userRole = userRole,
    money = money,
)

fun createAdmin(
    userName: String = TEST_ADMIN_USERNAME,
    userRole: UserRole = TEST_USER_ROLE_ADMIN,
    money: Int = TEST_MONEY_10,
) = User(
    userName = userName,
    userRole = userRole,
    money = money,
)

fun createUserCreateRequest(user: User) =
    UserCreateRequest(
        userName = user.userName,
        money = user.money,
        userRole = user.userRole,
    )

fun createUserMoneyPatchRequest(
    userId: Long = TEST_USER_ID,
    money: Int = TEST_MONEY_10,
) = UserMoneyPatchRequest(
    userId = userId,
    money = money,
)

fun createUserResponse(user: User) = UserResponse.from(user)
