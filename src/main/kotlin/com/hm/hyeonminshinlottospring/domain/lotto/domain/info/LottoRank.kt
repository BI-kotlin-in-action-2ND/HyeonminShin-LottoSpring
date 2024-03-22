package com.hm.hyeonminshinlottospring.domain.lotto.domain.info

import com.hm.hyeonminshinlottospring.domain.lotto.domain.LottoNumber

/**
 * **ordinal**: 등수를 나타내는 지표로 사용 from 0
 */
enum class LottoRank(val rankString: String, val prize: Int) {
    FIRST("1등", 100_000),
    SECOND("2등", 5_000),
    THIRD("3등", 100),
    FOURTH("4등", 5),
    LOSE("낙첨", 0),
    ;

    companion object Checker {
        fun getRank(countMatched: Int) = entries.getOrNull(LottoNumber.NUM_OF_LOTTO_NUMBERS - countMatched) ?: LOSE
    }
}
