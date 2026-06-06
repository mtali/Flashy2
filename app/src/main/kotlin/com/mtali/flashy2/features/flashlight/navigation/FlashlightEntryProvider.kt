package com.mtali.flashy2.features.flashlight.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.mtali.flashy2.core.navigation.Navigator
import com.mtali.flashy2.features.about.navigation.AboutNavKey
import com.mtali.flashy2.features.flashlight.FlashlightRoute
import com.mtali.flashy2.features.settings.navigation.SettingsNavKey
import kotlinx.serialization.Serializable

@Serializable
data object FlashlightNavKey : NavKey

fun EntryProviderScope<NavKey>.flashlightEntry(navigator: Navigator) {
  entry<FlashlightNavKey> {
    FlashlightRoute(
      onNavigateToSettings = { navigator.navigate(SettingsNavKey) },
      onNavigateToAbout = { navigator.navigate(AboutNavKey) },
    )
  }
}
