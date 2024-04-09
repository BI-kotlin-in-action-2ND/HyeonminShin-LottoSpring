package com.hm.hyeonminshinlottospring.domain.user.service

import com.hm.hyeonminshinlottospring.domain.user.dto.UserCreateRequest
import com.hm.hyeonminshinlottospring.domain.user.dto.UserMoneyPatchRequest
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
    fun createUser(request: UserCreateRequest): Long {
        val savedUser = userRepository.save(request.toEntity())
        return savedUser.id
    }

    fun getUserInformation(userId: Long): UserResponse {
        val user = userRepository.findByUserId(userId)
        return UserResponse.from(user)
    }

    @Transactional
    fun addUserMoney(request: UserMoneyPatchRequest): Int {
        val user = userRepository.findByUserId(request.userId)
        return user.addMoney(request.money)
    }

    @Transactional
    fun withdrawUserMoney(request: UserMoneyPatchRequest): Int {
        val user = userRepository.findByUserId(request.userId)
        return user.withdrawMoney(request.money)
    }

    // TODO: User 삭제 추가
}
