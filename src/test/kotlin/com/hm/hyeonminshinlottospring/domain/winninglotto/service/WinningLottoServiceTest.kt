package com.hm.hyeonminshinlottospring.domain.winninglotto.service

import com.hm.hyeonminshinlottospring.domain.lotto.repository.LottoRepository
import com.hm.hyeonminshinlottospring.domain.lotto.service.generator.RandomLottoNumbersGenerator
import com.hm.hyeonminshinlottospring.domain.user.domain.UserRole
import com.hm.hyeonminshinlottospring.domain.user.repository.UserRepository
import com.hm.hyeonminshinlottospring.domain.user.repository.getByUserId
import com.hm.hyeonminshinlottospring.domain.winninglotto.domain.WinningLotto
import com.hm.hyeonminshinlottospring.domain.winninglotto.domain.WinningLottoInformation
import com.hm.hyeonminshinlottospring.domain.winninglotto.repository.WinningLottoRepository
import com.hm.hyeonminshinlottospring.domain.winninglotto.repository.getByRound
import com.hm.hyeonminshinlottospring.support.TEST_ADMIN_USER_ID
import com.hm.hyeonminshinlottospring.support.TEST_INVALID_ROUND
import com.hm.hyeonminshinlottospring.support.TEST_ROUND
import com.hm.hyeonminshinlottospring.support.TEST_USER_ID
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
                val user = createUser()
                val lotto = createLotto(user)
                val winLotto =
                    WinningLotto(
                        round = TEST_ROUND,
                        lotto = lotto,
                    )
                every { winningLottoInformation.round } returns TEST_ROUND
                every { userRepository.save(any()) } returns user
                every { randomLottoNumbersGenerator.generate() } returns lotto.numbers
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
            val lotto = createLotto(createUser())
            context("Admin이 요청할 때") {
                every { userRepository.getByUserId(any()).userRole } returns UserRole.ROLE_ADMIN

                it("정상 종료: 존재하는 라운드") {
                    every { winningLottoRepository.getByRound(any()) } returns lotto
                    shouldNotThrowAny {
                        winningLottoService.getWinningLottoByRound(TEST_ADMIN_USER_ID, TEST_ROUND)
                    }
                }

                it("[NoSuchElementException] 예외 발생: 존재하지 않는 라운드") {
                    every { winningLottoRepository.getByRound(any()) } throws NoSuchElementException("존재하지 않는 라운드")
                    shouldThrowExactly<NoSuchElementException> {
                        winningLottoService.getWinningLottoByRound(TEST_ADMIN_USER_ID, TEST_INVALID_ROUND)
                    }
                }
            }

            context("일반 유저가 요청할 때") {
                every { userRepository.getByUserId(any()).userRole } returns UserRole.ROLE_USER
                every { winningLottoInformation.round } returns TEST_ROUND + 1 // 현재 라운드

                it("정상 종료: 현재 라운드가 아닌 이전 라운드 접근") {
                    every { winningLottoRepository.getByRound(any()) } returns lotto
                    shouldNotThrowAny {
                        winningLottoService.getWinningLottoByRound(TEST_USER_ID, TEST_ROUND)
                    }
                }

                it("[IllegalArgumentException] 예외 발생: 현재 라운드 접근시") {
                    shouldThrowExactly<IllegalArgumentException> {
                        winningLottoService.getWinningLottoByRound(TEST_USER_ID, TEST_ROUND + 1)
                    }
                }

                it("[NoSuchElementException] 예외 발생: 존재하지 않는 라운드") {
                    every { winningLottoRepository.getByRound(any()) } throws NoSuchElementException("존재하지 않는 라운드")
                    shouldThrowExactly<NoSuchElementException> {
                        winningLottoService.getWinningLottoByRound(TEST_USER_ID, TEST_INVALID_ROUND)
                    }
                }
            }
        }
    },
)
