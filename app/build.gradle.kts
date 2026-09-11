import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
   // alias(libs.plugins.kotlin.android)
  //alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)

    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.plugin.serialization)
    alias(libs.plugins.ksp.plugin)
    alias(libs.plugins.hilt.plugin)

}

android {
    namespace = "com.kodex.bookmarketcompose"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.kodex.bookmarketcompose"
        minSdk = 24
        targetSdk = 34
        versionCode = 3
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
   /* kotlinOptions {
        jvmTarget = "11"
    }*/

    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11) // или JVM_17, в зависимости от вашего проекта
    }
}
dependencies {
// ✅ ТЕСТЫ
  /*  testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)*/
    // ✅ ТЕСТЫ — прямыми строками, независимо от toml
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.11.0")
    testImplementation("io.mockk:mockk:1.14.11")

    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.remote.creation.core)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.constraintlayout)

     implementation(libs.koin.androidx.compose)

    implementation(libs.androidx.paging)

    implementation(libs.hilt.navigation.compose)
    implementation(libs.androidx.espresso.core)
   // implementation(libs.androidx.room.common.jvm)
    //implementation(libs.androidx.room3.runtime)

    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.compose.foundation)
    ksp(libs.hilt.navigation.compose.compiler)


    implementation(libs.android.hilt)
    ksp(libs.android.hilt.compiler)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    implementation(libs.androidx.material.icons.extended)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.storage)

  //  testImplementation(libs.junit)
    implementation(libs.coil.compose)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    dependencies {
        // OpenStreetMap
        implementation("org.osmdroid:osmdroid-android:6.1.20")

        // Для HTTP-запросов к OSRM (маршруты)
        implementation("com.squareup.okhttp3:okhttp:5.5.0")

        // Для работы с JSON от OSRM
        implementation("com.google.code.gson:gson:2.14.0")
    }
}