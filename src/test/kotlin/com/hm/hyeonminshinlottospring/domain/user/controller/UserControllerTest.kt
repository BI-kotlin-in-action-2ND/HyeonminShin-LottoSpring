package com.hm.hyeonminshinlottospring.domain.user.controller

import com.hm.hyeonminshinlottospring.domain.lotto.domain.info.LottoPrice
import com.hm.hyeonminshinlottospring.domain.user.service.UserService
import com.hm.hyeonminshinlottospring.support.CHARGE_PARAM
import com.hm.hyeonminshinlottospring.support.TEST_INVALID_MONEY
import com.hm.hyeonminshinlottospring.support.TEST_INVALID_USER_ID
import com.hm.hyeonminshinlottospring.support.TEST_MONEY_10
import com.hm.hyeonminshinlottospring.support.TEST_USER_ID
import com.hm.hyeonminshinlottospring.support.createUser
import com.hm.hyeonminshinlottospring.support.createUserCreateRequest
import com.hm.hyeonminshinlottospring.support.createUserResponse
import com.hm.hyeonminshinlottospring.support.test.BaseTests.UnitControllerTestEnvironment
import com.hm.hyeonminshinlottospring.support.test.ControllerTestHelper.Companion.jsonContent
import com.hm.hyeonminshinlottospring.support.test.RestDocsHelpler
import com.hm.hyeonminshinlottospring.support.test.RestDocsHelpler.Companion.createDocument
import com.hm.hyeonminshinlottospring.support.test.RestDocsHelpler.Companion.createPathDocument
import com.hm.hyeonminshinlottospring.support.test.RestDocsHelpler.Companion.requestBody
import com.hm.hyeonminshinlottospring.support.test.RestDocsHelpler.Companion.responseBody
import com.hm.hyeonminshinlottospring.support.test.example
import com.hm.hyeonminshinlottospring.support.test.parameterDescription
import com.hm.hyeonminshinlottospring.support.test.pathDescription
import com.hm.hyeonminshinlottospring.support.test.type
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.restdocs.ManualRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.context.WebApplicationContext

