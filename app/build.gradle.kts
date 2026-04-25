plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.moviemate"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.moviemate"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
        implementation(libs.appcompat)
        implementation(libs.material)
        testImplementation(libs.junit)
        androidTestImplementation(libs.ext.junit)
        androidTestImplementation(libs.espresso.core)
        implementation(libs.volley)
        // Keep these two lines for Glide, assuming libs.glide and libs.compiler are defined in libs.versions.toml
        implementation(libs.glide)
        annotationProcessor(libs.compiler)
        implementation("androidx.recyclerview:recyclerview:1.3.0")
        implementation("androidx.cardview:cardview:1.0.0")

    }
