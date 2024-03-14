package com.hm.hyeonminshinlottospring.domain.user.controller

import com.hm.hyeonminshinlottospring.domain.user.dto.UserCreateRequest
import com.hm.hyeonminshinlottospring.domain.user.dto.UserResponse
import com.hm.hyeonminshinlottospring.domain.user.service.UserService
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserService,
) {
    @PostMapping
    fun createUser(
        @Valid
        @RequestBody
        userCreateRequest: UserCreateRequest,
    ): ResponseEntity<UserResponse> {
        val response = userService.createUser(userCreateRequest)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{userId}")
    fun getUserInformation(
        @Positive(message = "유저 ID는 0보다 커야 합니다.")
        @PathVariable("userId")
        userId: Long,
    ): ResponseEntity<UserResponse> {
        val response = userService.getUserInformation(userId)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{userId}/addMoney")
    fun addUserMoney(
        @Positive(message = "유저 ID는 0보다 커야 합니다.")
        @PathVariable("userId")
        userId: Long,
        @Positive(message = "더할 돈의 값은 양수여야 합니다.")
        @RequestParam("charge", defaultValue = "0", required = false)
        charge: Int,
    ): ResponseEntity<Void> {
        userService.addUserMoney(userId, charge)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{userId}/withdrawMoney")
    fun withdrawUserMoney(
        @Positive(message = "유저 ID는 0보다 커야 합니다.")
        @PathVariable("userId")
        userId: Long,
        @Positive(message = "출금할 돈의 값은 양수여야 합니다.")
        @RequestParam("charge", defaultValue = "0", required = false)
        charge: Int,
    ): ResponseEntity<Void> {
        userService.withdrawUserMoney(userId, charge)
        return ResponseEntity.noContent().build()
    }
}
