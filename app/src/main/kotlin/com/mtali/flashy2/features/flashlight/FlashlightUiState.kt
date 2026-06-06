package com.mtali.flashy2.features.flashlight

import androidx.compose.ui.graphics.Color
import com.mtali.flashy2.core.datastore.LightMode
import com.mtali.flashy2.core.ui.theme.ScreenWhite
import com.mtali.flashy2.domain.FlashMode

data class FlashlightUiState(
  val lightMode: LightMode = LightMode.TORCH,
  val flashMode: FlashMode = FlashMode.OFF,
  val hasFlash: Boolean = true,
  val supportsStrength: Boolean = false,
  /** Torch strength as a 0f..1f fraction of the device range. */
  val strengthFraction: Float = 1f,
  /** Screen-light brightness as a 0f..1f fraction (0 = off). */
  val screenBrightness: Float = 0.5f,
  val screenColor: Color = ScreenWhite,
  val strobeIntervalSec: Float = 0.5f,
) {
  val isTorchOn: Boolean get() = flashMode == FlashMode.TORCH
  val isSosOn: Boolean get() = flashMode == FlashMode.SOS
  val isStrobeOn: Boolean get() = flashMode == FlashMode.STROBOSCOPE
}
