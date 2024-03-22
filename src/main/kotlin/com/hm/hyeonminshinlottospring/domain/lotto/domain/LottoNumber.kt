package com.hm.hyeonminshinlottospring.domain.lotto.domain

import java.util.SortedSet

@JvmInline
value class LottoNumber private constructor(private val number: SortedSet<Int>) {
    init {
        require(number.size == NUM_OF_LOTTO_NUMBERS) { "중복을 제외한 ${NUM_OF_LOTTO_NUMBERS}개의 번호가 필요합니다." }
        require(number.all { it in VALID_RANGE }) { NUMBER_RANGE_ERROR_MESSAGE }
    }

    constructor(number: Collection<Int>) : this(number.toSortedSet())

    fun joinToString() = this.number.joinToString(DELIMITER)
    fun intersect(other: LottoNumber) = this.number.intersect(other.number)

    fun count(other: LottoNumber) = this.number.count { it in other.number }

    companion object {
        const val LOTTO_MIN_NUMBER = 1
        const val LOTTO_MAX_NUMBER = 45

        const val NUM_OF_LOTTO_NUMBERS = 6

        private val VALID_RANGE: IntRange = LOTTO_MIN_NUMBER..LOTTO_MAX_NUMBER

        const val DELIMITER = ","

        const val NUMBER_RANGE_ERROR_MESSAGE =
            "로또 번호는 [$LOTTO_MIN_NUMBER ~ $LOTTO_MAX_NUMBER] 범위여야 합니다."

        @JvmStatic
        fun from(number: Collection<Int>) = LottoNumber(number.toSortedSet())
    }
}
