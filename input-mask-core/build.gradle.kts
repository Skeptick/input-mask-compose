@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

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

    androidTarget {
        publishAllLibraryVariants()
    }
    jvm()
    js {
        browser()
        nodejs()
    }
    iosArm64()
    iosX64()
    iosSimulatorArm64()
    watchosArm32()
    watchosArm64()
    watchosX64()
    watchosSimulatorArm64()
    tvosArm64()
    tvosX64()
    tvosSimulatorArm64()
    linuxX64()
    mingwX64()
    macosX64()
    macosArm64()
    linuxArm64()
    wasmJs()
    wasmWasi()

    jvmToolchain(17)
}

android {
    namespace = "io.github.skeptick.inputmask.core"
}