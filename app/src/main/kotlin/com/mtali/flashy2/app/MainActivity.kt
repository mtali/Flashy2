package com.mtali.flashy2.app

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mtali.flashy2.app.ui.FlashyApp
import com.mtali.flashy2.core.datastore.ThemeConfig
import com.mtali.flashy2.core.ui.theme.FlashyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  private val viewModel by viewModels<MainViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    // Keep the screen awake while the app is in the foreground (it is a light, after all).
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    setContent {
      val theme by viewModel.theme.collectAsStateWithLifecycle()
      val darkTheme =
        when (theme) {
          ThemeConfig.LIGHT -> false
          ThemeConfig.DARK -> true
          ThemeConfig.SYSTEM -> isSystemInDarkTheme()
        }
      FlashyTheme(darkTheme = darkTheme) {
        FlashyApp()
      }
    }
  }
}
