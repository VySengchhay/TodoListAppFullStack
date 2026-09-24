plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.androidapp.todolistapplication"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.androidapp.todolistapplication"
        minSdk = 29
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        // Swaps in HiltTestApplication so @HiltAndroidTest tests can replace modules.
        testInstrumentationRunner = "com.androidapp.todolistapplication.HiltTestRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
        resValues = true
    }

    // Group our flavors
    flavorDimensions += "environment"

    productFlavors {
        create("dev") {
            applicationIdSuffix = ".dev"
            dimension = "environment"
            buildConfigField(
                "String",
                "BASE_URL",
                "\"https://com.androidapp.todolistapplication.dev.uat/\""
            )
            resValue("string", "app_name", "[Dev] TodoList")
        }

        create("uat") {
            applicationIdSuffix = ".uat"
            dimension = "environment"
            buildConfigField(
                "String",
                "BASE_URL",
                "\"https://com.androidapp.todolistapplication.uat.com/\""
            )
            resValue("string", "app_name", "[UAT] TodoList")
        }

        create("pro") {
            applicationIdSuffix = ""
            dimension = "environment"
            buildConfigField(
                "String",
                "BASE_URL",
                "\"http://10.0.2.2:8080/\""
            )
            resValue("string", "app_name", "TodoList")
        }

    }

//    dynamicFeatures += setOf(":feature")
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    //DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    implementation(libs.androidx.room.ktx)

    //retrofit
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlinx.serialization)
    //okhttp
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging)
    //kotlin serialization
    implementation(libs.kotlinx.serialization.json)
    //gson
    implementation(libs.retrofit.converter.gson)
    implementation(libs.gson)

    //Room
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)

    //navigation3
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    //icons
    implementation(libs.androidx.compose.material.icons.extended)

    //datastore
    implementation(libs.androidx.datastore.preferences)
}