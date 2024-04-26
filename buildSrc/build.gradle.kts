plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

dependencies {
    implementation(libs.plugin.kotlin)
    implementation(libs.plugin.android)
}