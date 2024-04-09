package com.hm.hyeonminshinlottospring.domain.lotto.controller

import com.hm.hyeonminshinlottospring.domain.lotto.dto.LottoCreateRequest
import com.hm.hyeonminshinlottospring.domain.lotto.dto.LottoCreateResponse
import com.hm.hyeonminshinlottospring.domain.lotto.dto.SliceLottoNumberResponse
import com.hm.hyeonminshinlottospring.domain.lotto.service.LottoService
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
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
        @Valid
        @RequestBody
        lottoCreateRequest: LottoCreateRequest,
    ): ResponseEntity<LottoCreateResponse> {
        val response = lottoService.createLottos(lottoCreateRequest)
        return ResponseEntity.created(URI.create("/api/v1/lotto/${response.userId}"))
            .body(response)
    }

    @GetMapping("/{userId}")
    fun getlottosByUserAndRound(
        @Positive(message = "유저 ID는 0보다 커야 합니다.")
        @PathVariable("userId")
        userId: Long,
        @Positive(message = "라운드는 양수여야 합니다.")
        @RequestParam("round")
        round: Int,
        @PageableDefault(page = 0, size = 10)
        pageable: Pageable,
    ): ResponseEntity<SliceLottoNumberResponse> {
        val response = lottoService.getLottosByUserAndRound(userId, round, pageable)
        return ResponseEntity.ok(response)
    }
}
