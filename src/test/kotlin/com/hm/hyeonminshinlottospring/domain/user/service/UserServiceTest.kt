package com.hm.hyeonminshinlottospring.domain.user.service

import com.hm.hyeonminshinlottospring.domain.user.domain.User
import com.hm.hyeonminshinlottospring.domain.user.repository.UserRepository
import com.hm.hyeonminshinlottospring.domain.user.repository.existsByUserId
import com.hm.hyeonminshinlottospring.domain.user.repository.getByUserId
import com.hm.hyeonminshinlottospring.support.TEST_MONEY_10
import com.hm.hyeonminshinlottospring.support.TEST_USER_ID
import com.hm.hyeonminshinlottospring.support.createUser
import com.hm.hyeonminshinlottospring.support.createUserCreateRequest
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import jakarta.persistence.EntityExistsException

class UserServiceTest : DescribeSpec(
    {
        val userMock = mockk<User>()
        val userRepository = mockk<UserRepository>()
        val userService = UserService(userRepository)

        describe("createUser") {
            context("유효한 데이터가 주어진 경우") {
                val user = createUser()
                val userCreateRequest =
                    createUserCreateRequest(
                        user.userName,
                        user.money,
                        user.userRole,
                    )
                every { userRepository.existsByUserName(any()) } returns false
                every { userRepository.save(any<User>()) } returns user
                it("정상 종료한다") {
                    shouldNotThrowAny {
                        userService.createUser(userCreateRequest)
                    }
                }
            }

            context("만들려는 유저 이름이 이미 존재하는 경우") {
                val user = createUser()
                val userCreateRequest =
                    createUserCreateRequest(
                        user.userName,
                        user.money,
                        user.userRole,
                    )
                every { userRepository.existsByUserName(any()) } returns false andThen true
                every { userRepository.save(any<User>()) } returns user
                it("[EntityExistsException] 예외 발생한다") {
                    shouldThrow<EntityExistsException> {
                        userService.createUser(userCreateRequest)
                        userService.createUser(userCreateRequest)
                    }
                }
            }
        }

        describe("getUserInformation") {
            context("존재하는 유저 ID가 주어진 경우") {
                every { userRepository.existsByUserId(any()) } returns true
                every { userRepository.getByUserId(any()) } returns createUser()
                it("정상 종료한다") {
                    shouldNotThrowAny {
                        userService.getUserInformation(TEST_USER_ID)
                    }
                }
            }

            context("존재하지 않는 유저 ID가 주어진 경우") {
                every { userRepository.existsByUserId(any()) } returns false
                every { userRepository.getByUserId(any()) } throws NoSuchElementException("사용자가 존재하지 않습니다.")
                it("[NoSuchElementException] 예외 발생한다") {
                    shouldThrowExactly<NoSuchElementException> {
                        userService.getUserInformation(TEST_USER_ID)
                    }
                }
            }
        }

        describe("addUserMoney") {
            context("존재하는 유저 ID가 주어진 경우") {
                every { userRepository.existsByUserId(any()) } returns true
                every { userRepository.getByUserId(any()) } returns createUser()
                it("정상 종료한다") {
                    shouldNotThrowAny {
                        userService.addUserMoney(TEST_USER_ID, TEST_MONEY_10)
                    }
                }
            }

            context("존재하지 않는 유저 ID가 주어진 경우") {
                every { userRepository.existsByUserId(any()) } returns false
                every { userRepository.getByUserId(any()) } throws NoSuchElementException("사용자가 존재하지 않습니다.")
                it("[NoSuchElementException] 예외 발생한다") {
                    shouldThrowExactly<NoSuchElementException> {
                        userService.addUserMoney(TEST_USER_ID, TEST_MONEY_10)
                    }
                }
            }

            context("돈이 0 이하로 주어진 경우") {
                every { userRepository.existsByUserId(any()) } returns true
                every { userRepository.getByUserId(any()) } returns userMock
                every { userMock.addMoney(any()) } throws IllegalArgumentException("")
                it("[IllegalArgumentException] 예외 발생한다") {
                    shouldThrowExactly<IllegalArgumentException> {
                        userService.addUserMoney(TEST_USER_ID, TEST_MONEY_10)
                    }
                }
            }
        }

        describe("withdrawUserMoney") {
            context("존재하는 유저 ID가 주어진 경우") {
                every { userRepository.existsByUserId(any()) } returns true
                every { userRepository.getByUserId(any()) } returns createUser()
                it("정상 종료한다") {
                    shouldNotThrowAny {
                        userService.withdrawUserMoney(TEST_USER_ID, TEST_MONEY_10)
                    }
                }
            }

            context("존재하지 않는 유저 ID가 주어진 경우") {
                every { userRepository.existsByUserId(any()) } returns false
                every { userRepository.getByUserId(any()) } throws NoSuchElementException("사용자가 존재하지 않습니다.")
                it("[NoSuchElementException] 예외 발생한다") {
                    shouldThrowExactly<NoSuchElementException> {
                        userService.withdrawUserMoney(TEST_USER_ID, TEST_MONEY_10)
                    }
                }
            }

            context("돈이 0 이하로 주어진 경우") {
                every { userRepository.existsByUserId(any()) } returns true
                every { userRepository.getByUserId(any()) } returns userMock
                every { userMock.withdrawMoney(any()) } throws IllegalArgumentException("")
                it("[IllegalArgumentException] 예외 발생한다") {
                    shouldThrowExactly<IllegalArgumentException> {
                        userService.withdrawUserMoney(TEST_USER_ID, TEST_MONEY_10)
                    }
                }
            }
        }
    },
)
