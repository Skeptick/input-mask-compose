buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    dependencies {
        classpath(libs.plugin.android)
        classpath(libs.plugin.kotlin)
        classpath(libs.plugin.publish.maven)
        classpath(libs.plugin.compose.jb)
        classpath(libs.plugin.compose.compiler)
    }
}