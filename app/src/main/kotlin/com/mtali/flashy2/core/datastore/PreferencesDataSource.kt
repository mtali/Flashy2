package com.mtali.flashy2.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/** Reads and writes [UserSettings] to a Preferences DataStore. */
@Singleton
class PreferencesDataSource
@Inject
constructor(
  private val dataStore: DataStore<Preferences>,
) {
  val userSettings: Flow<UserSettings> =
    dataStore.data
      .catch { emit(emptyPreferences()) }
      .map { prefs ->
        UserSettings(
          theme =
          prefs[Keys.THEME]?.let { runCatching { ThemeConfig.valueOf(it) }.getOrNull() }
            ?: ThemeConfig.SYSTEM,
          defaultMode =
          prefs[Keys.DEFAULT_MODE]?.let { runCatching { LightMode.valueOf(it) }.getOrNull() }
            ?: LightMode.TORCH,
          wordsPerMinute = prefs[Keys.WORDS_PER_MIN] ?: 5,
          useFarnsworth = prefs[Keys.USE_FARNSWORTH] ?: false,
          farnsworthUnitMs = prefs[Keys.FARNSWORTH_UNIT] ?: 0,
          noFlashWhenScreen = prefs[Keys.NO_FLASH_WHEN_SCREEN] ?: true,
          noFlashOnScreenOff = prefs[Keys.NO_FLASH_ON_SCREEN_OFF] ?: false,
          strobeIntervalSec = prefs[Keys.STROBE_INTERVAL] ?: 0.5f,
          flashStrength = prefs[Keys.FLASH_STRENGTH] ?: -1,
        )
      }

  suspend fun setTheme(theme: ThemeConfig) = edit { it[Keys.THEME] = theme.name }

  suspend fun setDefaultMode(mode: LightMode) = edit { it[Keys.DEFAULT_MODE] = mode.name }

  suspend fun setWordsPerMinute(wpm: Int) = edit { it[Keys.WORDS_PER_MIN] = wpm }

  suspend fun setUseFarnsworth(enabled: Boolean) = edit { it[Keys.USE_FARNSWORTH] = enabled }

  suspend fun setFarnsworthUnitMs(ms: Int) = edit { it[Keys.FARNSWORTH_UNIT] = ms }

  suspend fun setNoFlashWhenScreen(enabled: Boolean) = edit { it[Keys.NO_FLASH_WHEN_SCREEN] = enabled }

  suspend fun setNoFlashOnScreenOff(enabled: Boolean) = edit { it[Keys.NO_FLASH_ON_SCREEN_OFF] = enabled }

  suspend fun setStrobeIntervalSec(sec: Float) = edit { it[Keys.STROBE_INTERVAL] = sec }

  suspend fun setFlashStrength(strength: Int) = edit { it[Keys.FLASH_STRENGTH] = strength }

  private suspend fun edit(block: (androidx.datastore.preferences.core.MutablePreferences) -> Unit) {
    dataStore.edit(block)
  }

  private object Keys {
    val THEME = stringPreferencesKey("theme")
    val DEFAULT_MODE = stringPreferencesKey("default_mode")
    val WORDS_PER_MIN = intPreferencesKey("words_per_min")
    val USE_FARNSWORTH = booleanPreferencesKey("use_farnsworth")
    val FARNSWORTH_UNIT = intPreferencesKey("farnsworth_unit")
    val NO_FLASH_WHEN_SCREEN = booleanPreferencesKey("no_flash_when_screen")
    val NO_FLASH_ON_SCREEN_OFF = booleanPreferencesKey("no_flash_on_device_screen_off")
    val STROBE_INTERVAL = floatPreferencesKey("stroboscope_interval")
    val FLASH_STRENGTH = intPreferencesKey("flashlight_strength")
  }
}
