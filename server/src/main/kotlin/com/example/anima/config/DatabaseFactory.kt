package com.example.anima.config

import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Persistence

object DatabaseFactory {
    lateinit var emf: EntityManagerFactory

    fun init() {
        val host = System.getenv("POSTGRES_HOST") ?: "localhost"
        val db = requireNotNull(System.getenv("POSTGRES_DB")) { "POSTGRES_DB not defined" }
        val user = requireNotNull(System.getenv("POSTGRES_USER")) { "POSTGRES_USER not defined" }
        val password =
            requireNotNull(System.getenv("POSTGRES_PASSWORD")) { "POSTGRES_PASSWORD not defined" }

        val overrides = mapOf(
            "jakarta.persistence.jdbc.url" to "jdbc:postgresql://$host:5432/$db",
            "jakarta.persistence.jdbc.user" to user,
            "jakarta.persistence.jdbc.password" to password,
            "jakarta.persistence.jdbc.driver" to "org.postgresql.Driver"
        )
        emf = Persistence.createEntityManagerFactory("anima", overrides)
    }
}