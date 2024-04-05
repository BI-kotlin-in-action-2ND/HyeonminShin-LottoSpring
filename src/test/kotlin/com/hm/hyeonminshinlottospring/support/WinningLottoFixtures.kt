package com.hm.hyeonminshinlottospring.support

import com.hm.hyeonminshinlottospring.domain.lotto.domain.Lotto
import com.hm.hyeonminshinlottospring.domain.lotto.domain.info.LottoRank
import com.hm.hyeonminshinlottospring.domain.winninglotto.domain.WinningLotto
import com.hm.hyeonminshinlottospring.domain.winninglotto.dto.TotalMatchResponse
import com.hm.hyeonminshinlottospring.domain.winninglotto.dto.WinningLottoMatchResponse
import com.hm.hyeonminshinlottospring.domain.winninglotto.dto.WinningLottoRoundResponse

const val TEST_WINNINGLOTTO_ID = 2001L
const val TEST_OTHER_WINNINGLOTTO_ID = 2002L
const val TEST_INVALID_WINNINGLOTTO_ID = -1L

fun createWinningLotto(
    lotto: Lotto,
    round: Int = TEST_ROUND,
    id: Long = TEST_WINNINGLOTTO_ID,
) = WinningLotto(
    lotto = lotto,
    round = round,
    id = id,
)

fun createWinningLottoRoundResponse(winningLotto: WinningLotto) = WinningLottoRoundResponse.from(winningLotto)

fun createWinningLottoMatchResponse(
    lotto: Lotto,
    matched: List<Int>,
    rank: LottoRank,
) = WinningLottoMatchResponse.from(lotto, matched, rank)

fun createListWinningLottoMatchResponse() =
    listOf(
        createWinningLottoMatchResponse(
            lotto = createLotto(createUser(), numbers = listOf(1, 2, 3, 4, 5, 6)),
            matched = listOf(1, 2, 3),
            rank = LottoRank.getRank(3),
        ),
        createWinningLottoMatchResponse(
            lotto = createOtherLotto(createUser(), numbers = listOf(10, 11, 12, 13, 14, 15)),
            matched = listOf(11, 12, 13, 14),
            rank = LottoRank.getRank(4),
        ),
    )

fun createTotalMatchResponse() = TotalMatchResponse.from(createListWinningLottoMatchResponse())
