package com.anima

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AnimaServerApplication

fun main(args: Array<String>) {
    runApplication<AnimaServerApplication>(*args)
}