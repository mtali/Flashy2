package com.mtali.flashy2.features.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mtali.flashy2.R
import com.mtali.flashy2.core.datastore.ThemeConfig
import com.mtali.flashy2.core.ui.theme.FlashyTheme

private const val MORSE_TIMING_URL = "https://morsecode.world/international/timing.html"

@Composable
fun SettingsRoute(
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: SettingsViewModel = hiltViewModel(),
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  val uriHandler = LocalUriHandler.current
  SettingsScreen(
    state = state,
    onBackClick = onBackClick,
    onThemeChange = viewModel::setTheme,
    onWpmChange = viewModel::setWordsPerMinute,
    onUseFarnsworthChange = viewModel::setUseFarnsworth,
    onFarnsworthUnitChange = viewModel::setFarnsworthUnitMs,
    onNoFlashWhenScreenChange = viewModel::setNoFlashWhenScreen,
    onNoFlashOnScreenOffChange = viewModel::setNoFlashOnScreenOff,
    onLearnMoreMorse = { uriHandler.openUri(MORSE_TIMING_URL) },
    modifier = modifier,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
  state: SettingsUiState,
  onBackClick: () -> Unit,
  onThemeChange: (ThemeConfig) -> Unit,
  onWpmChange: (Int) -> Unit,
  onUseFarnsworthChange: (Boolean) -> Unit,
  onFarnsworthUnitChange: (Int) -> Unit,
  onNoFlashWhenScreenChange: (Boolean) -> Unit,
  onNoFlashOnScreenOffChange: (Boolean) -> Unit,
  onLearnMoreMorse: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = { Text(stringResource(R.string.settings)) },
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(painterResource(R.drawable.baseline_arrow_back_24), stringResource(R.string.cd_back))
          }
        },
      )
    },
  ) { padding ->
    Column(
      modifier =
      Modifier
        .padding(padding)
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      SectionHeader(stringResource(R.string.appearance))
      Text(stringResource(R.string.theme), style = MaterialTheme.typography.bodyLarge)
      SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        ThemeOption(state.theme, ThemeConfig.LIGHT, R.string.light, 0, 3, onThemeChange)
        ThemeOption(state.theme, ThemeConfig.DARK, R.string.dark, 1, 3, onThemeChange)
        ThemeOption(state.theme, ThemeConfig.SYSTEM, R.string.follow_system, 2, 3, onThemeChange)
      }

      SectionHeader(stringResource(R.string.general))
      if (state.hasFlash) {
        SwitchRow(
          title = stringResource(R.string.no_flash_when_screen),
          checked = state.noFlashWhenScreen,
          onCheckedChange = onNoFlashWhenScreenChange,
        )
        SwitchRow(
          title = stringResource(R.string.no_flash_on_device_screen_off),
          subtitle = stringResource(R.string.no_flash_on_device_screen_off_subtext),
          checked = state.noFlashOnScreenOff,
          onCheckedChange = onNoFlashOnScreenOffChange,
        )
      }

      if (state.hasFlash) {
        SectionHeader(stringResource(R.string.sos))
        Text(
          stringResource(R.string.words_per_min) + ": ${state.wordsPerMinute}",
          style = MaterialTheme.typography.bodyLarge,
        )
        Slider(
          value = state.wordsPerMinute.toFloat(),
          onValueChange = { onWpmChange(it.toInt()) },
          valueRange = 1f..30f,
        )
        SwitchRow(
          title = stringResource(R.string.use_farnsworth),
          checked = state.useFarnsworth,
          onCheckedChange = onUseFarnsworthChange,
        )
        if (state.useFarnsworth) {
          val unitLabel = if (state.farnsworthUnitMs <= 0) "auto" else "${state.farnsworthUnitMs} ms"
          Text(
            stringResource(R.string.farnsworth_unit_length) + ": $unitLabel",
            style = MaterialTheme.typography.bodyLarge,
          )
          Slider(
            value = state.farnsworthUnitMs.toFloat(),
            onValueChange = { onFarnsworthUnitChange(it.toInt()) },
            valueRange = 0f..1000f,
          )
        }
        TextButton(onClick = onLearnMoreMorse, modifier = Modifier.padding(top = 4.dp)) {
          Text(stringResource(R.string.learn_more_morse_timing))
        }
      }
    }
  }
}

@Composable
private fun SectionHeader(title: String) {
  Text(
    text = title,
    style = MaterialTheme.typography.titleLarge,
    color = MaterialTheme.colorScheme.primary,
    fontWeight = FontWeight.SemiBold,
    modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
  )
}

@Composable
private fun SwitchRow(
  title: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
  subtitle: String? = null,
) {
  Row(
    modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(title, style = MaterialTheme.typography.bodyLarge)
      if (subtitle != null) {
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }
    Switch(checked = checked, onCheckedChange = onCheckedChange)
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun androidx.compose.material3.SingleChoiceSegmentedButtonRowScope.ThemeOption(
  current: ThemeConfig,
  option: ThemeConfig,
  labelRes: Int,
  index: Int,
  count: Int,
  onThemeChange: (ThemeConfig) -> Unit,
) {
  SegmentedButton(
    selected = current == option,
    onClick = { onThemeChange(option) },
    shape = SegmentedButtonDefaults.itemShape(index = index, count = count),
    icon = {},
  ) {
    Text(stringResource(labelRes))
  }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
  FlashyTheme {
    SettingsScreen(
      state = SettingsUiState(useFarnsworth = true),
      onBackClick = {},
      onThemeChange = {},
      onWpmChange = {},
      onUseFarnsworthChange = {},
      onFarnsworthUnitChange = {},
      onNoFlashWhenScreenChange = {},
      onNoFlashOnScreenOffChange = {},
      onLearnMoreMorse = {},
    )
  }
}
