package com.hm.hyeonminshinlottospring.global.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 사용법:
 * val objectMapper = JacksonConfig().objectMapper()
 */

@Configuration
class JacksonConfig {
    @Bean
    fun objectMapper(): ObjectMapper {
        val objectMapper = ObjectMapper()
        val javaTimeModule = JavaTimeModule()

        // LocalDateTime 커스텀 포멧으로 직렬화/역직렬화
        // javaTimeModule.addSerializer(LocalDateTime::class, CustomLocalDateTimeSerializer())
        // javaTimeModule.addDeserializer(LocalDateTime::class, CustomLocalDateTimeDeserializer())
        objectMapper.registerModule(javaTimeModule)

        // 아직 불러오지 않은 엔티티에 대해 null 값 내려주는 모듈 - lazy loading
        objectMapper.registerModule(Hibernate6Module())

        // 모르는 property에 대해 무시하고 넘어간다.
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        objectMapper.registerModule(
            KotlinModule.Builder()
                .withReflectionCacheSize(512)
                .configure(KotlinFeature.NullToEmptyCollection, false)
                .configure(KotlinFeature.NullToEmptyMap, false)
                .configure(KotlinFeature.NullIsSameAsDefault, false)
                .configure(KotlinFeature.SingletonSupport, false)
                .configure(KotlinFeature.StrictNullChecks, false)
                .build(),
        )

        // 시간 관련 객체(LocalDateTime, java.util.Date)를 직렬화할 때 timestamp 숫자값이 아닌 포맷팅 문자열로 한다.
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

        return objectMapper
    }
}