@UnitControllerTestEnvironment
@WebMvcTest(UserController::class)
class UserControllerTest(
    private val context: WebApplicationContext,
    @MockkBean private val userService: UserService,
) : DescribeSpec(
    {
        val restDocumentation = ManualRestDocumentation()
        val restDocMockMvc = RestDocsHelpler.generateRestDocMvc(context, restDocumentation)

        beforeEach {
            restDocumentation.beforeTest(javaClass, it.name.testName)
        }

        describe("POST /api/v1/user") {
            val targetUri = "/api/v1/user"
            context("유효한 요청인 경우") {
                val user = createUser()
                val request = createUserCreateRequest(user)
                every { userService.createUser(request) } returns user.id
                it("201 응답한다.") {
                    restDocMockMvc.post(targetUri) {
                        jsonContent(request)
                    }.andExpect {
                        status { isCreated() }
                    }.andDo {
                        createDocument(
                            "create-user-success",
                            requestBody(
                                "userName" type JsonFieldType.STRING description "유저 이름" example request.userName,
                                "money" type JsonFieldType.NUMBER description "충전할 돈" example request.money,
                                "userRole" type JsonFieldType.STRING description "유저 역할" example request.userRole,
                            ),
                        )
                    }
                }
            }

            context("유저 이름이 유효하지 않은 경우") {
                val user = createUser(userName = " ")
                val request = createUserCreateRequest(user)
                it("400 응답한다.") {
                    restDocMockMvc.post(targetUri) {
                        jsonContent(request)
                    }.andExpect {
                        status { isBadRequest() }
                    }.andDo {
                        createDocument(
                            "create-user-fail-user-name-blank",
                            requestBody(
                                "userName" type JsonFieldType.STRING description "공백인 유저 이름" example " ",
                                "money" type JsonFieldType.NUMBER description "충전할 돈" example request.money,
                                "userRole" type JsonFieldType.STRING description "유저 역할" example request.userRole,
                            ),
                        )
                    }
                }
            }

            context("돈이 음수로 들어온 경우") {
                val user = createUser(money = TEST_INVALID_MONEY)
                val request = createUserCreateRequest(user)
                it("400 응답한다.") {
                    restDocMockMvc.post(targetUri) {
                        jsonContent(request)
                    }.andExpect {
                        status { isBadRequest() }
                    }.andDo {
                        createDocument(
                            "create-user-fail-user-name-blank",
                            requestBody(
                                "userName" type JsonFieldType.STRING description "유저 이름" example request.userName,
                                "money" type JsonFieldType.NUMBER description "0 이하의 음수로 주어진 충전할 돈" example TEST_INVALID_MONEY,
                                "userRole" type JsonFieldType.STRING description "유저 역할" example request.userRole,
                            ),
                        )
                    }
                }
            }
        }

        describe("GET /api/v1/user/{userId}") {
            val targetUri = "/api/v1/user/{userId}"
            context("유효한 유저 ID가 주어진 경우") {
                val user = createUser()
                val response = createUserResponse(user)
                every { userService.getUserInformation(user.id) } returns response
                it("200 응답한다.") {
                    restDocMockMvc.perform(
                        RestDocumentationRequestBuilders
                            .get(targetUri, TEST_USER_ID),
                    ).andExpect {
                        status().isOk
                    }.andDo {
                        createPathDocument(
                            "get-user-information-success",
                            pathParameters(
                                "userId" pathDescription "조회할 유저 ID" example TEST_USER_ID,
                            ),
                            responseBody(
                                "userName" type JsonFieldType.STRING description "유저 이름" example response.userName,
                                "userRole" type JsonFieldType.STRING description "유저 역할" example response.userRole,
                                "money" type JsonFieldType.NUMBER description "소지한 금액" example response.money,
                            ),
                        )
                    }
                }
            }

            context("유저 ID가 음수로 주어진 경우") {
                it("400 응답한다.") {
                    restDocMockMvc.perform(
                        RestDocumentationRequestBuilders
                            .get(targetUri, TEST_INVALID_USER_ID),
                    ).andExpect {
                        status().isBadRequest
                    }.andDo {
                        createPathDocument(
                            "get-user-information-fail-user-id-negative",
                            pathParameters(
                                "userId" pathDescription "음수의 유저 ID" example TEST_INVALID_USER_ID,
                            ),
                        )
                    }
                }
            }

            context("유저 ID가 존재하지 않을 경우") {
                every { userService.getUserInformation(TEST_USER_ID) } throws NoSuchElementException("${TEST_USER_ID}: 사용자가 존재하지 않습니다.")
                it("404 응답한다.") {
                    restDocMockMvc.perform(
                        RestDocumentationRequestBuilders
                            .get(targetUri, TEST_USER_ID),
                    ).andExpect {
                        status().isNotFound
                    }.andDo {
                        createPathDocument(
                            "get-user-information-fail-user-id-not-exist",
                            pathParameters(
                                "userId" pathDescription "존재하지 않는 유저 ID" example TEST_USER_ID,
                            ),
                        )
                    }
                }
            }
        }

        describe("PATCH /api/v1/user/{userId}/addMoney") {
            val targetUri = "/api/v1/user/{userId}/addMoney"
            context("유효한 유저 ID가 주어진 경우") {
                every { userService.addUserMoney(any(), any()) } returns 1
                it("204 응답한다.") {
                    restDocMockMvc.perform(
                        RestDocumentationRequestBuilders
                            .patch(targetUri, TEST_USER_ID)
                            .param("charge", TEST_MONEY_10.toString()),
                    ).andExpect {
                        status().isNoContent
                    }.andDo {
                        createPathDocument(
                            "add-user-money-success",
                            pathParameters(
                                "userId" pathDescription "조회할 유저 ID" example TEST_USER_ID,
                            ),
                            queryParameters(
                                CHARGE_PARAM parameterDescription "충전할 금액" example TEST_MONEY_10,
                            ),
                        )
                    }
                }
            }

            context("유저 ID가 음수로 주어진 경우") {
                it("400 응답한다.") {
                    restDocMockMvc.perform(
                        RestDocumentationRequestBuilders
                            .patch(targetUri, TEST_INVALID_USER_ID)
                            .param("charge", TEST_MONEY_10.toString()),
                    ).andExpect {
                        status().isBadRequest
                    }.andDo {
                        createPathDocument(
                            "add-user-money-fail-user-id-negative",
                            pathParameters(
                                "userId" pathDescription "음수의 유저 ID" example TEST_INVALID_USER_ID,
                            ),
                        )
                    }
                }
            }

            context("유저 ID가 존재하지 않을 경우") {
                every { userService.addUserMoney(TEST_USER_ID, TEST_MONEY_10) } throws NoSuchElementException("$TEST_USER_ID: 사용자가 존재하지 않습니다.")
                it("404 응답한다.") {
                    restDocMockMvc.perform(
                        RestDocumentationRequestBuilders
                            .patch(targetUri, TEST_USER_ID)
                            .param("charge", TEST_MONEY_10.toString()),
                    ).andExpect {
                        status().isNotFound
                    }.andDo {
                        createPathDocument(
                            "add-user-money-fail-user-id-not-exist",
                            pathParameters(
                                "userId" pathDescription "존재하지 않는 유저 ID" example TEST_USER_ID,
                            ),
                        )
                    }
                }
            }

            context("충전할 돈이 음수로 주어진 경우") {
                it("400 응답한다.") {
                    restDocMockMvc.perform(
                        RestDocumentationRequestBuilders
                            .patch(targetUri, TEST_USER_ID)
                            .param("charge", TEST_MONEY_10.toString()),
                    ).andExpect {
                        status().isBadRequest
                    }.andDo {
                        createPathDocument(
                            "add-user-money-fail-charge-negative",
                            pathParameters(
                                "userId" pathDescription "조회할 유저 ID" example TEST_USER_ID,
                            ),
                            queryParameters(
                                CHARGE_PARAM parameterDescription "음수인 충전할 금액" example TEST_INVALID_MONEY,
                            ),
                        )
                    }
                }
            }
        }

        describe("PATCH /api/v1/user/{userId}/withdrawMoney") {
            val targetUri = "/api/v1/user/{userId}/withdrawMoney"
            context("유효한 유저 ID가 주어진 경우") {
                every { userService.withdrawUserMoney(any(), any()) } returns 1
                it("204 응답한다.") {
                    restDocMockMvc.perform(
                        RestDocumentationRequestBuilders
                            .patch(targetUri, TEST_USER_ID)
                            .param("charge", TEST_MONEY_10.toString()),
                    ).andExpect {
                        status().isNoContent
                    }.andDo {
                        createPathDocument(
                            "withdraw-user-money-success",
                            pathParameters(
                                "userId" pathDescription "조회할 유저 ID" example TEST_USER_ID,
                            ),
                            queryParameters(
                                CHARGE_PARAM parameterDescription "출금액" example TEST_MONEY_10,
                            ),
                        )
                    }
                }
            }

            context("유저 ID가 음수로 주어진 경우") {
                it("400 응답한다.") {
                    restDocMockMvc.perform(
                        RestDocumentationRequestBuilders
                            .patch(targetUri, TEST_INVALID_USER_ID)
                            .param("charge", TEST_MONEY_10.toString()),
                    ).andExpect {
                        status().isBadRequest
                    }.andDo {
                        createPathDocument(
                            "withdraw-user-money-fail-user-id-negative",
                            pathParameters(
                                "userId" pathDescription "음수의 유저 ID" example TEST_INVALID_USER_ID,
                            ),
                        )
                    }
                }
            }

            context("유저 ID가 존재하지 않을 경우") {
                every { userService.withdrawUserMoney(TEST_USER_ID, TEST_MONEY_10) } throws NoSuchElementException("$TEST_USER_ID: 사용자가 존재하지 않습니다.")
                it("404 응답한다.") {
                    restDocMockMvc.perform(
                        RestDocumentationRequestBuilders
                            .patch(targetUri, TEST_USER_ID)
                            .param("charge", TEST_MONEY_10.toString()),
                    ).andExpect {
                        status().isNotFound
                    }.andDo {
                        createPathDocument(
                            "withdraw-user-money-fail-user-id-not-exist",
                            pathParameters(
                                "userId" pathDescription "존재하지 않는 유저 ID" example TEST_USER_ID,
                            ),
                        )
                    }
                }
            }

            context("출금할 돈이 음수로 주어진 경우") {
                it("400 응답한다.") {
                    restDocMockMvc.perform(
                        RestDocumentationRequestBuilders
                            .patch(targetUri, TEST_USER_ID)
                            .param("charge", TEST_MONEY_10.toString()),
                    ).andExpect {
                        status().isBadRequest
                    }.andDo {
                        createPathDocument(
                            "withdraw-user-money-fail-charge-negative",
                            pathParameters(
                                "userId" pathDescription "조회할 유저 ID" example TEST_USER_ID,
                            ),
                            queryParameters(
                                CHARGE_PARAM parameterDescription "음수인 출금액" example TEST_INVALID_MONEY,
                            ),
                        )
                    }
                }
            }

            context("출금할 돈이 소지한 금액보다 크게 주어진 경우") {
                every {
                    userService.withdrawUserMoney(TEST_USER_ID, TEST_MONEY_10 + 1)
                } throws IllegalArgumentException("$TEST_USER_ID: 현재 소지한 금액($TEST_MONEY_10${LottoPrice.UNIT})보다 적은 금액을 입력해주세요.")
                it("400 응답한다.") {
                    restDocMockMvc.perform(
                        RestDocumentationRequestBuilders
                            .patch(targetUri, TEST_USER_ID)
                            .param("charge", (TEST_MONEY_10 + 1).toString()),
                    ).andExpect {
                        status().isBadRequest
                    }.andDo {
                        createPathDocument(
                            "withdraw-user-money-fail-charge-bigger-than-having",
                            pathParameters(
                                "userId" pathDescription "조회할 유저 ID" example TEST_USER_ID,
                            ),
                            queryParameters(
                                CHARGE_PARAM parameterDescription "가진 금액보다 큰 출금액" example (TEST_MONEY_10 + 1),
                            ),
                        )
                    }
                }
            }
        }

        afterTest {
            restDocumentation.afterTest()
        }
    },
)
