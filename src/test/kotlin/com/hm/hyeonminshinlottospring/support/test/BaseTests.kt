package com.hm.hyeonminshinlottospring.support.test

import com.hm.hyeonminshinlottospring.global.config.JpaAuditingConfig
import com.hm.hyeonminshinlottospring.global.config.SchedulingConfig
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
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

    // TODO: application-database.yml에서 [schema,data].sql 파일 생성 후 RepositoryTest 사용 가능
    @TestEnvironment
    @Target(AnnotationTarget.CLASS)
    @Import(JpaAuditingConfig::class)
    @Retention(AnnotationRetention.RUNTIME)
    @DataJpaTest(properties = ["spring.jpa.hibernate.ddl-auto=none"])
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    annotation class RepositoryTest

    @TestEnvironment
    @AutoConfigureRestDocs
    @Import(SchedulingConfig::class)
    @ExtendWith(RestDocumentationExtension::class)
    annotation class UnitControllerTestEnvironment

    @TestEnvironment
    @SpringBootTest
//    @Import(TestMockBeanConfig::class)
    annotation class IntegrationTest
}
