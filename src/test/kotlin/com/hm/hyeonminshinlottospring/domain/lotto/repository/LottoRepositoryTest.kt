package com.hm.hyeonminshinlottospring.domain.lotto.repository

import com.hm.hyeonminshinlottospring.domain.user.repository.UserRepository
import com.hm.hyeonminshinlottospring.support.TEST_DEFAULT_PAGEABLE
import com.hm.hyeonminshinlottospring.support.TEST_INVALID_LOTTO_ID
import com.hm.hyeonminshinlottospring.support.TEST_NOT_EXIST_ROUND
import com.hm.hyeonminshinlottospring.support.TEST_ROUND
import com.hm.hyeonminshinlottospring.support.createLotto
import com.hm.hyeonminshinlottospring.support.createOtherLotto
import com.hm.hyeonminshinlottospring.support.createOtherUser
import com.hm.hyeonminshinlottospring.support.createUser
import com.hm.hyeonminshinlottospring.support.test.BaseTests.RepositoryTest
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe

@RepositoryTest
class LottoRepositoryTest(
    private val lottoRepository: LottoRepository,
    private val userRepository: UserRepository,
) : ExpectSpec(
    {
        val user1 = userRepository.save(createUser())
        val user2 = userRepository.save(createOtherUser())
        val lotto1 = lottoRepository.save(createLotto(user1))
        val sameLotto1 = lottoRepository.save(createLotto(user1))
        val lotto2 = lottoRepository.save(createOtherLotto(user2))
        val pageable = TEST_DEFAULT_PAGEABLE

        // 순서 주의!
        afterSpec {
            lottoRepository.deleteAll()
            userRepository.deleteAll()
        }

        context("단순 로또 조회") {
            expect("로또 ID와 일치하는 로또 조회") {
                val result = lottoRepository.findByLottoId(lotto1.id)
                result.user.userName shouldBe user1.userName
            }

            expect("존재하지 않는 로또 ID로 조회") {
                shouldThrowExactly<NoSuchElementException> {
                    lottoRepository.findByLottoId(TEST_INVALID_LOTTO_ID)
                }
            }

            expect("유저와 일치하는 모든 로또 조회") {
                val result = lottoRepository.findSliceByUserId(user2.id, pageable)
                result.content.size shouldBe 1
            }
        }

        context("라운드에 해당하는 로또가 있는지 조회") {
            expect("존재하는 라운드로 조회") {
                val result = lottoRepository.findSliceByRound(TEST_ROUND, pageable)
                result.content.size shouldBeGreaterThan 0
            }

            expect("존재하지 않는 라운드로 조회") {
                val result = lottoRepository.findSliceByRound(TEST_NOT_EXIST_ROUND, pageable)
                result.content.size shouldBe 0
            }
        }

        context("유저와 라운드를 이용한 조회") {
            expect("Slice: 유저와 라운드가 존재할 때") {
                val result = lottoRepository.findSliceByUserIdAndRound(user1.id, lotto1.round, pageable)
                result.content.size shouldBe 2
            }

            expect("List: 유저와 라운드가 존재할 때") {
                val result = lottoRepository.findListByUserIdAndRound(user1.id, lotto1.round)
                result.size shouldBe 2
            }
        }
    },
)
