package com.example.anima.core.network

// status is null when the server was not reached at all
class ApiException(val status: Int?, message: String) : Exception(message)
