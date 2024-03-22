package com.hm.hyeonminshinlottospring.domain.lotto.service

import com.hm.hyeonminshinlottospring.domain.lotto.dto.LottoCreateRequest
import com.hm.hyeonminshinlottospring.domain.lotto.dto.LottoCreateResponse
import com.hm.hyeonminshinlottospring.domain.lotto.dto.SliceLottoNumberResponse
import com.hm.hyeonminshinlottospring.domain.lotto.repository.LottoRepository
import com.hm.hyeonminshinlottospring.domain.lotto.service.generator.RandomLottoNumbersGenerator
import com.hm.hyeonminshinlottospring.domain.user.domain.UserRole
import com.hm.hyeonminshinlottospring.domain.user.repository.UserRepository
import com.hm.hyeonminshinlottospring.domain.user.repository.findByUserId
import com.hm.hyeonminshinlottospring.domain.winninglotto.domain.WinningLottoInformation
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LottoService(
    private val randomLottoNumbersGenerator: RandomLottoNumbersGenerator,
    private val winningLottoInformation: WinningLottoInformation,
    private val lottoRepository: LottoRepository,
    private val userRepository: UserRepository,
) {
    @Transactional
    fun createLottos(
        request: LottoCreateRequest,
    ): LottoCreateResponse {
        val userId = request.userId
        val user = userRepository.findByUserId(userId)
        user.withdrawMoney(request.insertedMoney)
        val round = winningLottoInformation.round
        val savedLottos = lottoRepository.saveAll(request.toEntities(user, round, randomLottoNumbersGenerator))
        return LottoCreateResponse(userId, round, savedLottos.size)
    }

    @Transactional(readOnly = true)
    fun getLottosByUserAndRound(
        userId: Long,
        round: Int,
        pageable: Pageable,
    ): SliceLottoNumberResponse {
        val user = userRepository.findByUserId(userId)
        check(user.userRole != UserRole.ROLE_ADMIN) { "$userId: 주어진 유저 ID는 조회할 수 없습니다." }
        val slice = lottoRepository.findSliceByUserIdAndRound(userId, round, pageable)
        return SliceLottoNumberResponse.from(slice)
    }
}
