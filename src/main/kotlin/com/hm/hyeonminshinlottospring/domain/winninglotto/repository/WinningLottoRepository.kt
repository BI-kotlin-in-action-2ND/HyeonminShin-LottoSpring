package com.hm.hyeonminshinlottospring.domain.winninglotto.repository

import com.hm.hyeonminshinlottospring.domain.winninglotto.domain.WinningLotto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WinningLottoRepository : JpaRepository<WinningLotto, Long> {
    fun findByRound(round: Int): WinningLotto? // ?: throw NoSuchElementException("$round 라운드에 해당하는 당첨 번호가 존재하지 않습니다.")
}
