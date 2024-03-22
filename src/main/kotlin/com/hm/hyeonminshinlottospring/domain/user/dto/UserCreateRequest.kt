package com.hm.hyeonminshinlottospring.domain.user.dto

import com.hm.hyeonminshinlottospring.domain.user.domain.User
import com.hm.hyeonminshinlottospring.domain.user.domain.UserRole
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero

data class UserCreateRequest(
    @field:NotBlank(message = "유저 이름은 필수 입력 값입니다.")
    val userName: String,
    @field:NotNull(message = "돈은 필수 입력 값입니다.")
    @field:PositiveOrZero(message = "돈은 항상 0 이상의 정수여야 합니다.")
    val money: Int,
    val userRole: UserRole?,
) {
    fun toEntity() =
        User(
            userName = this.userName,
            money = this.money,
            userRole = this.userRole ?: UserRole.ROLE_USER,
        )
}
