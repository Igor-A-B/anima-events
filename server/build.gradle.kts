plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSpring)
    alias(libs.plugins.springBoot)
    application
}

group = "com.anima"
version = "1.0.0"

application {
    mainClass = "com.anima.ApplicationKt"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    api(project(":core"))
    implementation(platform(libs.spring.boot.dependencies))

    implementation(libs.logback)
    implementation(libs.spring.boot.starterWeb)
    implementation(libs.spring.boot.starterSecurity)
    testImplementation(libs.spring.boot.starterTest)
    implementation(libs.spring.boot.starterDataJpa)
    implementation(libs.hibernate.core)
    implementation(libs.jakarta.persistence)
    implementation(libs.postgres.driver)
    implementation(libs.hikari.cp)
    implementation(libs.auth0)
    implementation(libs.bcrypt)
    implementation(libs.kotlin.reflect)
    // request bodies are kotlin data classes without a default constructor
    implementation(libs.jackson.kotlin)
}