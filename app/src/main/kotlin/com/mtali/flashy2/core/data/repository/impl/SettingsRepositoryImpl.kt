package com.mtali.flashy2.core.data.repository.impl

import com.mtali.flashy2.core.data.repository.SettingsRepository
import com.mtali.flashy2.core.datastore.LightMode
import com.mtali.flashy2.core.datastore.PreferencesDataSource
import com.mtali.flashy2.core.datastore.ThemeConfig
import com.mtali.flashy2.core.datastore.UserSettings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl
@Inject
constructor(
  private val preferences: PreferencesDataSource,
) : SettingsRepository {
  override val settings: Flow<UserSettings> = preferences.userSettings

  override suspend fun setTheme(theme: ThemeConfig) = preferences.setTheme(theme)

  override suspend fun setDefaultMode(mode: LightMode) = preferences.setDefaultMode(mode)

  override suspend fun setWordsPerMinute(wpm: Int) = preferences.setWordsPerMinute(wpm)

  override suspend fun setUseFarnsworth(enabled: Boolean) = preferences.setUseFarnsworth(enabled)

  override suspend fun setFarnsworthUnitMs(ms: Int) = preferences.setFarnsworthUnitMs(ms)

  override suspend fun setNoFlashWhenScreen(enabled: Boolean) = preferences.setNoFlashWhenScreen(enabled)

  override suspend fun setNoFlashOnScreenOff(enabled: Boolean) = preferences.setNoFlashOnScreenOff(enabled)

  override suspend fun setStrobeIntervalSec(sec: Float) = preferences.setStrobeIntervalSec(sec)

  override suspend fun setFlashStrength(strength: Int) = preferences.setFlashStrength(strength)
}
