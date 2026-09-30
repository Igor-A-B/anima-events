package com.example.anima.core.log

// println shows up in logcat (System.out) on android, and in the console on ios and js
object AppLog {
    fun i(tag: String, message: String) {
        println("I/$tag: $message")
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        println("E/$tag: $message")
        throwable?.let { println(it.stackTraceToString()) }
    }
}
