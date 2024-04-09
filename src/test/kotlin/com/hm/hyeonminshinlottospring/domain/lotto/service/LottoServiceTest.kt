package com.hm.hyeonminshinlottospring.domain.lotto.service

import com.hm.hyeonminshinlottospring.domain.lotto.domain.GenerateMode
import com.hm.hyeonminshinlottospring.domain.lotto.domain.Lotto
import com.hm.hyeonminshinlottospring.domain.lotto.repository.LottoRepository
import com.hm.hyeonminshinlottospring.domain.lotto.service.generator.RandomLottoNumbersGenerator
import com.hm.hyeonminshinlottospring.domain.user.repository.UserRepository
import com.hm.hyeonminshinlottospring.domain.user.repository.findByUserId
import com.hm.hyeonminshinlottospring.domain.winninglotto.domain.WinningLottoInformation
import com.hm.hyeonminshinlottospring.support.TEST_DEFAULT_PAGEABLE
import com.hm.hyeonminshinlottospring.support.TEST_GENERATE_COUNT_10
import com.hm.hyeonminshinlottospring.support.TEST_GENERATE_COUNT_2
import com.hm.hyeonminshinlottospring.support.TEST_NUMBER
import com.hm.hyeonminshinlottospring.support.TEST_ROUND
import com.hm.hyeonminshinlottospring.support.TEST_USER_ID
import com.hm.hyeonminshinlottospring.support.createAdmin
import com.hm.hyeonminshinlottospring.support.createAllLotto
import com.hm.hyeonminshinlottospring.support.createLottoCreateRequest
import com.hm.hyeonminshinlottospring.support.createLottoNumbers
import com.hm.hyeonminshinlottospring.support.createUser
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.sync.Mutex
import org.springframework.data.domain.Slice

