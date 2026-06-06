import com.mtali.flashy2.Configuration

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.ksp)
  alias(libs.plugins.hilt)
}

// Tag-driven versioning: a `vX.Y.Z` git tag is the source of truth. CI may pass VERSION_NAME
// explicitly; otherwise we read the latest matching tag reachable from HEAD. Local builds with no
// release tag fall back to Configuration.FALLBACK_VERSION_NAME. The versionCode is always derived
// from the version name, never hand-edited.
fun gitTagVersionName(): String? = try {
  providers.exec {
    commandLine("git", "describe", "--tags", "--abbrev=0", "--match", "v*")
  }.standardOutput.asText.get().trim().removePrefix("v").ifBlank { null }
} catch (_: Exception) {
  null
}

val resolvedVersionName: String =
  System.getenv("VERSION_NAME")?.trim()?.removePrefix("v")?.ifBlank { null }
    ?: gitTagVersionName()
    ?: Configuration.FALLBACK_VERSION_NAME

val resolvedVersionCode: Int =
  Configuration.versionCodeFor(resolvedVersionName)
    ?: run {
      logger.warn("Flashy: '$resolvedVersionName' is not SemVer; using fallback versionCode")
      Configuration.versionCodeFor(Configuration.FALLBACK_VERSION_NAME)!!
    }

android {
  namespace = "com.mtali.flashy2"
  compileSdk = Configuration.COMPILE_SDK

  defaultConfig {
    applicationId = "com.mtali.flashy2"
    minSdk = Configuration.MIN_SDK
    targetSdk = Configuration.TARGET_SDK
    versionCode = resolvedVersionCode
    versionName = resolvedVersionName

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
