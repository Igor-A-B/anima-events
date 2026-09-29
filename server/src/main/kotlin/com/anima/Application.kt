package com.anima

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class AnimaServerApplication

fun main(args: Array<String>) {
    runApplication<AnimaServerApplication>(*args)
}
