package com.hm.hyeonminshinlottospring.domain.lotto.domain

import com.hm.hyeonminshinlottospring.domain.lotto.domain.converter.LottoNumberAttributeConverter
import com.hm.hyeonminshinlottospring.domain.user.domain.User
import com.hm.hyeonminshinlottospring.global.support.doamin.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

// TODO: 최적화 솔루션) numbers를 bitset으로 다루기!!

@Table(
    indexes = [
        Index(name = "user_id_index", columnList = "user_id"),
    ],
)
@SequenceGenerator(
    name = "LOTTO_SEQ_GENERATOR",
    sequenceName = "LOTTO_SEQ",
    initialValue = 1,
    allocationSize = 1,
)
@Entity
class Lotto(
    @Column(nullable = false, updatable = false)
    val round: Int,
    @Convert(converter = LottoNumberAttributeConverter::class)
    val numbers: LottoNumber,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false)
    val user: User,
    @Id
    @Column(columnDefinition = "NUMERIC(19, 0)")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LOTTO_SEQ_GENERATOR")
    val id: Long = 0L,
) : BaseTimeEntity() {
    // TODO: bonus 번호 프로퍼티를 추가하기
    constructor(
        round: Int,
        numbers: Collection<Int>,
        user: User,
        id: Long = 0L,
    ) : this(
        round = round,
        user = user,
        numbers = LottoNumber(numbers),
        id = id,
    )

    fun match(other: Lotto): List<Int> {
        val intersect = this.numbers.intersect(other.numbers)
        return intersect.toList()
    }
}
