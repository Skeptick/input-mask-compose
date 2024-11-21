plugins {
    id("com.android.library")
}

android {
    compileSdk = 35

    defaultConfig {
        minSdk = 21
    }

    buildFeatures {
        buildConfig = false
    }
}