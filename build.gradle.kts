// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {}
}
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.devToolsKsp) apply false
    kotlin("plugin.serialization") version "1.8.10"
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.androidDynamicFeature) apply false
}