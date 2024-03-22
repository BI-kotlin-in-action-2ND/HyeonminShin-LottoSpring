package com.hm.hyeonminshinlottospring.domain.winninglotto.domain

import com.hm.hyeonminshinlottospring.domain.lotto.domain.Lotto
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

@Table(
    indexes = [
        Index(name = "lotto_id_index", columnList = "lotto_id"),
    ],
)
@SequenceGenerator(
    name = "WINNING_LOTTO_SEQ_GENERATOR",
    sequenceName = "WINNING_LOTTO_SEQ",
    initialValue = 1,
    allocationSize = 1,
)
@Entity
class WinningLotto(
    val round: Int,
    @OneToOne
    @JoinColumn(name = "lotto_id", updatable = false, nullable = false)
    val lotto: Lotto,
    @Id
    @Column(columnDefinition = "NUMERIC(19, 0)")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WINNING_LOTTO_SEQ_GENERATOR")
    val id: Long = 0L,
)
