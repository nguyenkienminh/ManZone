plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.man_zone"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.man_zone"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(fileTree(mapOf(
        "dir" to "libs",
        "include" to listOf("*.aar", "*.jar")
    )))
    //TO-DO: Change path before run
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("com.tbuonomo:dotsindicator:5.1.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.11.0")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")


    // Google Maps dependencies
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.libraries.places:places:4.0.0")
    implementation("commons-codec:commons-codec:1.14")

    //paypal
    implementation ("com.paypal.sdk:paypal-android-sdk:2.16.0")
    
    // SwipeRefreshLayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
}
