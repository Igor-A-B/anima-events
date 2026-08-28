package com.anima.config

import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Persistence

object DatabaseFactory {
    lateinit var emf: EntityManagerFactory

    fun init() {
        // Get credentials from .env file, as the persistence.xml file cannot access .env directly
        val overrides = mapOf(
            "jakarta.persistence.jdbc.url" to System.getenv("POSTGRES_URI"),
            "jakarta.persistence.jdbc.user" to System.getenv("POSTGRES_USER"),
            "jakarta.persistence.jdbc.password" to System.getenv("POSTGRES_PASSWORD"),
            "jakarta.persistence.jdbc.driver" to "org.postgresql.Driver"
        )
        emf = Persistence.createEntityManagerFactory("anima", overrides)
    }
}