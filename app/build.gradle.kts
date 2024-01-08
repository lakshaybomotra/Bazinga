import java.util.*
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
}

val properties = Properties()
properties.load(project.rootProject.file("local.properties").reader())

android {
    namespace = "com.lbdev.bazinga"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.lbdev.bazinga"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "opencvApiKey", properties.getProperty("opencvApiKey"))
        buildConfigField("String", "opencvCollectionId", properties.getProperty("opencvCollectionId"))
        buildConfigField("String", "movieApi", properties.getProperty("movieApi"))
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
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation ("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    implementation("io.coil-kt:coil:2.5.0")
    implementation("com.github.bumptech.glide:glide:4.14.2")
    implementation("com.webtoonscorp.android:readmore-view:1.3.2")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    var room_version = "2.6.1"

    implementation("androidx.room:room-ktx:$room_version")
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.1")

    implementation("com.google.ai.client.generativeai:generativeai:0.1.1")

}