package com.hm.hyeonminshinlottospring.domain.lotto.domain

import com.hm.hyeonminshinlottospring.domain.user.domain.User
import com.hm.hyeonminshinlottospring.global.support.doamin.BaseTimeEntity
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.util.SortedSet

// TODO: 최적화 솔루션) numbers를 bitset으로 다루기!!

@Entity
class Lotto(
    @Column(nullable = false, updatable = false)
    val round: Int,
    numbers: SortedSet<LottoNumber>,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", updatable = false)
    val user: User,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lotto_id")
    val id: Long? = null,
) : BaseTimeEntity() {
    // TODO: bonus 번호 프로퍼티를 추가하기
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "lotto_numbers", joinColumns = [JoinColumn(name = "lotto_id")])
    var numbers: SortedSet<LottoNumber> = numbers
        protected set

    init {
        // 중복되지 않은 번호 개수 체크
        require(numbers.size == NUM_OF_LOTTO_NUMBERS) { "중복을 제외한 ${NUM_OF_LOTTO_NUMBERS}개의 번호가 필요합니다." }
    }

    constructor(
        round: Int,
        numbers: Collection<Int>,
        user: User,
        id: Long? = null,
        @Suppress("UNUSED_PARAMETER") dummyImplicit: Any? = null,
    ) : this(
        round = round,
        user = user,
        numbers = numbers.map { LottoNumber(it) }.toSortedSet(),
        id = id,
    )

    fun match(other: Lotto): List<Int> {
        val intersect = this.numbers.intersect(other.numbers)
        return intersect.map { it.number }.toList()
    }

    override fun toString() = numbers.joinToString(" ")

    companion object {
        const val NUM_OF_LOTTO_NUMBERS = 6
    }
}
