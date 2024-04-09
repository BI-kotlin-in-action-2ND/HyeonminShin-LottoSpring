package com.hm.hyeonminshinlottospring.global.config

import org.slf4j.MDC
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.core.task.TaskDecorator
import org.springframework.core.task.support.TaskExecutorAdapter
import org.springframework.scheduling.annotation.EnableAsync
import java.util.concurrent.Executors

/**
 * @Async 사용시 virtual thread 사용하도록 바꾸는 설정.
 *
 * ### 사용 예시
 * ```kotlin
 * import org.springframework.scheduling.annotation.Async
 * import org.springframework.stereotype.Service
 *
 * @Service
 * class FooService {
 *
 *     @Async(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
 *     fun doSomething() {
 *         // doSomething
 *     }
 * }
 * ```
 */
@Configuration
@EnableAsync
class AsyncConfig {

    @Bean(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    fun asyncTaskExecutor(): AsyncTaskExecutor {
        val taskExecutor = TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor())
        taskExecutor.setTaskDecorator(LoggingTaskDecorator())

        return taskExecutor
    }
}

class LoggingTaskDecorator : TaskDecorator {

    override fun decorate(task: Runnable): Runnable {
        val callerThreadContext = MDC.getCopyOfContextMap()

        return Runnable {
            callerThreadContext?.let {
                MDC.setContextMap(it)
            }
            task.run()
        }
    }
}
