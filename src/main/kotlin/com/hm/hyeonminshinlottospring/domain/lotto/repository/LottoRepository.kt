package com.hm.hyeonminshinlottospring.domain.lotto.repository

import com.hm.hyeonminshinlottospring.domain.lotto.domain.Lotto
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

fun LottoRepository.findByLottoId(lottoId: Long): Lotto =
    findByIdOrNull(lottoId) ?: throw NoSuchElementException("$lottoId: 해당 로또가 존재하지 않습니다.")

@Repository
interface LottoRepository : JpaRepository<Lotto, Long> {
    fun findSliceByUserId(
        userId: Long,
        pageable: Pageable,
    ): Slice<Lotto>

    fun findSliceByRound(
        round: Int,
        pageable: Pageable,
    ): Slice<Lotto>

    fun findSliceByUserIdAndRound(
        userId: Long,
        round: Int,
        pageable: Pageable,
    ): Slice<Lotto>

    fun findListByUserIdAndRound(
        userId: Long,
        round: Int,
    ): List<Lotto>

    fun findFirstByOrderByRoundDesc(): Lotto?
}
