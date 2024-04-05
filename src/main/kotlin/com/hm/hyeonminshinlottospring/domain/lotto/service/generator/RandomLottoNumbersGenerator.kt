package com.hm.hyeonminshinlottospring.domain.lotto.service.generator

import com.hm.hyeonminshinlottospring.domain.lotto.domain.LottoNumbers
import org.springframework.stereotype.Component

@Component
class RandomLottoNumbersGenerator {
    suspend fun generate() =
        (LottoNumbers.LOTTO_MIN_NUMBER..LottoNumbers.LOTTO_MAX_NUMBER)
            .shuffled()
            .take(LottoNumbers.NUM_OF_LOTTO_NUMBERS)
}
