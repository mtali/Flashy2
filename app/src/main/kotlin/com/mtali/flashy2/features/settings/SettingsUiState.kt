package com.mtali.flashy2.features.settings

import com.mtali.flashy2.core.datastore.ThemeConfig

data class SettingsUiState(
  val theme: ThemeConfig = ThemeConfig.SYSTEM,
  val wordsPerMinute: Int = 5,
  val useFarnsworth: Boolean = false,
  val farnsworthUnitMs: Int = 0,
  val noFlashWhenScreen: Boolean = true,
  val noFlashOnScreenOff: Boolean = false,
  val hasFlash: Boolean = true,
)
