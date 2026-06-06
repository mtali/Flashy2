import com.mtali.flashy2.Configuration

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.ksp)
  alias(libs.plugins.hilt)
}

android {
  namespace = "com.mtali.flashy2"
  compileSdk = Configuration.COMPILE_SDK

  defaultConfig {
    applicationId = "com.mtali.flashy2"
    minSdk = Configuration.MIN_SDK
    targetSdk = Configuration.TARGET_SDK
    versionCode = Configuration.VERSION_CODE
    versionName = Configuration.VERSION_NAME

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro",
      )
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
}

dependencies {
  // Core
  implementation(libs.androidx.core.ktx)

  // Lifecycle
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.viewmodel.compose)

  // Compose
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  debugImplementation(libs.androidx.compose.ui.tooling)
  debugImplementation(libs.androidx.compose.ui.test.manifest)

  // Navigation 3
  implementation(libs.navigation3.runtime)
  implementation(libs.navigation3.ui)
  implementation(libs.lifecycle.viewmodel.navigation3)

  // Hilt (DI)
  implementation(libs.hilt.android)
  implementation(libs.hilt.navigation.compose)
  ksp(libs.hilt.android.compiler)

  // DataStore
  implementation(libs.datastore.preferences)

  // Serialization (Navigation 3 keys)
  implementation(libs.kotlinx.serialization.json)

  // Unit testing
  testImplementation(libs.junit)
  testImplementation(libs.coroutines.test)
  testImplementation(libs.truth)
  testImplementation(libs.turbine)
  testImplementation(libs.robolectric)

  // Instrumentation testing
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
}
