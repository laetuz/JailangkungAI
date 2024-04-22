// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.6")
        classpath ("com.google.dagger:hilt-android-gradle-plugin:2.48.1")
    }
}
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.devToolsKsp) apply false
    kotlin("plugin.serialization") version "1.8.10"
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.androidDynamicFeature) apply false
}