class LottoServiceTest : DescribeSpec(
    {
        val randomLottoNumbersGenerator = mockk<RandomLottoNumbersGenerator>()
        val winningLottoInformation = mockk<WinningLottoInformation>()
        val lottoRepository = mockk<LottoRepository>()
        val userRepository = mockk<UserRepository>()
        val lottoService =
            LottoService(
                randomLottoNumbersGenerator,
                winningLottoInformation,
                lottoRepository,
                userRepository,
            )

        describe("createLottos") {
            context("모두 랜덤 생성 요청") {
                val user = createUser()
                val lottoCreateRequest =
                    createLottoCreateRequest(numbers = createLottoNumbers(TEST_GENERATE_COUNT_10))
                every { userRepository.findByUserId(TEST_USER_ID) } returns user
                every { winningLottoInformation.round } returns TEST_ROUND
                coEvery { winningLottoInformation.roundMutex } returns Mutex()
                coEvery { randomLottoNumbersGenerator.generate() } returns TEST_NUMBER
                every { lottoRepository.saveAll(any<List<Lotto>>()) } returns createAllLotto(user)
                it("요청한 로또의 개수만큼 생성한다") {
                    shouldNotThrowAny {
                        lottoService.createLottos(
                            lottoCreateRequest,
                        )
                    }
                }
            }

            context("모두 수동 생성 요청") {
                val user = createUser()
                val lottoCreateRequest =
                    createLottoCreateRequest(
                        numbers = createLottoNumbers(TEST_GENERATE_COUNT_10),
                        mode = GenerateMode.MANUAL,
                    )
                every { userRepository.findByUserId(TEST_USER_ID) } returns user
                every { winningLottoInformation.round } returns TEST_ROUND
                coEvery { winningLottoInformation.roundMutex } returns Mutex()
                coEvery { randomLottoNumbersGenerator.generate() } returns TEST_NUMBER
                every { lottoRepository.saveAll(any<List<Lotto>>()) } returns createAllLotto(user)
                it("요청한 로또의 개수만큼 생성한다") {
                    shouldNotThrowAny {
                        lottoService.createLottos(
                            lottoCreateRequest,
                        )
                    }
                }
            }
        }

        describe("getLottosByUserAndRound") {
            context("유효한 데이터가 주어진 경우") {
                val user = createUser()
                val mockSlice = mockk<Slice<Lotto>>()
                every { userRepository.findByUserId(TEST_USER_ID) } returns createUser()
                every {
                    lottoRepository.findSliceByUserIdAndRound(
                        TEST_USER_ID,
                        TEST_ROUND,
                        TEST_DEFAULT_PAGEABLE,
                    )
                } returns mockSlice
                every { mockSlice.numberOfElements } returns TEST_GENERATE_COUNT_2
                every { mockSlice.content } returns createAllLotto(user)
                every { mockSlice.hasNext() } returns true
                it("정상 종료한다.") {
                    shouldNotThrowAny {
                        lottoService.getLottosByUserAndRound(
                            TEST_USER_ID,
                            TEST_ROUND,
                            TEST_DEFAULT_PAGEABLE,
                        )
                    }
                }
            }

            context("Admin 유저가 주어진 경우") {
                every { userRepository.findByUserId(TEST_USER_ID) } returns createAdmin()
                it("[IllegalStateException] 발생한다.") {
                    shouldThrow<IllegalStateException> {
                        lottoService.getLottosByUserAndRound(
                            TEST_USER_ID,
                            TEST_ROUND,
                            TEST_DEFAULT_PAGEABLE,
                        )
                    }
                }
            }
        }

        // 아래 테스트론 coroutine 측정이 잘 안돼서 일단 postman으로 받아온 결과로 적는다.
        /**
         * (기준: RANDOM 생성)
         *             ||      시간 (단위: ms)        ||
         *   생성 개수   ||   legacy   |   coroutine(Default)  |  coroutine(LOOM) ||
         *  =====================================================================
         *       100  ||        183  |                     39 |              187 ||
         *     1,000  ||        740  |                    279 |              513 ||
         *    10,000  ||       3180  |                   3760 |             3170 ||
         *   100,000  ||      32930  |                  35840 |            32020 ||
         *
         *
         */
        // TODO: coroutine 테스트 방법 정확히 알게 되면 다시 적용하기
//        describe("createLottos - Performance Test") {
//            val insertedMoney = 1_000_000
//
//            context("기존 blocking 루틴 사용") {
//                val user = createUser(money = insertedMoney)
//                val lottoCreateRequest = createLottoCreateRequest(insertedMoney = insertedMoney)
//                every { userRepository.findByUserId(TEST_USER_ID) } returns user
//                every { winningLottoInformation.round } returns TEST_ROUND
//                every { lottoRepository.saveAll(any<List<Lotto>>()) } returns emptyList()
//                it("시간 측정") {
//                    val time = measureTimeMillis {
//                        shouldNotThrowAny {
//                            lottoService.createLottosLegacy(
//                                lottoCreateRequest,
//                            )
//                        }
//                    }
//                    println("legacy: $time ms")
//                }
//            }
//
//            context("coroutine 사용") {
//                val user = createUser(money = insertedMoney)
//                val lottoCreateRequest = createLottoCreateRequest(insertedMoney = insertedMoney)
//                every { userRepository.findByUserId(TEST_USER_ID) } returns user
//                every { winningLottoInformation.round } returns TEST_ROUND
//                coEvery { winningLottoInformation.roundMutex } returns Mutex()
//                coEvery { randomLottoNumbersGenerator.generate() } returns TEST_NUMBER
//                every { lottoRepository.saveAll(any<List<Lotto>>()) } returns emptyList()
//                it("시간 측정") {
//                    runTest {
//                        val time = measureTimeMillis {
//                            shouldNotThrowAny {
//                                lottoService.createLottos(
//                                    lottoCreateRequest,
//                                )
//                            }
//                        }
//                        println("coroutine: $time ms")
//                    }
//                }
//            }
//        }
    },
)
