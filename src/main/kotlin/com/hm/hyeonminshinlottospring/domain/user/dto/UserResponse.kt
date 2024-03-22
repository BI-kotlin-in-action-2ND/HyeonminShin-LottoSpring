package com.hm.hyeonminshinlottospring.domain.user.dto

import com.hm.hyeonminshinlottospring.domain.user.domain.User
import com.hm.hyeonminshinlottospring.domain.user.domain.UserRole

data class UserResponse(
    val userName: String,
    val userRole: UserRole,
    val money: Int,
) {
    companion object {
        fun from(user: User) =
            UserResponse(
                userName = user.userName,
                userRole = user.userRole,
                money = user.money,
            )
    }
}
