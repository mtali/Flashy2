package com.mtali.flashy2.app.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.mtali.flashy2.core.navigation.Navigator
import com.mtali.flashy2.features.about.navigation.aboutEntry
import com.mtali.flashy2.features.flashlight.navigation.flashlightEntry
import com.mtali.flashy2.features.settings.navigation.settingsEntry

@Composable
fun FlashyNavDisplay(
  navigator: Navigator,
  modifier: Modifier = Modifier,
) {
  val entryProvider =
    entryProvider {
      flashlightEntry(navigator)
      settingsEntry(navigator)
      aboutEntry(navigator)
    }

  if (navigator.backStack.isNotEmpty()) {
    NavDisplay(
      modifier = modifier,
      backStack = navigator.backStack,
      onBack = { navigator.goBack() },
      entryDecorators =
      listOf(
        rememberSaveableStateHolderNavEntryDecorator(),
        rememberViewModelStoreNavEntryDecorator(),
      ),
      entryProvider = entryProvider,
      transitionSpec = {
        slideInHorizontally(initialOffsetX = { it }) togetherWith slideOutHorizontally(targetOffsetX = { -it })
      },
      popTransitionSpec = {
        slideInHorizontally(initialOffsetX = { -it }) togetherWith slideOutHorizontally(targetOffsetX = { it })
      },
      predictivePopTransitionSpec = {
        slideInHorizontally(initialOffsetX = { -it }) togetherWith slideOutHorizontally(targetOffsetX = { it })
      },
    )
  }
}
