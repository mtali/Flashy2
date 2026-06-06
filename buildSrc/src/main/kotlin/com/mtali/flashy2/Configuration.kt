package com.mtali.flashy2

object Configuration {
  const val COMPILE_SDK = 36
  const val TARGET_SDK = 36
  const val MIN_SDK = 24

  // The release version is tag-driven: a `vX.Y.Z` git tag is the source of truth (resolved in
  // app/build.gradle.kts). This is only the fallback for local builds with no release tag — it is
  // never the number that ships.
  const val FALLBACK_VERSION_NAME = "1.0.0"

  /**
   * Maps a SemVer string to a strictly increasing Android `versionCode`:
   * `MAJOR * 10000 + MINOR * 100 + PATCH` — e.g. `"1.4.2"` -> `10402`. A leading `v` and any
   * pre-release/build metadata (`-rc1`, `+sha`) are ignored. `MINOR` and `PATCH` must each be in
   * `0..99`. Returns `null` if [versionName] isn't a valid `MAJOR.MINOR.PATCH` core.
   */
  fun versionCodeFor(versionName: String): Int? {
    val core = versionName.trim().removePrefix("v").substringBefore('-').substringBefore('+')
    val parts = core.split(".")
    if (parts.size != 3) return null
    val (major, minor, patch) = parts.map { it.toIntOrNull() ?: return null }
    if (major < 0 || minor !in 0..99 || patch !in 0..99) return null
    return major * 10000 + minor * 100 + patch
  }
}
