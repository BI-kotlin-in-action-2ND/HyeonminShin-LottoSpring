package com.hm.hyeonminshinlottospring.domain.user.service

import com.hm.hyeonminshinlottospring.domain.user.domain.User
import com.hm.hyeonminshinlottospring.domain.user.domain.UserRole
import com.hm.hyeonminshinlottospring.domain.user.dto.UserCreateRequest
import com.hm.hyeonminshinlottospring.domain.user.dto.UserResponse
import com.hm.hyeonminshinlottospring.domain.user.repository.UserRepository
import com.hm.hyeonminshinlottospring.domain.user.repository.existsByUserId
import com.hm.hyeonminshinlottospring.domain.user.repository.getByUserId
import jakarta.persistence.EntityExistsException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    @Transactional
    fun createUser(request: UserCreateRequest): UserResponse {
        validateUserNameIsDuplicated(request)
        val user =
            request.toEntity(
                request.userName,
                request.money,
                UserRole.ROLE_USER,
            )
        val savedUser = userRepository.save(user)
        return getUserResponse(savedUser)
    }

    @Transactional(readOnly = true)
    fun getUserInformation(userId: Long): UserResponse {
        validateUserIdExistence(userId)
        val user = userRepository.getByUserId(userId)
        return getUserResponse(user)
    }

    @Transactional
    fun addUserMoney(
        userId: Long,
        addMoney: Int,
    ): Int {
        validateUserIdExistence(userId)
        val user = userRepository.getByUserId(userId)
        return user.addMoney(addMoney)
    }

    @Transactional
    fun withdrawUserMoney(
        userId: Long,
        withdrawMoney: Int,
    ): Int {
        validateUserIdExistence(userId)
        val user = userRepository.getByUserId(userId)
        return user.withdrawMoney(withdrawMoney)
    }

    private fun getUserResponse(savedUser: User) =
        UserResponse.from(
            savedUser.id,
            savedUser.userName,
            savedUser.userRole,
            savedUser.money,
        )

    private fun validateUserIdExistence(userId: Long) {
        if (!userRepository.existsByUserId(userId)) {
            throw NoSuchElementException("$userId: 해당 사용자가 존재하지 않습니다.")
        }
    }

    private fun validateUserNameIsDuplicated(request: UserCreateRequest) {
        if (userRepository.existsByUserName(request.userName)) {
            throw EntityExistsException("${request.userName}: 이미 존재하는 유저 이름입니다.")
        }
    }
}
