package com.hm.hyeonminshinlottospring.domain.user.dto

import com.hm.hyeonminshinlottospring.domain.user.domain.User
import com.hm.hyeonminshinlottospring.domain.user.domain.UserRole
import jakarta.validation.constraints.NotNull

data class UserCreateRequest(
    @field:NotNull(message = "유저 이름은 필수 입력 값입니다.")
    val userName: String,
    @field:NotNull(message = "돈은 필수 입력 값입니다.")
    val money: Int,
    val userRole: UserRole?,
) {
    fun toEntity(
        userName: String,
        money: Int,
        userRole: UserRole,
    ) = User(
        userName = userName,
        money = money,
        userRole = userRole,
    )
}
