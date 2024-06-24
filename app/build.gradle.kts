plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.android.gms.oss-licenses-plugin")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.textfield.json.ottawastreetcameras"

    defaultConfig {
        applicationId = "com.textfield.json.ottawastreetcameras"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        minSdk = 28
        compileSdk = 34
        targetSdk = 34
        versionCode = 30
        versionName = "2.2.1"
        vectorDrawables { useSupportLibrary = true }
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.9"
    }

    signingConfigs {
        create("release") {
            storeFile = file("U:\\Jason\\.android\\release.keystore")
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
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")

    implementation("com.android.support:multidex:1.0.3")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Google
    implementation("com.google.android.gms:play-services-oss-licenses:17.1.0")
    implementation("com.google.maps.android:maps-compose:4.4.1")

    // Compose
    val composeBom = platform("androidx.compose:compose-bom:2023.04.01")
    implementation(composeBom)
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Testing
    androidTestImplementation(composeBom)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.5")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.5.0")
    testImplementation("org.json:json:20230227")
    testImplementation("io.mockk:mockk:1.13.7")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
}
