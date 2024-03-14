package com.hm.hyeonminshinlottospring.domain.winninglotto.repository

import com.hm.hyeonminshinlottospring.domain.lotto.domain.Lotto
import com.hm.hyeonminshinlottospring.domain.winninglotto.domain.WinningLotto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

fun WinningLottoRepository.getByRound(round: Int): Lotto =
    findByRound(round)?.lotto ?: throw NoSuchElementException("$round 라운드에 존재하는 로또 번호가 없습니다.")

@Repository
interface WinningLottoRepository : JpaRepository<WinningLotto, Long> {
    fun findByRound(round: Int): WinningLotto?
}
