plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "id.neotica.imageclassificationdemo"
    compileSdk = 34

    defaultConfig {
        applicationId = "id.neotica.imageclassificationdemo"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
        mlModelBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    dynamicFeatures += setOf(":mediapipe")
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core"))

    //Navigation Component
    api("androidx.navigation:navigation-fragment-ktx:2.7.6")
    api("androidx.navigation:navigation-ui-ktx:2.7.6")

    //tfLite
    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.metadata)
    implementation("org.tensorflow:tensorflow-lite-task-vision:0.4.4") //for vision
    implementation("org.tensorflow:tensorflow-lite-gpu:2.9.0")

    //tfLite gms
    implementation("com.google.android.gms:play-services-tflite-support:16.1.0") //add support for gms tfLite
    implementation("com.google.android.gms:play-services-tflite-gpu:16.2.0") //for gpu
    implementation("org.tensorflow:tensorflow-lite-task-vision-play-services:0.4.4") //for vision
    implementation("com.google.android.gms:play-services-tflite-java:16.1.0") //For Rice prediction



    val ktorVersion = "2.3.10"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")

    //Koin
    val koinVersion = "3.3.2"
    implementation ("io.insert-koin:koin-core:$koinVersion")
    implementation("io.insert-koin:koin-android:$koinVersion")

    //MLKit
    implementation("com.google.mlkit:text-recognition:16.0.0") //Text recognition
    implementation("com.google.mlkit:translate:17.0.1") //Translate

    //Barcode Scanning
    implementation("com.google.android.gms:play-services-mlkit-barcode-scanning:18.3.0")
    implementation("androidx.camera:camera-mlkit-vision:1.4.0-alpha02")
}