package com.hm.hyeonminshinlottospring.domain.lotto.dto

import com.hm.hyeonminshinlottospring.domain.lotto.domain.GenerateMode
import com.hm.hyeonminshinlottospring.domain.lotto.domain.Lotto
import com.hm.hyeonminshinlottospring.domain.lotto.domain.LottoNumber
import com.hm.hyeonminshinlottospring.domain.user.domain.User
import jakarta.validation.constraints.NotNull

data class LottoCreateRequest(
    @field:NotNull(message = "라운드 정보는 필수 입력 값입니다.")
    val round: Int,
    @field:NotNull(message = "생성 모드는 필수 입력 값입니다.")
    val mode: GenerateMode,
    @field:NotNull(message = "로또 생성 개수는 필수 입력 값입니다.")
    val generateCount: Int,
    val numbers: List<List<Int>>?,
) {
    @JvmName("entityWithInt")
    fun toEntity(
        round: Int,
        user: User,
        numbers: Collection<Int>,
    ) = Lotto(
        round = round,
        user = user,
        numbers = numbers,
    )

    @JvmName("entityWithLottoNumber")
    fun toEntity(
        round: Int,
        user: User,
        numbers: Collection<LottoNumber>,
    ) = Lotto(
        round = round,
        user = user,
        numbers = numbers.toSortedSet(),
    )
}
