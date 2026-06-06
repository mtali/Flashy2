package com.mtali.flashy2.core.base

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.collectLatest

/** Collects [BaseViewModel.toasts] and shows them as Android toasts. */
@Composable
fun ObserveToasts(viewModel: BaseViewModel) {
  val context = LocalContext.current
  LaunchedEffect(viewModel) {
    viewModel.toasts.collectLatest { messageRes ->
      Toast.makeText(context, messageRes, Toast.LENGTH_SHORT).show()
    }
  }
}
