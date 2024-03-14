package com.hm.hyeonminshinlottospring.domain.lotto.service

import com.hm.hyeonminshinlottospring.domain.lotto.domain.GenerateMode
import com.hm.hyeonminshinlottospring.domain.lotto.domain.Lotto
import com.hm.hyeonminshinlottospring.domain.lotto.repository.LottoRepository
import com.hm.hyeonminshinlottospring.domain.lotto.repository.getByUserAndRound
import com.hm.hyeonminshinlottospring.domain.lotto.service.generator.RandomLottoNumbersGenerator
import com.hm.hyeonminshinlottospring.domain.user.repository.UserRepository
import com.hm.hyeonminshinlottospring.domain.user.repository.existsByUserId
import com.hm.hyeonminshinlottospring.domain.user.repository.getByUserId
import com.hm.hyeonminshinlottospring.domain.winninglotto.domain.WinningLottoInformation
import com.hm.hyeonminshinlottospring.support.TEST_GENERATE_COUNT
import com.hm.hyeonminshinlottospring.support.TEST_NUMBER
import com.hm.hyeonminshinlottospring.support.TEST_ROUND
import com.hm.hyeonminshinlottospring.support.TEST_USER_ID
import com.hm.hyeonminshinlottospring.support.createAllLottoWithUser
import com.hm.hyeonminshinlottospring.support.createLotto
import com.hm.hyeonminshinlottospring.support.createLottoCreateRequest
import com.hm.hyeonminshinlottospring.support.createLottoNumbers
import com.hm.hyeonminshinlottospring.support.createUser
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.mockk

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
                val lottoCreateRequest =
                    createLottoCreateRequest(
                        mode = GenerateMode.RANDOM,
                        generateCount = TEST_GENERATE_COUNT,
                        numbers = createLottoNumbers(TEST_GENERATE_COUNT),
                    )
                val user = createUser()
                every { userRepository.getByUserId(TEST_USER_ID) } returns user
                every { randomLottoNumbersGenerator.generate() } returns TEST_NUMBER
                every { lottoRepository.save(any<Lotto>()) } returns createLotto(user)
                it("요청한 로또의 개수만큼 생성한다") {
                    shouldNotThrowAny {
                        lottoService.createLottos(
                            TEST_USER_ID,
                            lottoCreateRequest,
                        )
                    }
                }
            }

            context("모두 수동 생성 요청(모두 유효한 데이터)") {
                val lottoCreateRequest =
                    createLottoCreateRequest(
                        mode = GenerateMode.MANUAL,
                        generateCount = TEST_GENERATE_COUNT,
                        numbers = createLottoNumbers(TEST_GENERATE_COUNT),
                    )
                val user = createUser()
                every { userRepository.getByUserId(TEST_USER_ID) } returns user
                every { lottoRepository.save(any<Lotto>()) } returns createLotto(user)
                it("요청한 로또의 개수만큼 생성한다") {
                    shouldNotThrowAny {
                        lottoService.createLottos(
                            TEST_USER_ID,
                            lottoCreateRequest,
                        )
                    }
                }
            }

            context("모두 수동 생성 요청(일부 또는 전부가 유효하지 않은 데이터)") {
                val lottoCreateRequest =
                    createLottoCreateRequest(
                        mode = GenerateMode.MANUAL,
                        generateCount = TEST_GENERATE_COUNT,
                        numbers = createLottoNumbers(TEST_GENERATE_COUNT - 1),
                    )
                val user = createUser()
                every { userRepository.getByUserId(TEST_USER_ID) } returns user
                every { lottoRepository.save(any<Lotto>()) } returns createLotto(user)
                it("유효한 데이터의 개수만큼 로또를 생성한다") {
                    shouldNotThrowAny {
                        lottoService.createLottos(
                            TEST_USER_ID,
                            lottoCreateRequest,
                        )
                    }
                }
            }
        }

        describe("getLottosByUserAndRound") {
            context("유효한 데이터가 주어진 경우") {
                val user = createUser()
                every { winningLottoInformation.round } returns TEST_ROUND + 1
                every { userRepository.existsByUserId(TEST_USER_ID) } returns true
                every { userRepository.getByUserId(TEST_USER_ID) } returns user
                every { lottoRepository.getByUserAndRound(user, TEST_ROUND) } returns createAllLottoWithUser(user)
                it("정상 종료한다.") {
                    shouldNotThrowAny {
                        lottoService.getLottosByUserAndRound(
                            TEST_USER_ID,
                            TEST_ROUND,
                            5,
                        )
                    }
                }
            }

            context("조회하려는 로또의 개수가 0 이하일 경우") {
                it("[IllegalArgumentException] 예외 발생한다.") {
                    shouldThrowExactly<IllegalArgumentException> {
                        lottoService.getLottosByUserAndRound(
                            TEST_USER_ID,
                            TEST_ROUND,
                            -1,
                        )
                    }
                }
            }

            context("조회하려는 라운드가 유효하지 않을 경우") {
                it("[IllegalArgumentException] 발생한다.") {
                    shouldThrow<IllegalArgumentException> {
                        lottoService.getLottosByUserAndRound(
                            TEST_USER_ID,
                            -1,
                        )
                    }
                }
            }

            context("존재하지 않는 유저 ID가 주어진 경우") {
                every { winningLottoInformation.round } returns TEST_ROUND + 1
                every { userRepository.existsByUserId(TEST_USER_ID) } returns false
                it("[IllegalArgumentException] 발생한다.") {
                    shouldThrow<NoSuchElementException> {
                        lottoService.getLottosByUserAndRound(
                            TEST_USER_ID,
                            TEST_ROUND,
                        )
                    }
                }
            }
        }
    },
)
