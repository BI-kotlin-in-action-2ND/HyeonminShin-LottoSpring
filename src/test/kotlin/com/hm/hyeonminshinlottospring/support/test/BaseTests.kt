package com.hm.hyeonminshinlottospring.support.test

import com.hm.hyeonminshinlottospring.global.config.JpaAuditingConfig
import com.hm.hyeonminshinlottospring.global.config.SchedulingConfig
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor

class BaseTests {
    @ActiveProfiles("test")
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.RUNTIME)
    @TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
    annotation class TestEnvironment

    @TestEnvironment
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.RUNTIME)
    @Import(JpaAuditingConfig::class)
    @DataJpaTest(properties = ["spring.jpa.hibernate.ddl-auto=none"])
    annotation class RepositoryTest

    @TestEnvironment
    @AutoConfigureRestDocs
    @Import(SchedulingConfig::class)
    @ExtendWith(RestDocumentationExtension::class)
    annotation class UnitControllerTestEnvironment

    @TestEnvironment
    @SpringBootTest
    annotation class IntegrationTest
}
