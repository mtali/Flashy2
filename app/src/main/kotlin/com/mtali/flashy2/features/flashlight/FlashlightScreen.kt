package com.mtali.flashy2.features.flashlight

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mtali.flashy2.R
import com.mtali.flashy2.core.base.ObserveToasts
import com.mtali.flashy2.core.datastore.LightMode
import com.mtali.flashy2.core.ui.components.CircularSlider
import com.mtali.flashy2.core.ui.components.ColorSwatchRow
import com.mtali.flashy2.core.ui.components.HsvColorPicker
import com.mtali.flashy2.core.ui.components.PowerButton
import com.mtali.flashy2.core.ui.theme.FlashyAmber
import com.mtali.flashy2.core.ui.theme.FlashyTheme
import com.mtali.flashy2.core.ui.theme.InactiveGray

@Composable
fun FlashlightRoute(
  onNavigateToSettings: () -> Unit,
  onNavigateToAbout: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: FlashlightViewModel = hiltViewModel(),
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  ObserveToasts(viewModel)
  ApplyScreenBrightness(state)

  FlashlightScreen(
    state = state,
    onPowerClick = viewModel::onPowerClick,
    onToggleLightMode = viewModel::toggleLightMode,
    onSosClick = viewModel::toggleSos,
    onStrobeClick = viewModel::toggleStrobe,
    onStrengthChange = viewModel::onStrengthChange,
    onStrengthCommit = viewModel::onStrengthCommit,
    onBrightnessChange = viewModel::onBrightnessChange,
    onScreenColorChange = viewModel::onScreenColorChange,
    onStrobeIntervalChange = viewModel::onStrobeIntervalChange,
    onNavigateToSettings = onNavigateToSettings,
    onNavigateToAbout = onNavigateToAbout,
    modifier = modifier,
  )
}

@Composable
private fun ApplyScreenBrightness(state: FlashlightUiState) {
  val context = LocalContext.current
  val window = remember(context) { context.findActivity()?.window }
  androidx.compose.runtime.LaunchedEffect(state.lightMode, state.screenBrightness) {
    window?.let { w ->
      val params = w.attributes
      params.screenBrightness =
        if (state.lightMode == LightMode.SCREEN && state.screenBrightness > 0f) {
          state.screenBrightness
        } else {
          WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
        }
      w.attributes = params
    }
  }
}

