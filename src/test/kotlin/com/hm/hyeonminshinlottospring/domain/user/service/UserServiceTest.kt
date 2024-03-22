package com.hm.hyeonminshinlottospring.domain.user.service

import com.hm.hyeonminshinlottospring.domain.user.repository.UserRepository
import com.hm.hyeonminshinlottospring.domain.user.repository.findByUserId
import com.hm.hyeonminshinlottospring.support.TEST_INVALID_MONEY
import com.hm.hyeonminshinlottospring.support.TEST_MONEY_10
import com.hm.hyeonminshinlottospring.support.TEST_USER_ID
import com.hm.hyeonminshinlottospring.support.createUser
import com.hm.hyeonminshinlottospring.support.createUserCreateRequest
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.mockk

class UserServiceTest : DescribeSpec(
    {
        val userRepository = mockk<UserRepository>()
        val userService = UserService(userRepository)

        describe("createUser") {
            context("유효한 데이터가 주어진 경우") {
                val user = createUser()
                val userCreateRequest = createUserCreateRequest(user)
                every { userRepository.save(any()) } returns user
                it("정상 종료한다") {
                    shouldNotThrowAny {
                        userService.createUser(userCreateRequest)
                    }
                }
            }
        }

        describe("getUserInformation") {
            context("존재하는 유저 ID가 주어진 경우") {
                every { userRepository.findByUserId(any()) } returns createUser()
                it("정상 종료한다") {
                    shouldNotThrowAny {
                        userService.getUserInformation(TEST_USER_ID)
                    }
                }
            }
        }

        describe("addUserMoney") {
            val user = createUser()
            context("유효한 데이터가 주어진 경우") {
                every { userRepository.findByUserId(any()) } returns user
                it("정상 종료한다") {
                    shouldNotThrowAny {
                        userService.addUserMoney(TEST_USER_ID, TEST_MONEY_10)
                    }
                }
            }

            context("돈이 0 이하로 주어진 경우") {
                every { userRepository.findByUserId(any()) } returns user
                it("[IllegalArgumentException] 예외 발생한다") {
                    shouldThrowExactly<IllegalArgumentException> {
                        userService.addUserMoney(TEST_USER_ID, TEST_INVALID_MONEY)
                    }
                }
            }
        }

        describe("withdrawUserMoney") {
            val user = createUser()
            context("유효한 데이터가 주어진 경우") {
                every { userRepository.findByUserId(any()) } returns user
                it("정상 종료한다") {
                    shouldNotThrowAny {
                        userService.withdrawUserMoney(TEST_USER_ID, TEST_MONEY_10)
                    }
                }
            }

            context("돈이 0 이하로 주어진 경우") {
                every { userRepository.findByUserId(any()) } returns user
                it("[IllegalArgumentException] 예외 발생한다") {
                    shouldThrowExactly<IllegalArgumentException> {
                        userService.withdrawUserMoney(TEST_USER_ID, TEST_INVALID_MONEY)
                    }
                }
            }

            context("소지한 금액 이상으로 주어진 경우") {
                every { userRepository.findByUserId(any()) } returns user
                it("[IllegalArgumentException] 예외 발생한다") {
                    shouldThrowExactly<IllegalArgumentException> {
                        userService.withdrawUserMoney(TEST_USER_ID, user.money + TEST_MONEY_10)
                    }
                }
            }
        }
    },
)
