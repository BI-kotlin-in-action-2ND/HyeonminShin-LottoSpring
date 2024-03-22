package com.hm.hyeonminshinlottospring.domain.winninglotto.cotroller

import com.hm.hyeonminshinlottospring.domain.winninglotto.service.WinningLottoService
import com.hm.hyeonminshinlottospring.support.TEST_ADMIN_USER_ID
import com.hm.hyeonminshinlottospring.support.TEST_INVALID_ROUND
import com.hm.hyeonminshinlottospring.support.TEST_INVALID_USER_ID
import com.hm.hyeonminshinlottospring.support.TEST_NOT_EXIST_ROUND
import com.hm.hyeonminshinlottospring.support.TEST_ROUND
import com.hm.hyeonminshinlottospring.support.TEST_USER_ID
import com.hm.hyeonminshinlottospring.support.USER_ID_PARAM
import com.hm.hyeonminshinlottospring.support.createAdmin
import com.hm.hyeonminshinlottospring.support.createLotto
import com.hm.hyeonminshinlottospring.support.createTotalMatchResponse
import com.hm.hyeonminshinlottospring.support.createWinningLotto
import com.hm.hyeonminshinlottospring.support.createWinningLottoRoundResponse
import com.hm.hyeonminshinlottospring.support.test.BaseTests.UnitControllerTestEnvironment
import com.hm.hyeonminshinlottospring.support.test.RestDocsHelpler
import com.hm.hyeonminshinlottospring.support.test.RestDocsHelpler.Companion.createPathDocument
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.context.WebApplicationContext

