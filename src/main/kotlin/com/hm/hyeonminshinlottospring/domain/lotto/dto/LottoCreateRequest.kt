package com.hm.hyeonminshinlottospring.domain.lotto.dto

import com.hm.hyeonminshinlottospring.domain.lotto.domain.GenerateMode
import com.hm.hyeonminshinlottospring.domain.lotto.domain.Lotto
import com.hm.hyeonminshinlottospring.domain.lotto.domain.info.LottoPrice
import com.hm.hyeonminshinlottospring.domain.lotto.service.generator.RandomLottoNumbersGenerator
import com.hm.hyeonminshinlottospring.domain.user.domain.User
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class LottoCreateRequest(
    @field:NotNull(message = "유저 ID는 필수 입력 값입니다.")
    @field:Positive(message = "유저 ID는 0보다 커야 합니다.")
    val userId: Long,
    @field:NotNull(message = "생성 모드는 필수 입력 값입니다.")
    val mode: GenerateMode,
    @field:NotNull(message = "투입할 돈은 필수 입력 값입니다.")
    @field:Positive(message = "투입할 돈은 양수여야 합니다.")
    val insertedMoney: Int,
    val numbers: List<List<Int>>? = null,
) {
    fun toEntities(
        user: User,
        round: Int,
        randomLottoNumbersGenerator: RandomLottoNumbersGenerator,
    ) = createLottosWithMode(user, round, randomLottoNumbersGenerator)

    private fun createLottosWithMode(
        user: User,
        round: Int,
        randomLottoNumbersGenerator: RandomLottoNumbersGenerator,
    ): List<Lotto> {
        val result = mutableListOf<Lotto>()
        val generateCount = this.insertedMoney / LottoPrice.PER_PRICE
        when (this.mode) {
            GenerateMode.RANDOM ->
                repeat(generateCount) {
                    result.add(
                        Lotto(
                            round = round,
                            user = user,
                            numbers = randomLottoNumbersGenerator.generate(),
                        ),
                    )
                }

            GenerateMode.MANUAL -> {
                val numbersIterator = this.numbers?.listIterator() ?: listOf<List<Int>>().listIterator()
                while (numbersIterator.hasNext()) {
                    result.add(
                        Lotto(
                            round = round,
                            user = user,
                            numbers = numbersIterator.next(),
                        ),
                    )
                }
            }
        }
        return result.toList()
    }
}
