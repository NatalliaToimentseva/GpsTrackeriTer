plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("androidx.navigation.safeargs")
    id("com.google.devtools.ksp") version "1.9.10-1.0.13"
    id("com.google.gms.google-services")
}

android {
    namespace = "com.iTergt.routgpstracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.iTergt.routgpstracker"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.preference)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.preference.ktx)

    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    implementation(libs.koin.core)
    implementation(libs.koin.android)

    implementation(libs.play.services.location)

    implementation(libs.rxandroid)
    implementation(libs.rxjava)

    implementation("androidx.paging:paging-runtime:3.3.4")
    implementation("androidx.paging:paging-rxjava3:3.3.4")

    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-rxjava3:2.6.1")
    implementation("androidx.room:room-paging:2.6.1")

    implementation("androidx.viewpager2:viewpager2:1.1.0")

    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-auth")

    implementation("org.osmdroid:osmdroid-android:6.1.10")
    implementation("com.github.MKergall:osmbonuspack:6.7.0")



}