package com.mtali.flashy2.core.torch

class FakeTorchController(
  override val hasFlashUnit: Boolean = true,
  override val maxStrength: Int = 5,
) : TorchController {
  var isOn: Boolean = false
    private set
  var lastStrength: Int? = null
    private set
  var turnOnCount: Int = 0
    private set
  var turnOffCount: Int = 0
    private set

  override fun turnOn(strength: Int?) {
    isOn = true
    lastStrength = strength
    turnOnCount++
  }

  override fun turnOff() {
    isOn = false
    turnOffCount++
  }
}
