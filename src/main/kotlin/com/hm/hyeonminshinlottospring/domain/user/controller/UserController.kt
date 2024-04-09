package com.hm.hyeonminshinlottospring.domain.user.controller

import com.hm.hyeonminshinlottospring.domain.user.dto.UserCreateRequest
import com.hm.hyeonminshinlottospring.domain.user.dto.UserMoneyPatchRequest
import com.hm.hyeonminshinlottospring.domain.user.dto.UserResponse
import com.hm.hyeonminshinlottospring.domain.user.service.UserService
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

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
    ): ResponseEntity<Void> {
        val userId = userService.createUser(userCreateRequest)
        return ResponseEntity.created(URI.create("/api/v1/user/$userId")).build()
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

    @PatchMapping("/addMoney")
    fun addUserMoney(
        @Valid
        @RequestBody
        userMoneyPatchRequest: UserMoneyPatchRequest,
    ): ResponseEntity<Void> {
        userService.addUserMoney(userMoneyPatchRequest)
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/withdrawMoney")
    fun withdrawUserMoney(
        @Valid
        @RequestBody
        userMoneyPatchRequest: UserMoneyPatchRequest,
    ): ResponseEntity<Void> {
        userService.withdrawUserMoney(userMoneyPatchRequest)
        return ResponseEntity.noContent().build()
    }
}
