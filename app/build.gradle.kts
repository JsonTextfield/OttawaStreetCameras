plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    kotlin("plugin.serialization") version libs.versions.kotlin
}

android {
    namespace = "com.textfield.json.ottawastreetcameras"

    defaultConfig {
        applicationId = "com.textfield.json.ottawastreetcameras"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        minSdk = 28
        compileSdk = 36
        targetSdk = 36
        versionCode = 41
        versionName = "2.5.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }

    signingConfigs {
        create("release") {
            storeFile = file(properties["releaseStoreFile"].toString())
            storePassword = properties["releaseStorePassword"].toString()
            keyAlias = properties["releaseKeyAlias"].toString()
            keyPassword = properties["releaseKeyPassword"].toString()
        }
    }

    buildTypes {
        debug {
            multiDexEnabled = false
        }
        release {
            multiDexEnabled = true
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Koin
    implementation(libs.koin.compose.viewmodel.navigation)
    implementation(libs.koin.android)

    // Google
    implementation(libs.maps.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui.tooling.preview)

    // Jetpack Glance
    // For AppWidgets support
    implementation("androidx.glance:glance-appwidget:1.1.1")
    // For interop APIs with Material 3
    implementation("androidx.glance:glance-material3:1.1.1")

    implementation(libs.coil.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material3)

    // Supabase
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest.kt)
    implementation(libs.ktor.client.android)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)

    // Testing
    androidTestImplementation(platform(libs.androidx.compose.bom))

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
