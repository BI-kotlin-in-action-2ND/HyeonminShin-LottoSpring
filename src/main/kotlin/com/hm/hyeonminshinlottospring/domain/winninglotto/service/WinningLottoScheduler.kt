package com.hm.hyeonminshinlottospring.domain.winninglotto.service

import com.hm.hyeonminshinlottospring.domain.lotto.domain.Lotto
import com.hm.hyeonminshinlottospring.domain.lotto.repository.LottoRepository
import com.hm.hyeonminshinlottospring.domain.lotto.service.generator.RandomLottoNumbersGenerator
import com.hm.hyeonminshinlottospring.domain.user.domain.User
import com.hm.hyeonminshinlottospring.domain.user.domain.UserRole
import com.hm.hyeonminshinlottospring.domain.user.repository.UserRepository
import com.hm.hyeonminshinlottospring.domain.winninglotto.domain.WinningLotto
import com.hm.hyeonminshinlottospring.domain.winninglotto.domain.WinningLottoInformation
import com.hm.hyeonminshinlottospring.domain.winninglotto.repository.WinningLottoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class WinningLottoScheduler(
    private val winningLottoInformation: WinningLottoInformation,
    private val randomLottoNumbersGenerator: RandomLottoNumbersGenerator,
    private val lottoRepository: LottoRepository,
    private val userRepository: UserRepository,
    private val winningLottoRepository: WinningLottoRepository,
) {
    @Scheduled(cron = "\${schedule.cron}")
    suspend fun createWinningLotto() {
        coroutineScope {
            withContext(Dispatchers.IO) {
                winningLottoInformation.roundMutex.withLock {
                    val round = winningLottoInformation.round
                    winningLottoInformation.increaseRound()
                    val adminDeferred =
                        async {
                            userRepository.save(
                                User(
                                    userName = "LG-R$round",
                                    userRole = UserRole.ROLE_ADMIN,
                                ),
                            )
                        }
                    val savedLottoDeferred =
                        async {
                            lottoRepository.save(
                                Lotto(
                                    round = round,
                                    user = adminDeferred.await(),
                                    numbers = randomLottoNumbersGenerator.generate(),
                                ),
                            )
                        }
                    launch {
                        winningLottoRepository.save(WinningLotto(round, savedLottoDeferred.await()))
                    }
                }
            }
        }
    }
}
