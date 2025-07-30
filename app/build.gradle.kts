plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("com.google.android.gms.oss-licenses-plugin")
    kotlin("plugin.serialization") version libs.versions.kotlin
}

android {
    namespace = "com.textfield.json.ottawastreetcameras"

    defaultConfig {
        applicationId = "com.textfield.json.ottawastreetcameras"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        minSdk = 28
        compileSdk = 35
        targetSdk = 35
        versionCode = 41
        versionName = "2.4.4"
        vectorDrawables { useSupportLibrary = true }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
    buildFeatures {
        buildConfig = true
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
}

dependencies {

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(libs.multidex)

    // Koin
    implementation(libs.koin.compose.viewmodel.navigation)
    implementation(libs.koin.android)

    // Google
    implementation(libs.play.services.oss.licenses)
    implementation(libs.maps.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material.icons.extended)

    implementation(libs.coil.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material3)

    // Supabase
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest.kt)
    implementation(libs.ktor.client.android)

    implementation(libs.androidx.datastore.preferences)

    // Testing
    androidTestImplementation(platform(libs.androidx.compose.bom))

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
