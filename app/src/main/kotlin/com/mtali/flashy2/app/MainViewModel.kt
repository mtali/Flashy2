package com.mtali.flashy2.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtali.flashy2.core.data.repository.SettingsRepository
import com.mtali.flashy2.core.datastore.ThemeConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject
constructor(
  settingsRepository: SettingsRepository,
) : ViewModel() {
  val theme =
    settingsRepository.settings
      .map { it.theme }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ThemeConfig.SYSTEM)
}
