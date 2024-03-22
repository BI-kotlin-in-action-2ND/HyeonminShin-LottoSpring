package com.hm.hyeonminshinlottospring

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication
class HyeonminShinLottoSpringApplication

fun main(args: Array<String>) {
    runApplication<HyeonminShinLottoSpringApplication>(*args)
}
