package com.mtali.flashy2.core.datastore

/** Which light source the main screen defaults to. */
enum class LightMode { TORCH, SCREEN }

/** App theme preference. */
enum class ThemeConfig { LIGHT, DARK, SYSTEM }

/** Immutable snapshot of all persisted user preferences. */
data class UserSettings(
  val theme: ThemeConfig = ThemeConfig.SYSTEM,
  val defaultMode: LightMode = LightMode.TORCH,
  val wordsPerMinute: Int = 5,
  val useFarnsworth: Boolean = false,
  /** Farnsworth unit length in ms; 0 means "derive from the dit length". */
  val farnsworthUnitMs: Int = 0,
  val noFlashWhenScreen: Boolean = true,
  val noFlashOnScreenOff: Boolean = false,
  val strobeIntervalSec: Float = 0.5f,
  /** Torch strength level; -1 means "unset, use the device maximum". */
  val flashStrength: Int = -1,
)
