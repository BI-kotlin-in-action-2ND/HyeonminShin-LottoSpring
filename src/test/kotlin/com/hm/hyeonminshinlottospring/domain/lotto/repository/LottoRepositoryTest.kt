package com.hm.hyeonminshinlottospring.domain.lotto.repository

import com.hm.hyeonminshinlottospring.domain.lotto.domain.Lotto
import com.hm.hyeonminshinlottospring.domain.user.domain.User
import com.hm.hyeonminshinlottospring.domain.user.repository.UserRepository
import com.hm.hyeonminshinlottospring.support.TEST_NOT_EXIST_LOTTO_ID
import com.hm.hyeonminshinlottospring.support.TEST_NOT_EXIST_ROUND
import com.hm.hyeonminshinlottospring.support.TEST_NUMBER
import com.hm.hyeonminshinlottospring.support.TEST_ROUND
import com.hm.hyeonminshinlottospring.support.createCustomLotto
import com.hm.hyeonminshinlottospring.support.createLotto
import com.hm.hyeonminshinlottospring.support.createOtherLotto
import com.hm.hyeonminshinlottospring.support.createOtherUser
import com.hm.hyeonminshinlottospring.support.createUser
import com.hm.hyeonminshinlottospring.support.test.BaseTests.IntegrationTest
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import org.springframework.transaction.annotation.Transactional

// @RepositoryTest
@IntegrationTest
@Transactional
class LottoRepositoryTest(
    private val lottoRepository: LottoRepository,
    private val userRepository: UserRepository,
) : ExpectSpec(
        {
            lateinit var user1: User
            lateinit var user2: User
            lateinit var lotto1: Lotto
            lateinit var lotto2: Lotto

            beforeEach {
                user1 = userRepository.save(createUser())
                user2 = userRepository.save(createOtherUser())
                lotto1 = lottoRepository.save(createLotto(user1))
                lotto2 = lottoRepository.save(createOtherLotto(user2))
            }

            context("단순 로또 조회") {
                expect("로또 ID와 일치하는 로또 조회") {
                    val result: Lotto? = lotto1.id?.let { lottoRepository.getByLottoId(it) }
                    result?.numbers shouldBe TEST_NUMBER
                }

                expect("존재하지 않는 로또 ID로 조회") {
                    shouldThrowExactly<NoSuchElementException> {
                        lottoRepository.getByLottoId(TEST_NOT_EXIST_LOTTO_ID)
                    }
                }

                expect("유저와 일치하는 모든 로또 조회") {
                    val result = lottoRepository.getByUser(user1)
                    result.size shouldBe 1
                }
            }

            context("라운드에 해당하는 로또가 있는지 조회") {
                expect("존재하는 라운드로 조회") {
                    val result = lottoRepository.getByRound(TEST_ROUND)
                    result.size shouldBeGreaterThan 0
                }

                expect("존재하지 않는 라운드로 조회") {
                    val result = lottoRepository.getByRound(TEST_NOT_EXIST_ROUND)
                    result.size shouldBeEqual 0
                }
            }

            context("유저와 라운드를 이용한 조회") {
                expect("유저와 라운드가 존재할 때") {
                    val tmpLotto =
                        lottoRepository.save(
                            createCustomLotto(
                                TEST_ROUND,
                                listOf(1, 2, 3, 4, 5, 6),
                                user1,
                            ),
                        )
                    val result = lottoRepository.getByUserAndRound(user1, TEST_ROUND)
                    result.size shouldBe 2

                    lottoRepository.delete(tmpLotto)
                }
            }
        },
    )
