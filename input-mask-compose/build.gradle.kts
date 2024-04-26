@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    id("android-setup-plugin")
    kotlin("multiplatform")
    id("com.vanniktech.maven.publish")
    id("org.jetbrains.compose")
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
    }
    macosX64()
    macosArm64()
    iosArm64()
    iosX64()
    iosSimulatorArm64()
    wasmJs()

    sourceSets {
        commonMain {
            dependencies {
                api(projects.inputMaskCore)
                implementation(compose.material)
                implementation(compose.material3)
            }
        }
    }

    jvmToolchain(17)
}

android {
    namespace = "io.github.skeptick.inputmask.compose"
}