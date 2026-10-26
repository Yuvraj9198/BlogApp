plugins {
    alias(libs.plugins.androidApplication) // Ensure libs version catalog is correctly set up
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")  // Ensure the google-services plugin is applied
}

android {
    namespace = "com.example.blogapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.blogapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Corrected viewBinding declaration
    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // AndroidX Libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Firebase Libraries
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)

    // Glide Image Loading Library
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // Firebase App Check dependency
    implementation ("com.google.firebase:firebase-appcheck:16.0.0")
    implementation ("com.google.firebase:firebase-appcheck-playintegrity:16.0.0") // For Play Integrity
    // Optionally add other AppCheck providers (e.g., SafetyNet)
    implementation ("com.google.firebase:firebase-appcheck-safetynet:16.0.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
