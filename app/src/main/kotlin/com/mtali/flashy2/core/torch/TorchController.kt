package com.mtali.flashy2.core.torch

/**
 * Thin abstraction over the device camera torch, so the [com.mtali.flashy2.domain.FlashEngine] and
 * its tests do not depend on the Android camera APIs directly.
 */
interface TorchController {
  /** Whether the device has a camera flash unit at all. */
  val hasFlashUnit: Boolean

  /** Maximum supported strength level (1 when the device has no brightness control). */
  val maxStrength: Int

  /** Whether the torch brightness can be controlled (Android 13+ with a capable LED). */
  val supportsStrength: Boolean
    get() = maxStrength > 1

  /**
   * Turns the torch on. When [strength] is provided and the device [supportsStrength], the torch is
   * turned on at that level; otherwise it is turned on at full power.
   */
  fun turnOn(strength: Int? = null)

  /** Turns the torch off. */
  fun turnOff()
}
