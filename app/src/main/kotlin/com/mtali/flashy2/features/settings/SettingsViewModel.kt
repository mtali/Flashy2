package com.mtali.flashy2.features.settings

import androidx.lifecycle.viewModelScope
import com.mtali.flashy2.core.base.BaseViewModel
import com.mtali.flashy2.core.data.repository.SettingsRepository
import com.mtali.flashy2.core.datastore.ThemeConfig
import com.mtali.flashy2.domain.FlashEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
@Inject
constructor(
  private val settingsRepository: SettingsRepository,
  engine: FlashEngine,
) : BaseViewModel() {
  private val hasFlash = engine.hasFlashUnit

  val uiState =
    settingsRepository.settings
      .map { settings ->
        SettingsUiState(
          theme = settings.theme,
          wordsPerMinute = settings.wordsPerMinute,
          useFarnsworth = settings.useFarnsworth,
          farnsworthUnitMs = settings.farnsworthUnitMs,
          noFlashWhenScreen = settings.noFlashWhenScreen,
          noFlashOnScreenOff = settings.noFlashOnScreenOff,
          hasFlash = hasFlash,
        )
      }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState(hasFlash = hasFlash))

  fun setTheme(theme: ThemeConfig) = update { settingsRepository.setTheme(theme) }

  fun setWordsPerMinute(wpm: Int) = update { settingsRepository.setWordsPerMinute(wpm) }

  fun setUseFarnsworth(enabled: Boolean) = update { settingsRepository.setUseFarnsworth(enabled) }

  fun setFarnsworthUnitMs(ms: Int) = update { settingsRepository.setFarnsworthUnitMs(ms) }

  fun setNoFlashWhenScreen(enabled: Boolean) = update { settingsRepository.setNoFlashWhenScreen(enabled) }

  fun setNoFlashOnScreenOff(enabled: Boolean) = update { settingsRepository.setNoFlashOnScreenOff(enabled) }

  private fun update(block: suspend () -> Unit) {
    viewModelScope.launch { block() }
  }
}
