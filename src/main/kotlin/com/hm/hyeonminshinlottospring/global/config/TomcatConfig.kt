package com.hm.hyeonminshinlottospring.global.config

import org.apache.coyote.ProtocolHandler
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.Executors

/**
 * Tomcat으로 들어오는 request들을 platform thread를 사용하는 대신 virtual thread를 사용하게 만드는 설정
 */
@Configuration
class TomcatConfig {

    @Bean
    fun protocolHandlerVirtualThreadExecutorCustomizer(): TomcatProtocolHandlerCustomizer<*>? {
        return TomcatProtocolHandlerCustomizer<ProtocolHandler> { protocolHandler: ProtocolHandler ->
            protocolHandler.executor = Executors.newVirtualThreadPerTaskExecutor()
        }
    }
}
