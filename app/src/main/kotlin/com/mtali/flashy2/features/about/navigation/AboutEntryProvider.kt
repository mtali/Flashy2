package com.mtali.flashy2.features.about.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.mtali.flashy2.core.navigation.Navigator
import com.mtali.flashy2.features.about.AboutRoute
import kotlinx.serialization.Serializable

@Serializable
data object AboutNavKey : NavKey

fun EntryProviderScope<NavKey>.aboutEntry(navigator: Navigator) {
  entry<AboutNavKey> {
    AboutRoute(onBackClick = navigator::goBack)
  }
}
