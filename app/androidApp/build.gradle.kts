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
// a real phone reaches the dev server through usb: its localhost:8080 is forwarded to this machine
// runs on every debug build because adb drops the forward when it restarts; skipped when api.baseUrl is set
val adbPath = (localProperties.getProperty("sdk.dir") ?: System.getenv("ANDROID_HOME") ?: "") + "/platform-tools/adb"
val useAdbReverse = localProperties.getProperty("api.baseUrl") == null && File(adbPath).exists()
val adbReverse by tasks.registering(Exec::class) {
    enabled = useAdbReverse
    commandLine(adbPath, "reverse", "tcp:8080", "tcp:8080")
    // no device connected is fine, the emulator doesn't need it
    isIgnoreExitValue = true
}
tasks.matching { it.name == "preDebugBuild" }.configureEach { dependsOn(adbReverse) }
