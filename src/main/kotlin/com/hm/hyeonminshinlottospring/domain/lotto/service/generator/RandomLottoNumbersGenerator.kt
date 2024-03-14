package com.hm.hyeonminshinlottospring.domain.lotto.service.generator

import com.hm.hyeonminshinlottospring.domain.lotto.domain.Lotto
import com.hm.hyeonminshinlottospring.domain.lotto.domain.LottoNumber
import org.springframework.stereotype.Component

@Component
class RandomLottoNumbersGenerator {
    fun generate() =
        (LottoNumber.LOTTO_MIN_NUMBER..LottoNumber.LOTTO_MAX_NUMBER)
            .shuffled()
            .take(Lotto.NUM_OF_LOTTO_NUMBERS)
            .toSortedSet()
}
