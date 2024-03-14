package com.hm.hyeonminshinlottospring.domain.lotto.controller

import com.hm.hyeonminshinlottospring.domain.lotto.dto.LottoCreateRequest
import com.hm.hyeonminshinlottospring.domain.lotto.dto.LottoResponse
import com.hm.hyeonminshinlottospring.domain.lotto.service.LottoService
import com.hm.hyeonminshinlottospring.global.support.dto.SliceResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@Validated
@RestController
@RequestMapping("/api/v1/lotto")
class LottoController(
    private val lottoService: LottoService,
) {
    @PostMapping
    fun createLottos(
        @Positive(message = "유저 ID는 0보다 커야 합니다.")
        @RequestParam("userId")
        userId: Long,
        @Valid
        @RequestBody
        lottoCreateRequest: LottoCreateRequest,
    ): ResponseEntity<Void> {
        val response = lottoService.createLottos(userId, lottoCreateRequest)
        return ResponseEntity.created(URI.create("/api/v1/lotto/$userId?round=${response.round}&size=${response.size}")).build()
    }

    @GetMapping("/{userId}")
    fun getlottosByUserAndRound(
        @Positive(message = "유저 ID는 0보다 커야 합니다.")
        @PathVariable("userId")
        userId: Long,
        @Positive(message = "라운드는 양수여야 합니다.")
        @RequestParam("round")
        round: Int,
        @Positive(message = "조회할 개수는 양수여야 합니다.")
        @RequestParam("size", defaultValue = "1", required = false)
        size: Int?,
    ): ResponseEntity<SliceResponse<LottoResponse>> {
        val response = lottoService.getLottosByUserAndRound(userId, round, size)
        return ResponseEntity.ok(response)
    }
}
