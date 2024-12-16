import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.exclude

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
}

android {
    namespace = "com.izamaralv.swipethebeat"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.izamaralv.swipethebeat"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
        implementation ("com.squareup.retrofit2:retrofit:2.9.0")
        implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
        implementation ("com.squareup.okhttp3:okhttp:4.9.1")


    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("com.adamratzman:spotify-api-kotlin-core:3.8.6")
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation("androidx.compose.runtime:runtime-livedata:1.7.5")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-analytics")
    implementation("androidx.fragment:fragment-ktx:1.8.5")

    implementation("androidx.core:core-ktx:1.12.0") {
        exclude(group = "xmlpull")
        exclude(group = "xpp3")
    }

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0") {
        exclude(group = "xmlpull")
        exclude(group = "xpp3")
    }

    implementation("androidx.activity:activity-compose:1.8.2") {
        exclude(group = "xmlpull")
        exclude(group = "xpp3")
    }

    implementation(platform("androidx.compose:compose-bom:2023.08.00")) {
    }

    implementation("androidx.compose.ui:ui") {
        exclude(group = "xmlpull")
        exclude(group = "xpp3")
    }


    implementation("androidx.compose.ui:ui-graphics") {
        exclude(group = "xmlpull")
        exclude(group = "xpp3")
    }

    implementation("androidx.compose.ui:ui-tooling-preview") {
        exclude(group = "xmlpull")
        exclude(group = "xpp3")
    }

    implementation("androidx.compose.material3:material3") {
        exclude(group = "xmlpull")
        exclude(group = "xpp3")
    }

    implementation("androidx.navigation:navigation-safe-args-generator:2.7.7") {
        exclude(group = "xmlpull")
        exclude(group = "xpp3")
    }
    implementation("androidx.navigation:navigation-runtime-ktx:2.8.4")
    implementation("androidx.navigation:navigation-compose:2.8.4")

    // Retrofit and related dependencies
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation(fileTree(mapOf(
        "dir" to "libs",
        "include" to listOf("*.aar", "*.jar"),
        "exclude" to listOf("")
    )))

    // Additional dependencies for testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00")) {
    }
    androidTestImplementation("androidx.compose.ui:ui-test-junit4") {
        exclude(group = "xmlpull")
        exclude(group = "xpp3")
    }
    debugImplementation("androidx.compose.ui:ui-tooling") {
        exclude(group = "xmlpull")
        exclude(group = "xpp3")
    }
    debugImplementation("androidx.compose.ui:ui-test-manifest") {
        exclude(group = "xmlpull")
        exclude(group = "xpp3")
    }

    implementation("com.alexstyl.swipeablecard:swipeablecard:0.1.0")
    implementation("androidx.compose.material:material-icons-extended:1.7.5")
    implementation("com.google.code.gson:gson:2.10.1")
//    implementation("com.spotify.android:spotify-app-remote:1.2.4")

}
