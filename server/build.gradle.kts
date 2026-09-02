plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
}

group = "com.example.anima"
version = "1.0.0"
application {
    mainClass = "com.anima.ApplicationKt"
}

dependencies {
    api(project(":core"))
    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
    implementation(libs.hibernate.core)
    implementation(libs.jakarta.persistence)
    implementation(libs.postgres.driver)
    implementation(libs.hikari.cp)
}