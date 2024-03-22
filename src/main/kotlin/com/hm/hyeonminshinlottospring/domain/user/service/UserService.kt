package com.hm.hyeonminshinlottospring.domain.user.service

import com.hm.hyeonminshinlottospring.domain.user.dto.UserCreateRequest
import com.hm.hyeonminshinlottospring.domain.user.dto.UserResponse
import com.hm.hyeonminshinlottospring.domain.user.repository.UserRepository
import com.hm.hyeonminshinlottospring.domain.user.repository.findByUserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    // TODO: 나중엔 밖에서 Admin "만들지도, 접근하지도 못하도록" 인증 시스템 도입하기
    @Transactional
    fun createUser(request: UserCreateRequest): Long {
        val savedUser = userRepository.save(request.toEntity())
        return savedUser.id
    }

    @Transactional(readOnly = true)
    fun getUserInformation(userId: Long): UserResponse {
        val user = userRepository.findByUserId(userId)
        return UserResponse.from(user)
    }

    @Transactional
    fun addUserMoney(
        userId: Long,
        addMoney: Int,
    ): Int {
        val user = userRepository.findByUserId(userId)
        return user.addMoney(addMoney)
    }

    @Transactional
    fun withdrawUserMoney(
        userId: Long,
        withdrawMoney: Int,
    ): Int {
        val user = userRepository.findByUserId(userId)
        return user.withdrawMoney(withdrawMoney)
    }

    // TODO: User 삭제 추가
}
