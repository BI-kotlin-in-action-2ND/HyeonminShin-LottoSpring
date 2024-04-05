import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    kotlin("plugin.jpa") version "1.9.22"
}

// Proxy 객체를 위한 설정
allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

noArg {
    annotation("com.hm.hyeonminshinlottospring.global.support.doamin.NoArgsConstructor")
}

group = "com.hm"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
}

val asciidoctorExt: Configuration by configurations.creating
val snippetsDir by extra { file("build/generated-snippets") }

dependencies {
    // Common
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-hibernate6")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    // Coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1-Beta")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.8.0")
    // Database
    implementation("org.flywaydb:flyway-core")
    implementation("org.hibernate.orm:hibernate-core:6.4.4.Final")
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.0") // hibernate 6 - for logging SQL
    runtimeOnly("com.h2database:h2:2.2.222")
    runtimeOnly("org.postgresql:postgresql")
    // Test
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation("io.kotest:kotest-runner-junit5:5.6.2")
    testImplementation("io.kotest:kotest-assertions-core:5.6.2")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.2")
    // RestDocs
    asciidoctorExt("org.springframework.restdocs:spring-restdocs-asciidoctor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
}

tasks {
    withType<Test> {
        useJUnitPlatform()
    }

    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict --release 21"
            jvmTarget = "21"
        }
    }

    ktlint {
        verbose = true
    }

    test {
        useJUnitPlatform()
        outputs.dir(snippetsDir)
    }

    asciidoctor {
        doFirst {
            project.delete(files("src/main/resources/static/docs"))
        }
        inputs.dir(snippetsDir)
        configurations("asciidoctorExt")
        sources {
            include("**/index.adoc")
        }
        dependsOn(test)
        baseDirFollowsSourceFile()
    }

    register<Copy>("copyDocs") {
        mustRunAfter("processResources")
        dependsOn(asciidoctor)
        from("${asciidoctor.get().outputDir}/index.html")
        into("src/main/resources/static/docs")
    }

    bootJar {
        dependsOn("copyDocs")
        from("${asciidoctor.get().outputDir}/index.html") {
            into("static/docs")
        }
    }

    build {
        dependsOn("copyDocs")
    }

    named<Jar>("jar") {
        enabled = false
    }
}
