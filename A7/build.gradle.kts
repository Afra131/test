plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "edu.northeastern.a6_group8"
    compileSdk = 34

    defaultConfig {
        applicationId = "edu.northeastern.a6_group8"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat.v131)
    implementation(libs.material.v140)
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation(libs.androidx.constraintlayout.v210)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v113)
    androidTestImplementation(libs.androidx.espresso.core.v340)

    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.database)
}
