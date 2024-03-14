package com.hm.hyeonminshinlottospring.domain.winninglotto.repository

import com.hm.hyeonminshinlottospring.domain.lotto.domain.Lotto
import com.hm.hyeonminshinlottospring.domain.lotto.repository.LottoRepository
import com.hm.hyeonminshinlottospring.domain.user.domain.User
import com.hm.hyeonminshinlottospring.domain.user.domain.UserRole
import com.hm.hyeonminshinlottospring.domain.user.repository.UserRepository
import com.hm.hyeonminshinlottospring.domain.winninglotto.domain.WinningLotto
import com.hm.hyeonminshinlottospring.support.TEST_INVALID_ROUND
import com.hm.hyeonminshinlottospring.support.createAdmin
import com.hm.hyeonminshinlottospring.support.createLotto
import com.hm.hyeonminshinlottospring.support.createUser
import com.hm.hyeonminshinlottospring.support.test.BaseTests.IntegrationTest
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import org.springframework.transaction.annotation.Transactional

@IntegrationTest
@Transactional
class WinningLottoRepositoryTest(
    private val userRepository: UserRepository,
    private val lottoRepository: LottoRepository,
    private val winningLottoRepository: WinningLottoRepository,
) : ExpectSpec(
        {
            lateinit var user: User
            lateinit var admin: User
            lateinit var lotto1: Lotto
            lateinit var winLotto1: WinningLotto

            beforeEach {
                user = userRepository.save(createUser())
                admin = userRepository.save(createAdmin())
                lotto1 = lottoRepository.save(createLotto(admin))
                winLotto1 =
                    winningLottoRepository.save(
                        WinningLotto(
                            round = lotto1.round,
                            lotto = lotto1,
                        ),
                    )
            }

            context("당첨 로또 번호 조회") {
                expect("존재하는 라운드 조회") {
                    val result = winningLottoRepository.getByRound(lotto1.round)
                    result.user.userRole shouldBe UserRole.ROLE_ADMIN
                }

                expect("존재하지 않는 라운드 조회") {
                    shouldThrowAny { winningLottoRepository.getByRound(TEST_INVALID_ROUND) }
                }
            }
        },
    )
