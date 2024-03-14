package com.hm.hyeonminshinlottospring.domain.user.dto

import com.hm.hyeonminshinlottospring.domain.user.domain.UserRole

data class UserResponse(
    val userId: Long?,
    val userName: String,
    val userRole: UserRole,
    val money: Int,
) {
    companion object {
        fun from(
            userId: Long?,
            userName: String,
            userRole: UserRole,
            money: Int,
        ) = UserResponse(
            userId,
            userName,
            userRole,
            money,
        )
    }
}
