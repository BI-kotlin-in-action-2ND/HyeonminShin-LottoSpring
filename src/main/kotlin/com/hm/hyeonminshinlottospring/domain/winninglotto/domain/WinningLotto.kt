package com.hm.hyeonminshinlottospring.domain.winninglotto.domain

import com.hm.hyeonminshinlottospring.domain.lotto.domain.Lotto
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Table(
    indexes = [
        Index(name = "lotto_id_index", columnList = "lotto_id"),
    ],
)
@Entity
class WinningLotto(
    val round: Int,
    @OneToOne
    @JoinColumn(name = "lotto_id", updatable = false, nullable = false)
    val lotto: Lotto,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
)
