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
import com.hm.hyeonminshinlottospring.support.createSliceLottoNumberResponse
import com.hm.hyeonminshinlottospring.support.createUser
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.mockk
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
                every { randomLottoNumbersGenerator.generate() } returns TEST_NUMBER
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
                    createLottoCreateRequest(numbers = createLottoNumbers(TEST_GENERATE_COUNT_10), mode = GenerateMode.MANUAL)
                every { userRepository.findByUserId(TEST_USER_ID) } returns user
                every { winningLottoInformation.round } returns TEST_ROUND
                every { randomLottoNumbersGenerator.generate() } returns TEST_NUMBER
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
                val response = createSliceLottoNumberResponse()
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
    },
)
