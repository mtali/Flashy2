// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.kotlin.compose) apply false
  alias(libs.plugins.spotless)
}

spotless {
  val ktlintVersion = libs.versions.ktlint.get()
  val editorConfig = mapOf("indent_size" to "2", "indent_style" to "space")
  kotlin {
    target("**/*.kt")
    targetExclude("**/build/**/*.kt")
    ktlint(ktlintVersion).editorConfigOverride(editorConfig)
    trimTrailingWhitespace()
    endWithNewline()
  }
  kotlinGradle {
    target("**/*.gradle.kts")
    targetExclude("**/build/**/*.gradle.kts")
    ktlint(ktlintVersion).editorConfigOverride(editorConfig)
  }
}
