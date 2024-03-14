package com.hm.hyeonminshinlottospring.domain.winninglotto.domain

import com.hm.hyeonminshinlottospring.domain.lotto.domain.Lotto
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne

@Entity
class WinningLotto(
    val round: Int,
    @OneToOne
    @JoinColumn(name = "lotto_id", updatable = false, nullable = false)
    val lotto: Lotto,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "winning_lotto_id")
    val id: Long? = null,
)