@UnitControllerTestEnvironment
@WebMvcTest(WinningLottoController::class)
class WinningLottoControllerTest(
    private val context: WebApplicationContext,
    @MockkBean private val winningLottoService: WinningLottoService,
) : DescribeSpec(
    {
        val restDocumentation = ManualRestDocumentation()
        val restDocMockMvc = RestDocsHelpler.generateRestDocMvc(context, restDocumentation)

        beforeEach {
            restDocumentation.beforeTest(javaClass, it.name.testName)
        }

        describe("GET /api/v1/winninglotto/{round}") {
            val targetUri = "/api/v1/winninglotto/{round}"
            context("현재 라운드면서, Admin인 경우") {
                val admin = createAdmin()
                val lotto = createLotto(user = admin)
                val winningLotto = createWinningLotto(lotto)
                val response = createWinningLottoRoundResponse(winningLotto)
                every { winningLottoService.getWinningLottoByRound(TEST_ADMIN_USER_ID, TEST_ROUND) } returns response
                it("200 응답한다.") {
                    restDocMockMvc.perform(
                        RestDocumentationRequestBuilders
                            .get(targetUri, TEST_ROUND)
                            .param(USER_ID_PARAM, TEST_ADMIN_USER_ID.toString()),
                    ).andExpect {
                        status().isOk
                    }.andDo {
                        createPathDocument(
                            "get-winning-lotto-round-success-admin-current-round",
                            pathParameters(
                                "round" pathDescription "조회할 라운드 정보" example TEST_ROUND,
                            ),
                            queryParameters(
                                USER_ID_PARAM parameterDescription "조회하려는 Admin 유저 ID" example TEST_ADMIN_USER_ID,
                            ),
                            responseBody(
                                "round" type JsonFieldType.NUMBER description "조회한 라운드 정보" example TEST_ROUND,
                                "numbers" type JsonFieldType.ARRAY description "해당 라운드의 당첨 번호 리스트" example lotto.numbers,
                            ),
                        )
                    }
                }
            }

            context("현재 진행중인 라운드 이전 라운드면서, 일반 유저인 경우") {
                val admin = createAdmin()
                val lotto = createLotto(user = admin)
                val winningLotto = createWinningLotto(lotto)
                val response = createWinningLottoRoundResponse(winningLotto)
                every { winningLottoService.getWinningLottoByRound(TEST_USER_ID, TEST_ROUND) } returns response
                it("200 응답한다.") {
                    restDocMockMvc.perform(
                        RestDocumentationRequestBuilders
                            .get(targetUri, TEST_ROUND)
                            .param(USER_ID_PARAM, TEST_USER_ID.toString()),
                    ).andExpect {
                        status().isOk
                    }.andDo {
                        createPathDocument(
                            "get-winning-lotto-round-success-user-not-current-round",
                            pathParameters(
                                "round" pathDescription "조회할 라운드 정보(현재 진행중인 라운드가 아님)" example TEST_ROUND,
                            ),
                            queryParameters(
                                USER_ID_PARAM parameterDescription "조회하려는 일반 유저 ID" example TEST_USER_ID,
                            ),
                            responseBody(
                                "round" type JsonFieldType.NUMBER description "조회한 라운드 정보" example TEST_ROUND,
                                "numbers" type JsonFieldType.ARRAY description "해당 라운드의 당첨 번호 리스트" example lotto.numbers,
                            ),
                        )
                    }
                }
            }

            context("현재 진행중인 라운드를 조회하는 일반 유저인 경우") {
                every {
                    winningLottoService.getWinningLottoByRound(
                        TEST_USER_ID,
                        TEST_ROUND,
                    )
                } throws IllegalArgumentException("현재 진행중인 라운드는 조회할 수 없습니다.")
                it("400 응답한다.") {
                    restDocMockMvc.perform(
                        RestDocumentationRequestBuilders
                            .get(targetUri, TEST_ROUND)
                            .param(USER_ID_PARAM, TEST_USER_ID.toString()),
                    ).andExpect {
                        status().isBadRequest
                    }.andDo {
                        createPathDocument(
                            "get-winning-lotto-round-fail-user-current-round",
                            pathParameters(
                                "round" pathDescription "조회할 라운드 정보(현재 진행중인 라운드!)" example TEST_ROUND,
                            ),
                            queryParameters(
                                USER_ID_PARAM parameterDescription "조회하려는 일반 유저 ID" example TEST_USER_ID,
                            ),
                        )
                    }
                }
            }

            context("음수의 유저 ID가 주어진 경우") {
                it("400 응답한다.") {
                    restDocMockMvc.perform(
                        RestDocumentationRequestBuilders
                            .get(targetUri, TEST_ROUND)
                            .param(USER_ID_PARAM, TEST_INVALID_USER_ID.toString()),
                    ).andExpect {
                        status().isBadRequest
                    }.andDo {
                        createPathDocument(
                            "get-winning-lotto-round-fail-user-id-negative",
                            pathParameters(
                                "round" pathDescription "조회할 라운드 정보(현재 진행중인 라운드가 아님)" example TEST_ROUND,
                            ),
                            queryParameters(
                                USER_ID_PARAM parameterDescription "음수의 유저 ID" example TEST_INVALID_USER_ID,
                            ),
                        )
                    }
                }
            }

            context("음수의 라운드가 주어진 경우") {
                it("400 응답한다.") {
                    restDocMockMvc.perform(
                        RestDocumentationRequestBuilders
                            .get(targetUri, TEST_INVALID_ROUND)
                            .param(USER_ID_PARAM, TEST_USER_ID.toString()),
                    ).andExpect {
                        status().isBadRequest
                    }.andDo {
                        createPathDocument(
                            "get-winning-lotto-round-fail-round-negative",
                            pathParameters(
                                "round" pathDescription "음수의 조회할 라운드 정보" example TEST_INVALID_ROUND,
                            ),
                            queryParameters(
                                USER_ID_PARAM parameterDescription "유저 ID" example TEST_USER_ID,
                            ),
                        )
                    }
                }
            }

            context("존재하지 않는 라운드가 주어진 경우") {
                every {
                    winningLottoService.getWinningLottoByRound(
                        TEST_USER_ID,
                        TEST_NOT_EXIST_ROUND,
                    )
                } throws NoSuchElementException("$TEST_NOT_EXIST_ROUND 라운드에 해당하는 당첨 번호가 존재하지 않습니다.")
                it("404 응답한다.") {
                    restDocMockMvc.perform(
                        RestDocumentationRequestBuilders
                            .get(targetUri, TEST_NOT_EXIST_ROUND)
                            .param(USER_ID_PARAM, TEST_USER_ID.toString()),
                    ).andExpect {
                        status().isNotFound
                    }.andDo {
                        createPathDocument(
                            "get-winning-lotto-round-fail-round-not-exist",
                            pathParameters(
                                "round" pathDescription "존재하지 않는 라운드 정보" example TEST_NOT_EXIST_ROUND,
                            ),
                            queryParameters(
                                USER_ID_PARAM parameterDescription "유저 ID" example TEST_USER_ID,
                            ),
                        )
                    }
                }
            }
        }

        describe("GET /api/v1/winninglotto/{round}/match") {
            val targetUri = "/api/v1/winninglotto/{round}/match"
            context("유효한 데이터가 주어진 경우") {
                val response = createTotalMatchResponse()
                every { winningLottoService.matchUserLottoByRound(TEST_USER_ID, TEST_ROUND) } returns response
                it("200 응답한다.") {
                    restDocMockMvc.perform(
                        RestDocumentationRequestBuilders
                            .get(targetUri, TEST_ROUND)
                            .param(USER_ID_PARAM, TEST_USER_ID.toString()),
                    ).andExpect {
                        status().isOk
                    }.andDo {
                        createPathDocument(
                            "match-user-lotto-success",
                            pathParameters(
                                "round" pathDescription "조회할 라운드 정보(현재 진행중인 라운드 아님!)" example TEST_ROUND,
                            ),
                            queryParameters(
                                USER_ID_PARAM parameterDescription "해당 라운드와 매치할 일반 유저 ID" example TEST_USER_ID,
                            ),
                            responseBody(
                                "size" type JsonFieldType.NUMBER description "당첨된 로또 개수" example response.size,
                                "matchResult[].numbers" type JsonFieldType.ARRAY description "당첨된 유저의 로또 번호" example response.matchResult[0].numbers,
                                "matchResult[].matched" type JsonFieldType.ARRAY description "해당 라운드의 당첨 번호와 겹치는 로또 번호" example response.matchResult[0].matched,
                                "matchResult[].rankString" type JsonFieldType.STRING description "등수 문자열" example response.matchResult[0].rankString,
                                "matchResult[].prize" type JsonFieldType.NUMBER description "얻은 상금" example response.matchResult[0].prize,
                            ),
                        )
                    }
                }
            }

            context("음수의 유저 ID가 주어진 경우") {
                it("400 응답한다.") {
                    restDocMockMvc.perform(
                        RestDocumentationRequestBuilders
                            .get(targetUri, TEST_ROUND)
                            .param(USER_ID_PARAM, TEST_INVALID_USER_ID.toString()),
                    ).andExpect {
                        status().isBadRequest
                    }.andDo {
                        createPathDocument(
                            "match-user-lotto-fail-user-id-negative",
                            pathParameters(
                                "round" pathDescription "조회할 라운드 정보(현재 진행중인 라운드 아님!)" example TEST_ROUND,
                            ),
                            queryParameters(
                                USER_ID_PARAM parameterDescription "음수의 유저 ID" example TEST_INVALID_USER_ID,
                            ),
                        )
                    }
                }
            }

            context("음수의 라운드가 주어진 경우") {
                it("400 응답한다.") {
                    restDocMockMvc.perform(
                        RestDocumentationRequestBuilders
                            .get(targetUri, TEST_INVALID_ROUND)
                            .param(USER_ID_PARAM, TEST_USER_ID.toString()),
                    ).andExpect {
                        status().isBadRequest
                    }.andDo {
                        createPathDocument(
                            "match-user-lotto-fail-round-negative",
                            pathParameters(
                                "round" pathDescription "음수의 라운드 정보" example TEST_INVALID_ROUND,
                            ),
                            queryParameters(
                                USER_ID_PARAM parameterDescription "조회할 유저 ID" example TEST_USER_ID,
                            ),
                        )
                    }
                }
            }

            context("존재하지 않는 라운드가 주어진 경우") {
                every {
                    winningLottoService.matchUserLottoByRound(
                        TEST_USER_ID,
                        TEST_NOT_EXIST_ROUND,
                    )
                } throws IllegalArgumentException("존재하지 않는 라운드는 조회할 수 없습니다.")
                it("400 응답한다.") {
                    restDocMockMvc.perform(
                        RestDocumentationRequestBuilders
                            .get(targetUri, TEST_NOT_EXIST_ROUND)
                            .param(USER_ID_PARAM, TEST_USER_ID.toString()),
                    ).andExpect {
                        status().isBadRequest
                    }.andDo {
                        createPathDocument(
                            "match-user-lotto-fail-round-not-exist",
                            pathParameters(
                                "round" pathDescription "존재하지 않는 라운드 정보" example TEST_NOT_EXIST_ROUND,
                            ),
                            queryParameters(
                                USER_ID_PARAM parameterDescription "조회할 유저 ID" example TEST_USER_ID,
                            ),
                        )
                    }
                }
            }

            context("유효한 라운드지만, Admin 유저 ID가 주어진 경우") {
                every {
                    winningLottoService.matchUserLottoByRound(
                        TEST_ADMIN_USER_ID,
                        TEST_ROUND,
                    )
                } throws IllegalStateException("주어진 유저 ID는 매칭할 수 없습니다.")
                it("400 응답한다.") {
                    restDocMockMvc.perform(
                        RestDocumentationRequestBuilders
                            .get(targetUri, TEST_ROUND)
                            .param(USER_ID_PARAM, TEST_ADMIN_USER_ID.toString()),
                    ).andExpect {
                        status().isBadRequest
                    }.andDo {
                        createPathDocument(
                            "match-user-lotto-fail-user-id-admin",
                            pathParameters(
                                "round" pathDescription "조회할 라운드 정보" example TEST_ROUND,
                            ),
                            queryParameters(
                                USER_ID_PARAM parameterDescription "Admin 유저 ID" example TEST_ADMIN_USER_ID,
                            ),
                        )
                    }
                }
            }

            context("일반 유저 ID지만, 현재 진행 중인 라운드를 조회하려는 경우") {
                every {
                    winningLottoService.matchUserLottoByRound(
                        TEST_USER_ID,
                        TEST_ROUND,
                    )
                } throws IllegalArgumentException("현재 진행중인 라운드는 조회할 수 없습니다.")
                it("400 응답한다.") {
                    restDocMockMvc.perform(
                        RestDocumentationRequestBuilders
                            .get(targetUri, TEST_ROUND)
                            .param(USER_ID_PARAM, TEST_USER_ID.toString()),
                    ).andExpect {
                        status().isBadRequest
                    }.andDo {
                        createPathDocument(
                            "match-user-lotto-fail-user-access-current-round",
                            pathParameters(
                                "round" pathDescription "조회할 라운드 정보" example TEST_ROUND,
                            ),
                            queryParameters(
                                USER_ID_PARAM parameterDescription "조회할 일반 유저 ID" example TEST_USER_ID,
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
