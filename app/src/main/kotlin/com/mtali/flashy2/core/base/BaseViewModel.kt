package com.mtali.flashy2.core.base

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Shared ViewModel base providing a loading flag and a one-shot toast channel, mirroring the
 * starapp convention so screens can `observeToasts(viewModel)` uniformly.
 */
abstract class BaseViewModel : ViewModel() {
  private val _isLoading = MutableStateFlow(false)
  val isLoading = _isLoading.asStateFlow()

  private val _toasts = Channel<Int>(Channel.BUFFERED)
  val toasts = _toasts.receiveAsFlow()

  protected fun showToast(
    @StringRes messageRes: Int,
  ) {
    viewModelScope.launch { _toasts.send(messageRes) }
  }

  protected fun performWithLoading(block: suspend () -> Unit) {
    viewModelScope.launch {
      _isLoading.value = true
      try {
        block()
      } finally {
        _isLoading.value = false
      }
    }
  }
}
