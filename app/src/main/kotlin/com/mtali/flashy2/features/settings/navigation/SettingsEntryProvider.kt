package com.mtali.flashy2.features.settings.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.mtali.flashy2.core.navigation.Navigator
import com.mtali.flashy2.features.settings.SettingsRoute
import kotlinx.serialization.Serializable

@Serializable
data object SettingsNavKey : NavKey

fun EntryProviderScope<NavKey>.settingsEntry(navigator: Navigator) {
  entry<SettingsNavKey> {
    SettingsRoute(onBackClick = navigator::goBack)
  }
}
