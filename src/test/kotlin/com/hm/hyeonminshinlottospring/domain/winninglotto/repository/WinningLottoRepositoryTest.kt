package com.hm.hyeonminshinlottospring.domain.winninglotto.repository

import com.hm.hyeonminshinlottospring.domain.lotto.repository.LottoRepository
import com.hm.hyeonminshinlottospring.domain.user.domain.UserRole
import com.hm.hyeonminshinlottospring.domain.user.repository.UserRepository
import com.hm.hyeonminshinlottospring.domain.winninglotto.domain.WinningLotto
import com.hm.hyeonminshinlottospring.support.TEST_INVALID_ROUND
import com.hm.hyeonminshinlottospring.support.createAdmin
import com.hm.hyeonminshinlottospring.support.createLotto
import com.hm.hyeonminshinlottospring.support.createUser
import com.hm.hyeonminshinlottospring.support.test.BaseTests.RepositoryTest
import io.kotest.assertions.fail
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe

@RepositoryTest
class WinningLottoRepositoryTest(
    private val userRepository: UserRepository,
    private val lottoRepository: LottoRepository,
    private val winningLottoRepository: WinningLottoRepository,
) : ExpectSpec(
    {
        val user = userRepository.save(createUser())
        val admin = userRepository.save(createAdmin())
        val lotto1 = lottoRepository.save(createLotto(admin))
        val winLotto1 = winningLottoRepository.save(
            WinningLotto(
                round = lotto1.round,
                lotto = lotto1,
            ),
        )

        // 순서 주의!
        afterSpec {
            winningLottoRepository.deleteAll()
            lottoRepository.deleteAll()
            userRepository.deleteAll()
        }

        context("당첨 로또 번호 조회") {
            expect("존재하는 라운드 조회") {
                val result = winningLottoRepository.findByRound(lotto1.round)
                if (result != null) {
                    result.lotto.user.userRole shouldBe UserRole.ROLE_ADMIN
                } else {
                    fail("존재하는 라운드를 제공해주세요.")
                }
            }

            expect("존재하지 않는 라운드 조회") {
                val result = winningLottoRepository.findByRound(TEST_INVALID_ROUND)
                result shouldBe null
            }
        }
    },
)
