package com.hm.hyeonminshinlottospring.domain.winninglotto.cotroller

import com.hm.hyeonminshinlottospring.domain.winninglotto.dto.TotalMatchResponse
import com.hm.hyeonminshinlottospring.domain.winninglotto.dto.WinningLottoRoundResponse
import com.hm.hyeonminshinlottospring.domain.winninglotto.service.WinningLottoService
import jakarta.validation.constraints.Positive
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/api/v1/winninglotto")
class WinningLottoController(
    private val winningLottoService: WinningLottoService,
) {
    @GetMapping("/{round}")
    fun getWinningLottoByRound(
        @Positive(message = "유저 ID는 0보다 커야 합니다.")
        @RequestParam("userId")
        userId: Long,
        @Positive(message = "라운드는 양수여야 합니다.")
        @PathVariable("round")
        round: Int,
    ): ResponseEntity<WinningLottoRoundResponse> {
        val response = winningLottoService.getWinningLottoByRound(userId, round)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{round}/match")
    fun matchUserLottoByRound(
        @Positive(message = "유저 ID는 0보다 커야 합니다.")
        @RequestParam("userId")
        userId: Long,
        @Positive(message = "라운드는 양수여야 합니다.")
        @PathVariable("round")
        round: Int,
    ): ResponseEntity<TotalMatchResponse> {
        val response = winningLottoService.matchUserLottoByRound(userId, round)
        return ResponseEntity.ok(response)
    }
}
