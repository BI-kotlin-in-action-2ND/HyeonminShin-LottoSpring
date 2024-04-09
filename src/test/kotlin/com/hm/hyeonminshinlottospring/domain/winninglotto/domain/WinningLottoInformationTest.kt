package com.hm.hyeonminshinlottospring.domain.winninglotto.domain

import com.hm.hyeonminshinlottospring.domain.lotto.repository.LottoRepository
import com.hm.hyeonminshinlottospring.support.TEST_ROUND
import com.hm.hyeonminshinlottospring.support.createLotto
import com.hm.hyeonminshinlottospring.support.createUser
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class WinningLottoInformationTest : ExpectSpec(
    {
        val lottoRepository = mockk<LottoRepository>()

        context("이전 라운드 정보가 존재하지 않을 때") {
            every { lottoRepository.findFirstByOrderByRoundDesc() } returns null
            val winningLottoInformation = WinningLottoInformation(lottoRepository)
            expect("round 1 부터 시작") {
                winningLottoInformation.round shouldBe 1
            }
        }

        context("이전 라운드 정보가 존재할 때") {
            every { lottoRepository.findFirstByOrderByRoundDesc() } returns createLotto(createUser())
            val winningLottoInformation = WinningLottoInformation(lottoRepository)
            expect("마지막 round 다음부터 시작") {
                winningLottoInformation.round shouldBe TEST_ROUND + 1
            }
        }
    },
)
