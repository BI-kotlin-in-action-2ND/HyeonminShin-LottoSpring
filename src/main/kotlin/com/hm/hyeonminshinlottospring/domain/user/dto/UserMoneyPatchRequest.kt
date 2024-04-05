package com.hm.hyeonminshinlottospring.domain.user.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class UserMoneyPatchRequest(
    @field:NotNull(message = "유저 ID는 필수 입력 값입니다.")
    @field:Positive(message = "유저 ID는 0보다 커야 합니다.")
    val userId: Long,
    @field:NotNull(message = "투입할 돈은 필수 입력 값입니다.")
    @field:Positive(message = "투입할 돈은 양수여야 합니다.")
    val money: Int,
)
