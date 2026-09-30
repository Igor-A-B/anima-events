import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
}

// debug builds read the server url from local.properties (api.baseUrl), release builds from API_BASE_URL
val localProperties = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use(::load)
}
val debugApiUrl = localProperties.getProperty("api.baseUrl") ?: "http://localhost:8080"
val releaseApiUrl = providers.gradleProperty("API_BASE_URL").orElse(providers.environmentVariable("API_BASE_URL"))

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}
dependencies {
    implementation(project(":app:shared"))

    implementation(libs.androidx.activity.compose)

    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)
}

android {
    namespace = "com.example.anima"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.anima"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        debug {
            buildConfigField("String", "API_BASE_URL", "\"$debugApiUrl\"")
        }
        release {
            // a release must never ship with a dev url
            val url = releaseApiUrl.orNull ?: if (gradle.startParameter.taskNames.any { it.contains("release", true) }) {
                throw GradleException("Set API_BASE_URL (gradle property or env var) to build a release")
            } else ""
            buildConfigField("String", "API_BASE_URL", "\"$url\"")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}