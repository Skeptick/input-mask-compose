@file:Suppress("UnstableApiUsage")

rootProject.name = "input-mask"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

include(":input-mask-core", ":input-mask-compose")