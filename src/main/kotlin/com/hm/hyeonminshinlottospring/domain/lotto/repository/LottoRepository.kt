package com.hm.hyeonminshinlottospring.domain.lotto.repository

import com.hm.hyeonminshinlottospring.domain.lotto.domain.Lotto
import com.hm.hyeonminshinlottospring.domain.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

fun LottoRepository.getByLottoId(lottoId: Long): Lotto =
    findByIdOrNull(
        lottoId,
    ) ?: throw NoSuchElementException("$lottoId: 해당 로또가 존재하지 않습니다.")

// TODO: userId로 바꾸기
fun LottoRepository.getByUser(user: User): List<Lotto> = findAllByUser(user)

fun LottoRepository.getByRound(round: Int): List<Lotto> = findAllByRound(round)

fun LottoRepository.getByUserAndRound(
    user: User,
    round: Int,
): List<Lotto> = findAllByUserAndRound(user, round)

@Repository
interface LottoRepository : JpaRepository<Lotto, Long> {
    fun findAllByUser(user: User): List<Lotto>

    fun findAllByRound(round: Int): List<Lotto>

    fun findAllByUserAndRound(
        user: User,
        round: Int,
    ): List<Lotto>
}
