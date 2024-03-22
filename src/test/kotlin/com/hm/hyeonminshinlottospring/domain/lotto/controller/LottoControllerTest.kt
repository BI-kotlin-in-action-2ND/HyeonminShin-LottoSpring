package com.hm.hyeonminshinlottospring.domain.lotto.controller

import com.hm.hyeonminshinlottospring.domain.lotto.domain.GenerateMode
import com.hm.hyeonminshinlottospring.domain.lotto.domain.info.LottoPrice
import com.hm.hyeonminshinlottospring.domain.lotto.service.LottoService
import com.hm.hyeonminshinlottospring.support.PAGE_PARAM
import com.hm.hyeonminshinlottospring.support.ROUND_PARAM
import com.hm.hyeonminshinlottospring.support.SIZE_PARAM
import com.hm.hyeonminshinlottospring.support.TEST_ADMIN_USER_ID
import com.hm.hyeonminshinlottospring.support.TEST_GENERATE_COUNT_10
import com.hm.hyeonminshinlottospring.support.TEST_INVALID_MONEY
import com.hm.hyeonminshinlottospring.support.TEST_INVALID_ROUND
import com.hm.hyeonminshinlottospring.support.TEST_INVALID_USER_ID
import com.hm.hyeonminshinlottospring.support.TEST_MONEY_10
import com.hm.hyeonminshinlottospring.support.TEST_NOT_EXIST_USER_ID
import com.hm.hyeonminshinlottospring.support.TEST_PAGE
import com.hm.hyeonminshinlottospring.support.TEST_PAGEABLE
import com.hm.hyeonminshinlottospring.support.TEST_ROUND
import com.hm.hyeonminshinlottospring.support.TEST_SIZE
import com.hm.hyeonminshinlottospring.support.TEST_USER_ID
import com.hm.hyeonminshinlottospring.support.createLottoCreateRequest
import com.hm.hyeonminshinlottospring.support.createLottoCreateResponse
import com.hm.hyeonminshinlottospring.support.createLottoNumbers
import com.hm.hyeonminshinlottospring.support.createSliceLottoNumberResponse
import com.hm.hyeonminshinlottospring.support.createUser
import com.hm.hyeonminshinlottospring.support.test.BaseTests.UnitControllerTestEnvironment
import com.hm.hyeonminshinlottospring.support.test.ControllerTestHelper.Companion.jsonContent
import com.hm.hyeonminshinlottospring.support.test.RestDocsHelpler
import com.hm.hyeonminshinlottospring.support.test.RestDocsHelpler.Companion.createDocument
import com.hm.hyeonminshinlottospring.support.test.RestDocsHelpler.Companion.createPathDocument
import com.hm.hyeonminshinlottospring.support.test.RestDocsHelpler.Companion.requestBody
import com.hm.hyeonminshinlottospring.support.test.RestDocsHelpler.Companion.responseBody
import com.hm.hyeonminshinlottospring.support.test.example
import com.hm.hyeonminshinlottospring.support.test.isOptional
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
@WebMvcTest(LottoController::class)
class LottoControllerTest(
    private val context: WebApplicationContext,
    @MockkBean private val lottoService: LottoService,
) : DescribeSpec(
    {
        val restDocumentation = ManualRestDocumentation()
        val restDocMockMvc = RestDocsHelpler.generateRestDocMvc(context, restDocumentation)

        beforeEach {
            restDocumentation.beforeTest(javaClass, it.name.testName)
        }

        describe("POST /api/v1/lotto") {
            val targetUri = "/api/v1/lotto"
            context("유효한 요청이면서 랜덤 생성인 경우") {
                val user = createUser()
                val request = createLottoCreateRequest(mode = GenerateMode.RANDOM, numbers = null)
                val response = createLottoCreateResponse()
                every { lottoService.createLottos(request) } returns response
                it("201 응답힌다.") {
                    restDocMockMvc.post(targetUri) {
                        jsonContent(request)
                    }.andExpect {
                        status { isCreated() }
                    }.andDo {
                        createDocument(
                            "create-lottos-success-random",
                            requestBody(
                                "userId" type JsonFieldType.NUMBER description "요청한 유저 ID" example request.userId,
                                "mode" type JsonFieldType.STRING description "생성 모드" example request.mode,
                                "insertedMoney" type JsonFieldType.NUMBER description "투입한 금액" example request.insertedMoney,
                                "numbers" type JsonFieldType.NULL description "랜덤일 땐 의미없는 지정 로또 번호" example request.numbers isOptional true,
                            ),
                            responseBody(
                                "userId" type JsonFieldType.NUMBER description "요청한 유저 ID" example response.userId,
                                "round" type JsonFieldType.NUMBER description "생성 요청된 라운드" example response.round,
                                "createdLottoCount" type JsonFieldType.NUMBER description "정상 생성된 로또 개수" example response.createdLottoCount,
                            ),
                        )
                    }
                }
            }

            context("유효한 요청이면서 수동 생성인 경우") {
                val request = createLottoCreateRequest(
                    mode = GenerateMode.MANUAL,
                    numbers = createLottoNumbers(
                        TEST_GENERATE_COUNT_10,
                    ),
                )
                val response = createLottoCreateResponse()
                every { lottoService.createLottos(request) } returns response
                it("201 응답힌다.") {
                    restDocMockMvc.post(targetUri) {
                        jsonContent(request)
                    }.andExpect {
                        status { isCreated() }
                    }.andDo {
                        createDocument(
                            "create-lottos-success-manual",
                            requestBody(
                                "userId" type JsonFieldType.NUMBER description "요청한 유저 ID" example request.userId,
                                "mode" type JsonFieldType.STRING description "생성 모드" example request.mode,
                                "insertedMoney" type JsonFieldType.NUMBER description "투입한 금액" example request.insertedMoney,
                                "numbers[][]" type JsonFieldType.ARRAY description "수동으로 지정한 로또 번호 리스트" example request.numbers isOptional true,
                            ),
                            responseBody(
                                "userId" type JsonFieldType.NUMBER description "요청한 유저 ID" example response.userId,
                                "round" type JsonFieldType.NUMBER description "생성 요청된 라운드" example response.round,
                                "createdLottoCount" type JsonFieldType.NUMBER description "정상 생성된 로또 개수" example response.createdLottoCount,
                            ),
                        )
                    }
                }
            }

            context("유저 ID가 음수인 경우") {
                val request = createLottoCreateRequest(userId = TEST_INVALID_USER_ID)
                it("400 응답힌다.") {
                    restDocMockMvc.post(targetUri) {
                        jsonContent(request)
                    }.andExpect {
                        status { isBadRequest() }
                    }.andDo {
                        createDocument(
                            "create-lottos-fail-user-id-negative",
                            requestBody(
                                "userId" type JsonFieldType.NUMBER description "요청한 음수의 유저 ID" example request.userId,
                                "mode" type JsonFieldType.STRING description "생성 모드" example request.mode,
                                "insertedMoney" type JsonFieldType.NUMBER description "투입한 금액" example request.insertedMoney,
                                "numbers" type JsonFieldType.NULL description "지정한 로또 번호" example request.numbers isOptional true,
                            ),
                        )
                    }
                }
            }

            context("요청한 유저 ID가 존재하지 않을 경우") {
                val request = createLottoCreateRequest()
                every { lottoService.createLottos(request) } throws NoSuchElementException("$TEST_USER_ID: 사용자가 존재하지 않습니다.")
                it("404 응답힌다.") {
                    restDocMockMvc.post(targetUri) {
                        jsonContent(request)
                    }.andExpect {
                        status { isNotFound() }
                    }.andDo {
                        createDocument(
                            "create-lottos-fail-user-id-not-exist",
                            requestBody(
                                "userId" type JsonFieldType.NUMBER description "존재하지 않는 유저 ID" example request.userId,
                                "mode" type JsonFieldType.STRING description "생성 모드" example request.mode,
                                "insertedMoney" type JsonFieldType.NUMBER description "투입한 금액" example request.insertedMoney,
                                "numbers" type JsonFieldType.NULL description "지정한 로또 번호" example request.numbers isOptional true,
                            ),
                        )
                    }
                }
            }

            context("투입한 금액이 음수인 경우") {
                val request = createLottoCreateRequest(insertedMoney = TEST_INVALID_MONEY)
                it("400 응답힌다.") {
                    restDocMockMvc.post(targetUri) {
                        jsonContent(request)
                    }.andExpect {
                        status { isBadRequest() }
                    }.andDo {
                        createDocument(
                            "create-lottos-fail-inserted-money-negative",
                            requestBody(
                                "userId" type JsonFieldType.NUMBER description "요청한 유저 ID" example request.userId,
                                "mode" type JsonFieldType.STRING description "생성 모드" example request.mode,
                                "insertedMoney" type JsonFieldType.NUMBER description "음수의 투입한 금액" example request.insertedMoney,
                                "numbers" type JsonFieldType.NULL description "지정한 로또 번호" example request.numbers isOptional true,
                            ),
                        )
                    }
                }
            }

            context("투입한 금액이 유저가 가진 돈보다 많은 경우") {
                val request = createLottoCreateRequest(insertedMoney = TEST_MONEY_10 * 2)
                every { lottoService.createLottos(request) } throws IllegalArgumentException("${request.userId}: 현재 소지한 금액($TEST_MONEY_10${LottoPrice.UNIT})보다 적은 금액을 입력해주세요.")
                it("400 응답힌다.") {
                    restDocMockMvc.post(targetUri) {
                        jsonContent(request)
                    }.andExpect {
                        status { isBadRequest() }
                    }.andDo {
                        createDocument(
                            "create-lottos-fail-inserted-money-bigger-than-having",
                            requestBody(
                                "userId" type JsonFieldType.NUMBER description "요청한 유저 ID" example request.userId,
                                "mode" type JsonFieldType.STRING description "생성 모드" example request.mode,
                                "insertedMoney" type JsonFieldType.NUMBER description "소지금액보다 많이 투입한 금액" example request.insertedMoney,
                                "numbers" type JsonFieldType.NULL description "지정한 로또 번호" example request.numbers isOptional true,
                            ),
                        )
                    }
                }
            }
        }

        describe("GET /api/v1/lotto/{userId}") {
            val targetUri = "/api/v1/lotto/{userId}"
            context("유효한 파라미터들이 넘어오는 경우") {
                val response = createSliceLottoNumberResponse()
                every { lottoService.getLottosByUserAndRound(TEST_USER_ID, TEST_ROUND, TEST_PAGEABLE) } returns response
                it("200 응답한다.") {
                    restDocMockMvc.perform(
                        RestDocumentationRequestBuilders
                            .get(targetUri, TEST_USER_ID)
                            .param(ROUND_PARAM, TEST_ROUND.toString())
                            .param(PAGE_PARAM, TEST_PAGE.toString())
                            .param(SIZE_PARAM, TEST_SIZE.toString()),
                    ).andExpect {
                        status().isOk
                    }.andDo {
                        createPathDocument(
                            "get-lottos-by-user-round-success",
                            pathParameters(
                                "userId" pathDescription "조회할 유저 ID" example TEST_USER_ID,
                            ),
                            queryParameters(
                                ROUND_PARAM parameterDescription "조회할 라운드" example TEST_ROUND,
                                PAGE_PARAM parameterDescription "데이터 조회 시작점 (default = 0)" example TEST_PAGE isOptional true,
                                SIZE_PARAM parameterDescription "데이터 개수 (default = 10)" example TEST_SIZE isOptional true,
                            ),
                            responseBody(
                                "userId" type JsonFieldType.NUMBER description "조회 요청한 유저 ID" example response.userId isOptional true,
                                "round" type JsonFieldType.NUMBER description "조회한 로또 라운드" example response.round isOptional true,
                                "hasNext" type JsonFieldType.BOOLEAN description "조회 가능한 추가적인 데이터 존재 여부" example response.hasNext,
                                "numberOfElements" type JsonFieldType.NUMBER description "현재 가져온 데이터 개수" example response.numberOfElements,
                                "content" type JsonFieldType.ARRAY description "조회한 개수만큼의 로또 번호 리스트" example response.numberList,
                            ),
                        )
                    }
                }
            }

            context("음수의 유저 ID가 주어진 경우") {
                it("400 응답한다.") {
                    restDocMockMvc.perform(
                        RestDocumentationRequestBuilders
                            .get(targetUri, TEST_INVALID_USER_ID)
                            .param(ROUND_PARAM, TEST_ROUND.toString())
                            .param(PAGE_PARAM, TEST_PAGE.toString())
                            .param(SIZE_PARAM, TEST_SIZE.toString()),
                    ).andExpect {
                        status().isBadRequest
                    }.andDo {
                        createPathDocument(
                            "get-lottos-by-user-round-fail-user-id-negative",
                            pathParameters(
                                "userId" pathDescription "음수의 조회할 유저 ID" example TEST_INVALID_USER_ID,
                            ),
                            queryParameters(
                                ROUND_PARAM parameterDescription "조회할 라운드" example TEST_ROUND,
                                PAGE_PARAM parameterDescription "데이터 조회 시작점 (default = 0)" example TEST_PAGE isOptional true,
                                SIZE_PARAM parameterDescription "데이터 개수 (default = 10)" example TEST_SIZE isOptional true,
                            ),
                        )
                    }
                }
            }

            context("음수의 라운드가 주어진 경우") {
                it("400 응답한다.") {
                    restDocMockMvc.perform(
                        RestDocumentationRequestBuilders
                            .get(targetUri, TEST_USER_ID)
                            .param(ROUND_PARAM, TEST_INVALID_ROUND.toString())
                            .param(PAGE_PARAM, TEST_PAGE.toString())
                            .param(SIZE_PARAM, TEST_SIZE.toString()),
                    ).andExpect {
                        status().isBadRequest
                    }.andDo {
                        createPathDocument(
                            "get-lottos-by-user-round-fail-round-negative",
                            pathParameters(
                                "userId" pathDescription "조회할 유저 ID" example TEST_USER_ID,
                            ),
                            queryParameters(
                                ROUND_PARAM parameterDescription "음수의 조회할 라운드" example TEST_INVALID_ROUND,
                                PAGE_PARAM parameterDescription "데이터 조회 시작점 (default = 0)" example TEST_PAGE isOptional true,
                                SIZE_PARAM parameterDescription "데이터 개수 (default = 10)" example TEST_SIZE isOptional true,
                            ),
                        )
                    }
                }
            }

            context("Admin 유저 ID가 주어진 경우") {
                every { lottoService.getLottosByUserAndRound(TEST_ADMIN_USER_ID, TEST_ROUND, TEST_PAGEABLE) } throws IllegalStateException("$TEST_ADMIN_USER_ID: 주어진 유저 ID는 조회할 수 없습니다.")
                it("400 응답한다.") {
                    restDocMockMvc.perform(
                        RestDocumentationRequestBuilders
                            .get(targetUri, TEST_ADMIN_USER_ID)
                            .param(ROUND_PARAM, TEST_ROUND.toString())
                            .param(PAGE_PARAM, TEST_PAGE.toString())
                            .param(SIZE_PARAM, TEST_SIZE.toString()),
                    ).andExpect {
                        status().isBadRequest
                    }.andDo {
                        createPathDocument(
                            "get-lottos-by-user-round-fail-user-id-admin",
                            pathParameters(
                                "userId" pathDescription "Admin 유저 ID" example TEST_ADMIN_USER_ID,
                            ),
                            queryParameters(
                                ROUND_PARAM parameterDescription "조회할 라운드" example TEST_ROUND,
                                PAGE_PARAM parameterDescription "데이터 조회 시작점 (default = 0)" example TEST_PAGE isOptional true,
                                SIZE_PARAM parameterDescription "데이터 개수 (default = 10)" example TEST_SIZE isOptional true,
                            ),
                        )
                    }
                }
            }

            context("존재하지 않는 유저 ID가 주어진 경우") {
                val response = createSliceLottoNumberResponse()
                every { lottoService.getLottosByUserAndRound(TEST_NOT_EXIST_USER_ID, TEST_ROUND, TEST_PAGEABLE) } returns response
                it("404 응답한다.") {
                    restDocMockMvc.perform(
                        RestDocumentationRequestBuilders
                            .get(targetUri, TEST_NOT_EXIST_USER_ID)
                            .param(ROUND_PARAM, TEST_ROUND.toString())
                            .param(PAGE_PARAM, TEST_PAGE.toString())
                            .param(SIZE_PARAM, TEST_SIZE.toString()),
                    ).andExpect {
                        status().isNotFound
                    }.andDo {
                        createPathDocument(
                            "get-lottos-by-user-round-fail-user-id-not-exist",
                            pathParameters(
                                "userId" pathDescription "존재하지 않는 유저 ID" example TEST_NOT_EXIST_USER_ID,
                            ),
                            queryParameters(
                                ROUND_PARAM parameterDescription "조회할 라운드" example TEST_ROUND,
                                PAGE_PARAM parameterDescription "데이터 조회 시작점 (default = 0)" example TEST_PAGE isOptional true,
                                SIZE_PARAM parameterDescription "데이터 개수 (default = 10)" example TEST_SIZE isOptional true,
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
