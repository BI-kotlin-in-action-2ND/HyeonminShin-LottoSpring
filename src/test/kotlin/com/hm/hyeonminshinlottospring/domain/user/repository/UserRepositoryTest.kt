package com.hm.hyeonminshinlottospring.domain.user.repository

import com.hm.hyeonminshinlottospring.support.TEST_INVALID_USER_ID
import com.hm.hyeonminshinlottospring.support.TEST_NOT_EXIST_USERNAME
import com.hm.hyeonminshinlottospring.support.createUser
import com.hm.hyeonminshinlottospring.support.test.BaseTests.RepositoryTest
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe

@RepositoryTest
class UserRepositoryTest(
    private val userRepository: UserRepository,
) : ExpectSpec(
    {
        val user1 = userRepository.save(createUser())

        afterSpec {
            userRepository.deleteAll()
        }

        context("유저 ID 조회") {
            expect("유저 ID와 일치하는 유저 조회") {
                val result = userRepository.findByUserId(user1.id)
                result.userName shouldBe user1.userName
            }

            expect("유저 ID가 존재할 때") {
                val result = userRepository.existsById(user1.id)
                result shouldBe true
            }

            expect("유저 ID가 존재하지 않을 때") {
                val result = userRepository.existsById(TEST_INVALID_USER_ID)
                result shouldBe false
            }
        }

        context("유저 이름 조회") {
            expect("유저 이름과 일치하는 유저 조회") {
                shouldNotThrowAny { userRepository.findByUserName(user1.userName) }
            }

            expect("유저 이름이 존재할 때") {
                val result = userRepository.existsByUserName(user1.userName)
                result shouldBe true
            }

            expect("유저 이름이 존재하지 않을 때") {
                val result = userRepository.existsByUserName(TEST_NOT_EXIST_USERNAME)
                result shouldBe false
            }
        }
    },
)
