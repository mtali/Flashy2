import com.github.triplet.gradle.androidpublisher.ReleaseStatus
import com.mtali.flashy2.Configuration

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.ksp)
  alias(libs.plugins.hilt)
  alias(libs.plugins.play.publisher)
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

  signingConfigs {
    create("release") {
      // Supplied by CI through env vars; absent locally so debug builds keep working without a
      // keystore. The keystore itself never lives in the repo.
      System.getenv("KEYSTORE_PATH")?.let { path ->
        storeFile = file(path)
        storePassword = System.getenv("KEYSTORE_PASSWORD")
        keyAlias = System.getenv("KEY_ALIAS")
        keyPassword = System.getenv("KEY_PASSWORD")
      }
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro",
      )
      // Only sign with the upload key when CI provided it; otherwise fall back to the default
      // (debug) signing so a plain `assembleRelease` still succeeds locally.
      if (System.getenv("KEYSTORE_PATH") != null) {
        signingConfig = signingConfigs.getByName("release")
      }
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

// Gradle Play Publisher. The release workflow runs `publishReleaseBundle`, which builds the signed
// AAB and uploads it. Service-account credentials are read from the ANDROID_PUBLISHER_CREDENTIALS
// env var in CI (raw JSON), so nothing secret is committed. Promote internal -> production in the
// Play Console, or change `track` here.
play {
  track.set("internal")
  defaultToAppBundles.set(true)
  releaseStatus.set(ReleaseStatus.COMPLETED)
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
