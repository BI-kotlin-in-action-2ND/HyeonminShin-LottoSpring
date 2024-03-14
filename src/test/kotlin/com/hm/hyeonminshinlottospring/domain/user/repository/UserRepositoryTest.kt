package com.hm.hyeonminshinlottospring.domain.user.repository

import com.hm.hyeonminshinlottospring.domain.user.domain.User
import com.hm.hyeonminshinlottospring.support.TEST_NOT_EXIST_USERNAME
import com.hm.hyeonminshinlottospring.support.TEST_NOT_EXIST_USER_ID
import com.hm.hyeonminshinlottospring.support.TEST_USERNAME
import com.hm.hyeonminshinlottospring.support.createUser
import com.hm.hyeonminshinlottospring.support.test.BaseTests.IntegrationTest
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.transaction.annotation.Transactional

@IntegrationTest
@Transactional
class UserRepositoryTest(
    private val userRepository: UserRepository,
) : ExpectSpec(
        {
            lateinit var user1: User

            beforeEach {
                user1 = userRepository.save(createUser())
            }

            context("유저 ID 조회") {
                expect("유저 ID와 일치하는 유저 조회") {
                    val result: User? = user1.id?.let { userRepository.getByUserId(it) }
                    result?.userName shouldBe TEST_USERNAME
                }

                expect("유저 ID가 존재할 때") {
                    val result = user1.id?.let { userRepository.existsByUserId(it) }
                    result shouldBe true
                }

                expect("유저 ID가 존재하지 않을 때") {
                    val result = userRepository.existsByUserId(TEST_NOT_EXIST_USER_ID)
                    result shouldBe false
                }
            }

            context("유저 이름 조회") {
                expect("유저 이름과 일치하는 유저 조회") {
                    shouldNotThrowAny { userRepository.getByUserName(user1.userName) }
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

            context("유저 저장") {
                // TODO: 로그인 세션 기능이 생기면 로그인 ID로 유저 특정할 예정. 그땐 아래 테스트 삭제!!
                expect("이미 저장되어 있는 유저를 저장하려 할 때") {
                    val sameUser1 = createUser()
                    shouldThrowExactly<DataIntegrityViolationException> {
                        userRepository.save(sameUser1)
                    }
                }
            }
        },
    )
