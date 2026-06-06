package com.mtali.flashy2.features.flashlight

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.mtali.flashy2.core.base.BaseViewModel
import com.mtali.flashy2.core.data.repository.SettingsRepository
import com.mtali.flashy2.core.datastore.LightMode
import com.mtali.flashy2.core.morse.MorseTimer
import com.mtali.flashy2.core.ui.theme.ScreenWhite
import com.mtali.flashy2.domain.FlashEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class FlashlightViewModel
@Inject
constructor(
  private val engine: FlashEngine,
  private val settingsRepository: SettingsRepository,
) : BaseViewModel() {
  // Session-only UI state (not persisted as snapshots).
  private val lightModeOverride = MutableStateFlow<LightMode?>(null)
  private val screenBrightness = MutableStateFlow(0.5f)
  private val screenColor = MutableStateFlow(ScreenWhite)

  val uiState =
    combine(
      engine.state,
      settingsRepository.settings,
      lightModeOverride,
      screenBrightness,
      screenColor,
    ) { flash, settings, modeOverride, brightness, color ->
      val mode = if (!engine.hasFlashUnit) LightMode.SCREEN else modeOverride ?: settings.defaultMode
      FlashlightUiState(
        lightMode = mode,
        flashMode = flash.mode,
        hasFlash = engine.hasFlashUnit,
        supportsStrength = engine.supportsStrength,
        strengthFraction = strengthToFraction(flash.strength),
        screenBrightness = brightness,
        screenColor = color,
        strobeIntervalSec = settings.strobeIntervalSec,
      )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FlashlightUiState())

  init {
    // Apply persisted torch strength / strobe interval to the engine on startup.
    viewModelScope.launch {
      val settings = settingsRepository.settings.first()
      val strength =
        if (settings.flashStrength in 1..engine.maxStrength) settings.flashStrength else engine.maxStrength
      engine.setStrength(strength)
      engine.setStrobeInterval((settings.strobeIntervalSec * 1000).toLong())
    }
  }

  fun toggleLightMode() {
    val current = uiState.value.lightMode
    val next = if (current == LightMode.TORCH) LightMode.SCREEN else LightMode.TORCH
    lightModeOverride.value = next
    viewModelScope.launch { settingsRepository.setDefaultMode(next) }
    if (next == LightMode.SCREEN) {
      viewModelScope.launch {
        if (settingsRepository.settings.first().noFlashWhenScreen) engine.turnOff()
      }
    }
  }

  fun onPowerClick() {
    if (uiState.value.lightMode == LightMode.TORCH) {
      engine.toggleTorch()
    } else {
      screenBrightness.value = if (screenBrightness.value > 0f) 0f else 1f
    }
  }

  fun toggleSos() {
    viewModelScope.launch {
      val settings = settingsRepository.settings.first()
      val dit = MorseTimer.ditLengthMs(settings.wordsPerMinute.coerceAtLeast(1))
      val farnsworth =
        if (settings.useFarnsworth) {
          if (settings.farnsworthUnitMs > 0) settings.farnsworthUnitMs.toLong() else (dit * 1.25).toLong()
        } else {
          null
        }
      engine.toggleSos(MorseTimer.sosTimings(dit, farnsworth))
    }
  }

  fun toggleStrobe() {
    engine.toggleStrobe((uiState.value.strobeIntervalSec * 1000).toLong())
  }

  fun onStrengthChange(fraction: Float) {
    if (!engine.supportsStrength) return
    engine.setStrength(fractionToStrength(fraction))
  }

  fun onStrengthCommit() {
    viewModelScope.launch { settingsRepository.setFlashStrength(engine.state.value.strength) }
  }

  fun onBrightnessChange(fraction: Float) {
    screenBrightness.value = fraction.coerceIn(0f, 1f)
  }

  fun onScreenColorChange(color: Color) {
    screenColor.value = color
  }

  fun onStrobeIntervalChange(sec: Float) {
    engine.setStrobeInterval((sec * 1000).toLong())
    viewModelScope.launch { settingsRepository.setStrobeIntervalSec(sec) }
  }

  private fun strengthToFraction(strength: Int): Float {
    val max = engine.maxStrength
    return if (max > 1) (strength - 1).toFloat() / (max - 1) else 1f
  }

  private fun fractionToStrength(fraction: Float): Int {
    val max = engine.maxStrength
    return (1 + (fraction.coerceIn(0f, 1f) * (max - 1)).roundToInt()).coerceIn(1, max)
  }
}
