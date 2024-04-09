package com.hm.hyeonminshinlottospring.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler

@Configuration
@EnableScheduling
class SchedulingConfig {

    /**
     * @Scheduled 적용된 테스크의 virtual thread 사용 설정.
     */
    @Bean
    fun taskScheduler(): TaskScheduler {
        return SimpleAsyncTaskScheduler().apply {
            this.setVirtualThreads(true)
            this.setTaskTerminationTimeout(30 * 1000)
        }
    }
}
