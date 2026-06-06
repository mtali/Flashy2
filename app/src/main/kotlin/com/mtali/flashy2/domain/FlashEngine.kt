package com.mtali.flashy2.domain

import com.mtali.flashy2.core.dispatchers.Dispatcher
import com.mtali.flashy2.core.dispatchers.FlashyDispatchers
import com.mtali.flashy2.core.torch.TorchController
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

enum class FlashMode { OFF, TORCH, SOS, STROBOSCOPE }

data class FlashState(
  val mode: FlashMode = FlashMode.OFF,
  val strength: Int = 1,
)

/**
 * Single source of truth for the camera torch. Replaces the original `CameraHelper` singleton's raw
 * `Thread`s and busy-wait loops with cancellable coroutine [Job]s: switching modes simply cancels
 * the previous job, which turns the torch off in its `finally` block. Modes are mutually exclusive.
 */
@Singleton
class FlashEngine
@Inject
constructor(
  private val torch: TorchController,
  @Dispatcher(FlashyDispatchers.Default) dispatcher: CoroutineDispatcher,
) {
  private val scope = CoroutineScope(SupervisorJob() + dispatcher)

  private val _state = MutableStateFlow(FlashState())
  val state: StateFlow<FlashState> = _state.asStateFlow()

  val hasFlashUnit: Boolean get() = torch.hasFlashUnit
  val maxStrength: Int get() = torch.maxStrength
  val supportsStrength: Boolean get() = torch.supportsStrength

  private var blinkJob: Job? = null

  @Volatile
  private var strobeIntervalMs: Long = 500

  /** Toggles steady torch: off if already in torch mode, otherwise switches to torch. */
  fun toggleTorch() {
    if (_state.value.mode == FlashMode.TORCH) {
      turnOff()
      return
    }
    cancelBlink()
    torch.turnOn(_state.value.strength)
    _state.update { it.copy(mode = FlashMode.TORCH) }
  }

  /** Toggles the SOS pattern using the supplied ON/OFF [timings] (see [com.mtali.flashy2.core.morse.MorseTimer]). */
  fun toggleSos(timings: List<Long>) {
    if (_state.value.mode == FlashMode.SOS) {
      turnOff()
      return
    }
    startBlink(FlashMode.SOS) {
      var i = 0
      while (isActive) {
        torch.turnOn(_state.value.strength)
        delay(timings[i++ % timings.size])
        torch.turnOff()
        delay(timings[i++ % timings.size])
      }
    }
  }

  /** Toggles the stroboscope at [intervalMs] (the live interval can be changed via [setStrobeInterval]). */
  fun toggleStrobe(intervalMs: Long) {
    if (_state.value.mode == FlashMode.STROBOSCOPE) {
      turnOff()
      return
    }
    strobeIntervalMs = intervalMs
    startBlink(FlashMode.STROBOSCOPE) {
      while (isActive) {
        torch.turnOn(_state.value.strength)
        delay(strobeIntervalMs)
        torch.turnOff()
        delay(strobeIntervalMs)
      }
    }
  }

  /** Updates the live stroboscope interval without restarting the loop. */
  fun setStrobeInterval(intervalMs: Long) {
    strobeIntervalMs = intervalMs
  }

  /** Sets the torch strength (clamped to the device range) and re-applies it if the torch is steady-on. */
  fun setStrength(strength: Int) {
    val clamped = strength.coerceIn(1, maxStrength)
    _state.update { it.copy(strength = clamped) }
    if (_state.value.mode == FlashMode.TORCH) {
      torch.turnOn(clamped)
    }
  }

  /** Turns everything off. */
  fun turnOff() {
    cancelBlink()
    torch.turnOff()
    _state.update { it.copy(mode = FlashMode.OFF) }
  }

  private fun cancelBlink() {
    blinkJob?.cancel()
    blinkJob = null
  }

  private fun startBlink(mode: FlashMode, block: suspend CoroutineScope.() -> Unit) {
    cancelBlink()
    torch.turnOff()
    _state.update { it.copy(mode = mode) }
    blinkJob =
      scope.launch {
        try {
          block()
        } finally {
          withContext(NonCancellable) { torch.turnOff() }
        }
      }
  }
}
