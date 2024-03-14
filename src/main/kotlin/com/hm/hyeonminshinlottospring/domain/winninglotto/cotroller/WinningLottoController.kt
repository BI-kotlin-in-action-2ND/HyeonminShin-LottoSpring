package com.hm.hyeonminshinlottospring.domain.winninglotto.cotroller

import com.hm.hyeonminshinlottospring.domain.winninglotto.dto.WinningLottoMatchResponse
import com.hm.hyeonminshinlottospring.domain.winninglotto.dto.WinningLottoRoundResponse
import com.hm.hyeonminshinlottospring.domain.winninglotto.service.WinningLottoService
import com.hm.hyeonminshinlottospring.global.support.dto.SliceResponse
import jakarta.validation.constraints.Positive
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/api/v1/winninglotto")
class WinningLottoController(
    private val winningLottoService: WinningLottoService,
) {
    @Scheduled(fixedRate = PER_ROUND_TIME)
    fun createWinningLotto() {
        winningLottoService.createWinningLotto()
    }

    @GetMapping
    fun getWinningLottoByRound(
        @Positive(message = "유저 ID는 0보다 커야 합니다.")
        @RequestParam("userId")
        userId: Long,
        @Positive(message = "라운드는 양수여야 합니다.")
        @RequestParam("round")
        round: Int,
    ): ResponseEntity<WinningLottoRoundResponse> {
        val response = winningLottoService.getWinningLottoByRound(userId, round)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/match")
    fun matchUserLottoByRound(
        @Positive(message = "유저 ID는 0보다 커야 합니다.")
        @RequestParam("userId")
        userId: Long,
        @Positive(message = "라운드는 양수여야 합니다.")
        @RequestParam("round")
        round: Int,
    ): ResponseEntity<SliceResponse<WinningLottoMatchResponse>> {
        val response = winningLottoService.matchUserLottoByRound(userId, round)
        return ResponseEntity.ok(response)
    }

    companion object {
        private const val SECOND = 1000L
        private const val MIN = 60L * SECOND
        private const val HOUR = 60L * MIN

        const val PER_ROUND_TIME = 10L * MIN
    }
}
