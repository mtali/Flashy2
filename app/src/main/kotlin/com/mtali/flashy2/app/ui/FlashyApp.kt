package com.mtali.flashy2.app.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mtali.flashy2.app.navigation.FlashyNavDisplay
import com.mtali.flashy2.core.navigation.rememberNavigator
import com.mtali.flashy2.features.flashlight.navigation.FlashlightNavKey

@Composable
fun FlashyApp(modifier: Modifier = Modifier) {
  val navigator = rememberNavigator(FlashlightNavKey)
  FlashyNavDisplay(navigator = navigator, modifier = modifier.fillMaxSize())
}
