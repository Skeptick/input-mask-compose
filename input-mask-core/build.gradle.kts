@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)

plugins {
    id("android-setup-plugin")
    kotlin("multiplatform")
    id("com.vanniktech.maven.publish")
}

kotlin {
    metadata {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.add("-Xexplicit-api=strict")
                }
            }
        }
    }

    // Android
    androidTarget {
        publishAllLibraryVariants()
    }

    // JVM
    jvm()

    // iOS
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    // watchOS
    watchosX64()
    watchosArm32()
    watchosArm64()
    watchosSimulatorArm64()

    // tvOS
    tvosX64()
    tvosArm64()
    tvosSimulatorArm64()

    // Linux
    linuxX64()
    linuxArm64()

    // Windows
    mingwX64()

    // MacOS
    macosX64()
    macosArm64()

    // JavaScript
    js {
        browser()
        nodejs()
    }

    // WASM JS
    wasmJs {
        browser()
        nodejs()
        d8()
    }

    // WASM WASI
    wasmWasi {
        nodejs()
    }

    jvmToolchain(17)
}

android {
    namespace = "io.github.skeptick.inputmask.core"
}