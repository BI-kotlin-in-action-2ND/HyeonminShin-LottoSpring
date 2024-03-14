package com.hm.hyeonminshinlottospring.domain.user.repository

import com.hm.hyeonminshinlottospring.domain.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

fun UserRepository.existsByUserId(userId: Long): Boolean = existsById(userId)

fun UserRepository.getByUserId(userId: Long): User = findByIdOrNull(userId) ?: throw NoSuchElementException("$userId: 사용자가 존재하지 않습니다.")

fun UserRepository.getByUserName(userName: String): User =
    findUserByUserName(userName) ?: throw NoSuchElementException("$userName: 사용자가 존재하지 않습니다.")

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun existsByUserName(userName: String): Boolean

    fun findUserByUserName(userName: String): User?
}
