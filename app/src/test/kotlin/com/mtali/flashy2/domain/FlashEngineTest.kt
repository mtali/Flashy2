package com.mtali.flashy2.domain

import com.google.common.truth.Truth.assertThat
import com.mtali.flashy2.core.torch.FakeTorchController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FlashEngineTest {
  private val sosTimings = listOf(10L, 10L, 10L, 10L)

  private fun engineWith(torch: FakeTorchController, scheduler: kotlinx.coroutines.test.TestCoroutineScheduler) = FlashEngine(torch, UnconfinedTestDispatcher(scheduler))

  @Test
  fun toggleTorch_turnsTorchOn() = runTest {
    val torch = FakeTorchController()
    val engine = engineWith(torch, testScheduler)

    engine.toggleTorch()

    assertThat(engine.state.value.mode).isEqualTo(FlashMode.TORCH)
    assertThat(torch.isOn).isTrue()
  }

  @Test
  fun toggleTorch_twice_turnsOff() = runTest {
    val torch = FakeTorchController()
    val engine = engineWith(torch, testScheduler)

    engine.toggleTorch()
    engine.toggleTorch()

    assertThat(engine.state.value.mode).isEqualTo(FlashMode.OFF)
    assertThat(torch.isOn).isFalse()
  }

  @Test
  fun toggleSos_whileTorchOn_switchesToSosAndBlinks() = runTest {
    val torch = FakeTorchController()
    val engine = engineWith(torch, testScheduler)

    engine.toggleTorch()
    engine.toggleSos(sosTimings)

    assertThat(engine.state.value.mode).isEqualTo(FlashMode.SOS)
    // The blink loop ran at least its first ON before suspending on delay.
    assertThat(torch.isOn).isTrue()

    engine.turnOff()
    assertThat(engine.state.value.mode).isEqualTo(FlashMode.OFF)
    assertThat(torch.isOn).isFalse()
  }

  @Test
  fun startingStrobe_whileSos_isMutuallyExclusive() = runTest {
    val torch = FakeTorchController()
    val engine = engineWith(torch, testScheduler)

    engine.toggleSos(sosTimings)
    engine.toggleStrobe(20)

    assertThat(engine.state.value.mode).isEqualTo(FlashMode.STROBOSCOPE)

    engine.turnOff()
  }

  @Test
  fun setStrength_clampsToRange_andReappliesWhenOn() = runTest {
    val torch = FakeTorchController(maxStrength = 5)
    val engine = engineWith(torch, testScheduler)

    engine.toggleTorch()
    engine.setStrength(99)

    assertThat(engine.state.value.strength).isEqualTo(5)
    assertThat(torch.lastStrength).isEqualTo(5)
  }
}
