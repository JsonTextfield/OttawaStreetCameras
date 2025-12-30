plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.jsontextfield.streetcamstv"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.jsontextfield.streetcamstv"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

    }

    buildTypes {
        release {
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
    kotlin {
        jvmToolchain(11)
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(project(":core"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.tv.foundation)
    implementation(libs.androidx.tv.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    implementation(project(":core"))

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