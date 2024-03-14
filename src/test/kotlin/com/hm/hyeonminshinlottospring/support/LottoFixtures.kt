package com.hm.hyeonminshinlottospring.support

import com.hm.hyeonminshinlottospring.domain.lotto.domain.GenerateMode
import com.hm.hyeonminshinlottospring.domain.lotto.domain.Lotto
import com.hm.hyeonminshinlottospring.domain.lotto.domain.LottoNumber
import com.hm.hyeonminshinlottospring.domain.lotto.dto.LottoCreateRequest
import com.hm.hyeonminshinlottospring.domain.user.domain.User
import java.util.SortedSet

const val TEST_LOTTO_ID = 12L
const val TEST_OTHER_LOTTO_ID = 13L
const val TEST_ANOTHER_LOTTO_ID = 14L
const val TEST_NOT_EXIST_LOTTO_ID = 15L
const val TEST_INVALID_LOTTO_ID = -1L
const val TEST_ROUND = 1001
const val TEST_OTHER_ROUND = 1002
const val TEST_ANOTHER_ROUND = 1003
const val TEST_NOT_EXIST_ROUND = 4
const val TEST_INVALID_ROUND = -1
const val TEST_GENERATE_COUNT = 5
val TEST_NUMBER =
    (LottoNumber.LOTTO_MIN_NUMBER..LottoNumber.LOTTO_MAX_NUMBER)
        .shuffled()
        .take(Lotto.NUM_OF_LOTTO_NUMBERS)
        .toSortedSet()
val TEST_NUMBER_2 =
    (LottoNumber.LOTTO_MIN_NUMBER..LottoNumber.LOTTO_MAX_NUMBER)
        .shuffled()
        .take(Lotto.NUM_OF_LOTTO_NUMBERS)
        .toSortedSet()
val TEST_OTHER_NUMBER =
    (LottoNumber.LOTTO_MIN_NUMBER..LottoNumber.LOTTO_MAX_NUMBER)
        .shuffled()
        .take(Lotto.NUM_OF_LOTTO_NUMBERS)
        .toSortedSet()
val TEST_NUMBER_NODUP_BUT_OVERSIZE =
    (LottoNumber.LOTTO_MIN_NUMBER..LottoNumber.LOTTO_MAX_NUMBER)
        .shuffled()
        .take(Lotto.NUM_OF_LOTTO_NUMBERS + 1)
        .toSortedSet()
val TEST_NUMBER_NODUP_BUT_UNDERSIZE =
    (LottoNumber.LOTTO_MIN_NUMBER..LottoNumber.LOTTO_MAX_NUMBER)
        .shuffled()
        .take(Lotto.NUM_OF_LOTTO_NUMBERS - 1)
        .toSortedSet()
val TEST_NUMBER_NORMALSIZE_BUT_DUP =
    (LottoNumber.LOTTO_MIN_NUMBER..<LottoNumber.LOTTO_MAX_NUMBER)
        .take(Lotto.NUM_OF_LOTTO_NUMBERS - 2) + LottoNumber.LOTTO_MAX_NUMBER + LottoNumber.LOTTO_MAX_NUMBER

fun createLotto(
    user: User,
    numbers: SortedSet<LottoNumber> = TEST_NUMBER,
) = Lotto(
    round = TEST_ROUND,
    numbers = numbers,
    user = user,
//        id = TEST_LOTTO_ID,
)

fun createOtherLotto(
    user: User,
    numbers: SortedSet<LottoNumber> = TEST_OTHER_NUMBER,
) = Lotto(
    round = TEST_ROUND,
    numbers = numbers,
    user = user,
//    id = TEST_OTHER_LOTTO_ID,
)

@JvmName("lottoWithInt")
fun createCustomLotto(
    round: Int,
    numbers: Collection<Int>,
    user: User,
    id: Long? = null,
) = Lotto(
    round = round,
    numbers = numbers,
    user = user,
    id = id,
)

@JvmName("lottoWithLottoNumber")
fun createCustomLotto(
    round: Int,
    numbers: Collection<LottoNumber>,
    user: User,
    id: Long? = null,
) = Lotto(
    round = round,
    numbers = numbers.toSortedSet(),
    user = user,
    id = id,
)

fun createLottoCreateRequest(
    round: Int = TEST_ROUND,
    mode: GenerateMode = GenerateMode.RANDOM,
    generateCount: Int = TEST_GENERATE_COUNT,
    numbers: List<List<Int>>?,
) = LottoCreateRequest(
    round = round,
    mode = mode,
    generateCount = generateCount,
    numbers = numbers,
)

fun createLottoNumbers(count: Int = 1): List<List<Int>> {
    val mutableList = mutableListOf<List<Int>>()
    repeat(count) {
        mutableList.add(
            (LottoNumber.RAW_LOTTO_MIN_NUMBER..LottoNumber.RAW_LOTTO_MAX_NUMBER)
                .shuffled()
                .take(Lotto.NUM_OF_LOTTO_NUMBERS),
        )
    }
    return mutableList.toList()
}

fun createAllLottoWithUser(user: User) = listOf(createLotto(user), createLotto(user, TEST_NUMBER_2))
