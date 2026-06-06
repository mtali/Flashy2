package com.mtali.flashy2.core.data.repository

import com.mtali.flashy2.core.datastore.LightMode
import com.mtali.flashy2.core.datastore.ThemeConfig
import com.mtali.flashy2.core.datastore.UserSettings
import kotlinx.coroutines.flow.Flow

/** App-wide access to persisted user settings. */
interface SettingsRepository {
  val settings: Flow<UserSettings>

  suspend fun setTheme(theme: ThemeConfig)

  suspend fun setDefaultMode(mode: LightMode)

  suspend fun setWordsPerMinute(wpm: Int)

  suspend fun setUseFarnsworth(enabled: Boolean)

  suspend fun setFarnsworthUnitMs(ms: Int)

  suspend fun setNoFlashWhenScreen(enabled: Boolean)

  suspend fun setNoFlashOnScreenOff(enabled: Boolean)

  suspend fun setStrobeIntervalSec(sec: Float)

  suspend fun setFlashStrength(strength: Int)
}
