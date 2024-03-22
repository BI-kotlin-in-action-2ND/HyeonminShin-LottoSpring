package com.hm.hyeonminshinlottospring.domain.winninglotto.service

import com.hm.hyeonminshinlottospring.domain.lotto.repository.LottoRepository
import com.hm.hyeonminshinlottospring.domain.lotto.service.generator.RandomLottoNumbersGenerator
import com.hm.hyeonminshinlottospring.domain.user.repository.UserRepository
import com.hm.hyeonminshinlottospring.domain.user.repository.findByUserId
import com.hm.hyeonminshinlottospring.domain.winninglotto.domain.WinningLotto
import com.hm.hyeonminshinlottospring.domain.winninglotto.domain.WinningLottoInformation
import com.hm.hyeonminshinlottospring.domain.winninglotto.repository.WinningLottoRepository
import com.hm.hyeonminshinlottospring.support.TEST_ADMIN_USER_ID
import com.hm.hyeonminshinlottospring.support.TEST_INVALID_ROUND
import com.hm.hyeonminshinlottospring.support.TEST_NUMBER
import com.hm.hyeonminshinlottospring.support.TEST_ROUND
import com.hm.hyeonminshinlottospring.support.createAdmin
import com.hm.hyeonminshinlottospring.support.createAllLotto
import com.hm.hyeonminshinlottospring.support.createLotto
import com.hm.hyeonminshinlottospring.support.createUser
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.mockk

class WinningLottoServiceTest : DescribeSpec(
    {
        val winningLottoInformation = mockk<WinningLottoInformation>()
        val randomLottoNumbersGenerator = mockk<RandomLottoNumbersGenerator>()
        val lottoRepository = mockk<LottoRepository>()
        val winningLottoRepository = mockk<WinningLottoRepository>()
        val userRepository = mockk<UserRepository>()
        val winningLottoService =
            WinningLottoService(
                winningLottoInformation,
                randomLottoNumbersGenerator,
                lottoRepository,
                winningLottoRepository,
                userRepository,
            )

        describe("createWinningLotto") {
            context("호출했을 경우") {
                val admin = createAdmin()
                val lotto = createLotto(admin)
                val winLotto =
                    WinningLotto(
                        round = TEST_ROUND,
                        lotto = lotto,
                    )
                every { winningLottoInformation.round } returns TEST_ROUND
                every { userRepository.save(any()) } returns admin
                every { randomLottoNumbersGenerator.generate() } returns TEST_NUMBER
                every { lottoRepository.save(any()) } returns lotto
                every { winningLottoRepository.save(any()) } returns winLotto
                every { winningLottoInformation.increaseRound() } returns Unit
                it("정상 종료") {
                    shouldNotThrowAny {
                        winningLottoService.createWinningLotto()
                    }
                }
            }
        }

        describe("getWinningLottoByRound") {
            val user = createUser()
            val admin = createAdmin()
            context("Admin이 요청할 때") {
                val lotto = createLotto(admin)
                val winLotto =
                    WinningLotto(
                        round = TEST_ROUND,
                        lotto = lotto,
                    )
                every { userRepository.findByUserId(any()) } returns admin
                every { winningLottoInformation.round } returns TEST_ROUND

                it("정상 종료: 존재하는 라운드") {
                    every { winningLottoRepository.findByRound(any()) } returns winLotto
                    shouldNotThrowAny {
                        winningLottoService.getWinningLottoByRound(TEST_ADMIN_USER_ID, TEST_ROUND)
                    }
                }

                it("[IllegalArgumentException] 예외 발생: 존재하지 않는 라운드") {
                    shouldThrowExactly<IllegalArgumentException> {
                        winningLottoService.getWinningLottoByRound(TEST_ADMIN_USER_ID, TEST_INVALID_ROUND)
                    }
                }
            }

            context("일반 유저가 요청할 때") {
                val lotto = createLotto(user)
                val winLotto =
                    WinningLotto(
                        round = TEST_ROUND,
                        lotto = lotto,
                    )
                every { userRepository.findByUserId(any()) } returns user
                every { winningLottoInformation.round } returns TEST_ROUND

                it("정상 종료: 현재 라운드 이전 라운드 접근") {
                    every { winningLottoRepository.findByRound(any()) } returns winLotto
                    shouldNotThrowAny {
                        winningLottoService.getWinningLottoByRound(user.id, TEST_ROUND - 1)
                    }
                }

                it("[IllegalArgumentException] 예외 발생: 현재 라운드 접근시") {
                    shouldThrowExactly<IllegalArgumentException> {
                        winningLottoService.getWinningLottoByRound(user.id, TEST_ROUND)
                    }
                }

                it("[IllegalArgumentException] 예외 발생: 존재하지 않는 라운드") {
                    shouldThrowExactly<IllegalArgumentException> {
                        winningLottoService.getWinningLottoByRound(user.id, TEST_INVALID_ROUND)
                    }
                }
            }
        }

        describe("matchUserLottoByRound") {
            val user = createUser()
            val admin = createAdmin()
            val winLotto =
                WinningLotto(
                    round = TEST_ROUND,
                    lotto = createLotto(admin),
                )

            context("유효한 데이터가 주어진 경우") {
                every { userRepository.findByUserId(any()) } returns user
                every { winningLottoInformation.round } returns TEST_ROUND + 1
                every { lottoRepository.findListByUserIdAndRound(user.id, TEST_ROUND) } returns createAllLotto(user)
                every { winningLottoRepository.findByRound(TEST_ROUND) } returns winLotto
                it("정상 종료") {
                    shouldNotThrowAny {
                        winningLottoService.matchUserLottoByRound(user.id, TEST_ROUND)
                    }
                }
            }

            context("유저가 해당 라운드에 구매한 로또가 없을 경우") {
                every { userRepository.findByUserId(any()) } returns user
                every { winningLottoInformation.round } returns TEST_ROUND + 1
                every { lottoRepository.findListByUserIdAndRound(user.id, TEST_ROUND) } returns emptyList()
                it("[IllegalStateException] 예외 발생") {
                    shouldThrowExactly<IllegalStateException> {
                        winningLottoService.matchUserLottoByRound(user.id, TEST_ROUND)
                    }
                }
            }

            // TODO: Admin 관련 추가해야 함
        }
    },
)