@Composable
private fun FlashlightScreen(
  state: FlashlightUiState,
  onPowerClick: () -> Unit,
  onToggleLightMode: () -> Unit,
  onSosClick: () -> Unit,
  onStrobeClick: () -> Unit,
  onStrengthChange: (Float) -> Unit,
  onStrengthCommit: () -> Unit,
  onBrightnessChange: (Float) -> Unit,
  onScreenColorChange: (Color) -> Unit,
  onStrobeIntervalChange: (Float) -> Unit,
  onNavigateToSettings: () -> Unit,
  onNavigateToAbout: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val isScreenMode = state.lightMode == LightMode.SCREEN
  val background by animateColorAsState(
    if (isScreenMode) state.screenColor else androidx.compose.material3.MaterialTheme.colorScheme.background,
    label = "bg",
  )
  // Content colour: contrast against a coloured screen background, theme colour otherwise.
  val contentColor =
    if (isScreenMode) {
      if (background.luminance() > 0.5f) Color.Black else Color.White
    } else {
      androidx.compose.material3.MaterialTheme.colorScheme.onBackground
    }
  var showColorPicker by remember { mutableStateOf(false) }

  Box(
    modifier = modifier.fillMaxSize().background(background),
  ) {
    Column(
      modifier =
      Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.safeDrawing)
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      // Top bar: about + settings
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        IconButton(onClick = onNavigateToAbout) {
          Icon(painterResource(R.drawable.ic_info), stringResource(R.string.cd_about), tint = contentColor)
        }
        IconButton(onClick = onNavigateToSettings) {
          Icon(painterResource(R.drawable.ic_settings), stringResource(R.string.cd_settings), tint = contentColor)
        }
      }

      Spacer(Modifier.weight(1f))

      // The circular control with the power button at its centre.
      val sliderValue = if (isScreenMode) state.screenBrightness else state.strengthFraction
      val sliderEnabled = isScreenMode || state.supportsStrength
      CircularSlider(
        value = sliderValue,
        onValueChange = { if (isScreenMode) onBrightnessChange(it) else onStrengthChange(it) },
        onValueChangeFinished = { if (!isScreenMode) onStrengthCommit() },
        enabled = sliderEnabled,
        progressColor = if (isScreenMode) contentColor else FlashyAmber,
        thumbColor = if (isScreenMode) contentColor else FlashyAmber,
        trackColor = contentColor.copy(alpha = 0.15f),
        modifier = Modifier.size(300.dp),
      ) {
        val powerOn = if (isScreenMode) state.screenBrightness > 0f else state.isTorchOn
        PowerButton(
          isOn = powerOn,
          onClick = onPowerClick,
          iconTint = if (isScreenMode) contentColor else FlashyAmber,
          onColor = (if (isScreenMode) contentColor else FlashyAmber).copy(alpha = 0.18f),
        )
      }

      Spacer(Modifier.weight(1f))

      // SOS + stroboscope (torch mode only).
      AnimatedVisibility(visible = !isScreenMode && state.hasFlash) {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
          ToggleIcon(
            checked = state.isSosOn,
            onCheckedChange = { onSosClick() },
            icon = if (state.isSosOn) R.drawable.sos_on else R.drawable.sos,
            contentDescription = stringResource(R.string.cd_sos),
          )
          ToggleIcon(
            checked = state.isStrobeOn,
            onCheckedChange = { onStrobeClick() },
            icon = R.drawable.flare,
            contentDescription = stringResource(R.string.cd_stroboscope),
          )
        }
      }

      // Stroboscope interval slider.
      AnimatedVisibility(visible = state.isStrobeOn) {
        Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
          Text(stringResource(R.string.stroboscope_interval), color = contentColor)
          Slider(
            value = state.strobeIntervalSec,
            onValueChange = onStrobeIntervalChange,
            valueRange = 0.1f..1.0f,
          )
        }
      }

      // Screen-light colour controls.
      AnimatedVisibility(visible = isScreenMode) {
        ColorSwatchRow(
          selected = state.screenColor,
          onSelect = onScreenColorChange,
          onCustomClick = { showColorPicker = true },
          modifier = Modifier.padding(top = 12.dp),
        )
      }

      Spacer(Modifier.height(16.dp))

      // Light-source mode toggle.
      if (state.hasFlash) {
        SingleChoiceSegmentedButtonRow {
          SegmentedButton(
            selected = !isScreenMode,
            onClick = { if (isScreenMode) onToggleLightMode() },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
            icon = {},
          ) {
            Text(stringResource(R.string.flashlight))
          }
          SegmentedButton(
            selected = isScreenMode,
            onClick = { if (!isScreenMode) onToggleLightMode() },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
            icon = {},
          ) {
            Text(stringResource(R.string.screen_light))
          }
        }
      }
    }
  }

  if (showColorPicker) {
    AlertDialog(
      onDismissRequest = { showColorPicker = false },
      confirmButton = {
        TextButton(onClick = { showColorPicker = false }) { Text(stringResource(android.R.string.ok)) }
      },
      title = { Text(stringResource(R.string.cd_color_picker)) },
      text = {
        HsvColorPicker(
          initialColor = state.screenColor,
          onColorChange = onScreenColorChange,
          modifier = Modifier.fillMaxWidth(),
        )
      },
    )
  }
}

@Composable
private fun ToggleIcon(
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  icon: Int,
  contentDescription: String,
) {
  FilledIconToggleButton(checked = checked, onCheckedChange = onCheckedChange) {
    Icon(
      painter = painterResource(icon),
      contentDescription = contentDescription,
      tint = if (checked) FlashyAmber else InactiveGray,
    )
  }
}

private fun Context.findActivity(): Activity? {
  var context = this
  while (context is ContextWrapper) {
    if (context is Activity) return context
    context = context.baseContext
  }
  return null
}

@Preview(showBackground = true)
@Composable
private fun FlashlightScreenPreview() {
  FlashyTheme {
    FlashlightScreen(
      state = FlashlightUiState(supportsStrength = true),
      onPowerClick = {},
      onToggleLightMode = {},
      onSosClick = {},
      onStrobeClick = {},
      onStrengthChange = {},
      onStrengthCommit = {},
      onBrightnessChange = {},
      onScreenColorChange = {},
      onStrobeIntervalChange = {},
      onNavigateToSettings = {},
      onNavigateToAbout = {},
    )
  }
}
