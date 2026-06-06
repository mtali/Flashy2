package com.mtali.flashy2.app

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mtali.flashy2.app.ui.FlashyApp
import com.mtali.flashy2.core.ui.theme.FlashyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    // Keep the screen awake while the app is in the foreground (it is a light, after all).
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    setContent {
      FlashyTheme {
        FlashyApp()
      }
    }
  }
}
