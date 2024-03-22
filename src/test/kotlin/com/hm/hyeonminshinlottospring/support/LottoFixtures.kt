package com.hm.hyeonminshinlottospring.support

import com.hm.hyeonminshinlottospring.domain.lotto.domain.GenerateMode
import com.hm.hyeonminshinlottospring.domain.lotto.domain.Lotto
import com.hm.hyeonminshinlottospring.domain.lotto.domain.LottoNumber
import com.hm.hyeonminshinlottospring.domain.lotto.dto.LottoCreateRequest
import com.hm.hyeonminshinlottospring.domain.lotto.dto.LottoCreateResponse
import com.hm.hyeonminshinlottospring.domain.lotto.dto.SliceLottoNumberResponse
import com.hm.hyeonminshinlottospring.domain.user.domain.User

const val TEST_LOTTO_ID = 12L
const val TEST_OTHER_LOTTO_ID = 13L
const val TEST_ANOTHER_LOTTO_ID = 14L
const val TEST_NOT_EXIST_LOTTO_ID = 1000L
const val TEST_INVALID_LOTTO_ID = -1L
const val TEST_ROUND = 1001
const val TEST_OTHER_ROUND = 1002
const val TEST_ANOTHER_ROUND = 1003
const val TEST_NOT_EXIST_ROUND = 4
const val TEST_INVALID_ROUND = -1
const val TEST_GENERATE_COUNT_2 = 2
const val TEST_GENERATE_COUNT_10 = 10
val TEST_NUMBER =
    (LottoNumber.LOTTO_MIN_NUMBER..LottoNumber.LOTTO_MAX_NUMBER)
        .shuffled()
        .take(LottoNumber.NUM_OF_LOTTO_NUMBERS)
val TEST_NUMBER_2 =
    (LottoNumber.LOTTO_MIN_NUMBER..LottoNumber.LOTTO_MAX_NUMBER)
        .shuffled()
        .take(LottoNumber.NUM_OF_LOTTO_NUMBERS)
val TEST_OTHER_NUMBER =
    (LottoNumber.LOTTO_MIN_NUMBER..LottoNumber.LOTTO_MAX_NUMBER)
        .shuffled()
        .take(LottoNumber.NUM_OF_LOTTO_NUMBERS)
val TEST_NUMBER_NODUP_BUT_OVERSIZE =
    (LottoNumber.LOTTO_MIN_NUMBER..LottoNumber.LOTTO_MAX_NUMBER)
        .shuffled()
        .take(LottoNumber.NUM_OF_LOTTO_NUMBERS + 1)
val TEST_NUMBER_NODUP_BUT_UNDERSIZE =
    (LottoNumber.LOTTO_MIN_NUMBER..LottoNumber.LOTTO_MAX_NUMBER)
        .shuffled()
        .take(LottoNumber.NUM_OF_LOTTO_NUMBERS - 1)
val TEST_NUMBER_NORMALSIZE_BUT_DUP =
    (LottoNumber.LOTTO_MIN_NUMBER..<LottoNumber.LOTTO_MAX_NUMBER)
        .take(LottoNumber.NUM_OF_LOTTO_NUMBERS - 2) + LottoNumber.LOTTO_MAX_NUMBER + LottoNumber.LOTTO_MAX_NUMBER

fun createLotto(
    user: User,
    numbers: Collection<Int> = TEST_NUMBER,
) = Lotto(
    round = TEST_ROUND,
    numbers = numbers,
    user = user,
)

fun createOtherLotto(
    user: User,
    numbers: Collection<Int> = TEST_OTHER_NUMBER,
) = Lotto(
    round = TEST_ROUND,
    numbers = numbers,
    user = user,
)

fun createCustomLotto(
    round: Int,
    numbers: Collection<Int>,
    user: User,
) = Lotto(
    round = round,
    numbers = numbers,
    user = user,
)

fun createLottoNumber(list: Collection<Int> = TEST_NUMBER) = LottoNumber(list)

fun createLottoNumbers(count: Int = 1): List<List<Int>> {
    val mutableList = mutableListOf<List<Int>>()
    repeat(count) {
        mutableList.add(
            (LottoNumber.LOTTO_MIN_NUMBER..LottoNumber.LOTTO_MAX_NUMBER)
                .shuffled()
                .take(LottoNumber.NUM_OF_LOTTO_NUMBERS),
        )
    }
    return mutableList.toList()
}

fun createAllLotto(user: User) = listOf(createLotto(user), createLotto(user, TEST_NUMBER_2))
fun createLottoCreateRequest(
    userId: Long = TEST_USER_ID,
    insertedMoney: Int = TEST_MONEY_10,
    numbers: List<List<Int>>? = null,
    mode: GenerateMode = GenerateMode.RANDOM,
) = LottoCreateRequest(
    userId = userId,
    mode = mode,
    insertedMoney = insertedMoney,
    numbers = numbers,
)

fun createLottoCreateResponse(
    userId: Long = TEST_USER_ID,
    round: Int = TEST_ROUND,
    createdLottoCount: Int = TEST_GENERATE_COUNT_10,
) = LottoCreateResponse(
    userId = userId,
    round = round,
    createdLottoCount = createdLottoCount,
)

fun createSliceLottoNumberResponse(
    userId: Long? = TEST_USER_ID,
    round: Int? = TEST_ROUND,
    hasNext: Boolean = true,
    numberOfElements: Int = TEST_GENERATE_COUNT_2,
    numberList: List<String>? = null,
) = SliceLottoNumberResponse(
    userId = userId,
    round = round,
    hasNext = hasNext,
    numberOfElements = numberOfElements,
    numberList = createLottoNumbers(numberOfElements).map { it.joinToString() },
)
