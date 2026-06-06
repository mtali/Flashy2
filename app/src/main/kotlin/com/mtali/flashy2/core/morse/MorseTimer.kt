package com.mtali.flashy2.core.morse

import kotlin.math.roundToLong

/**
 * Pure Morse-code timing maths, ported verbatim from the original Flashy `CameraHelper`.
 *
 * The SOS pattern is emitted by alternating the torch ON/OFF, starting with ON, using consecutive
 * entries of [sosTimings] as the durations (index 0 = first ON, index 1 = first OFF, ...).
 */
object MorseTimer {
  /**
   * Reference SOS pattern (`... --- ...`) expressed with the canonical 1/3/7-unit ratios, where a
   * basic unit (`dit`) is 250 and a `dah`/letter-gap is 750 and the trailing word-gap is 1750.
   */
  private val SOS_REFERENCE =
    listOf<Long>(250, 250, 250, 250, 250, 750, 750, 250, 750, 250, 750, 750, 250, 250, 250, 250, 250, 1750)

  /** The length of a `dit` in milliseconds for the given words-per-minute. */
  fun ditLengthMs(wordsPerMinute: Int): Long {
    require(wordsPerMinute > 0) { "wordsPerMinute must be positive" }
    return (60.0 / (50 * wordsPerMinute) * 1000).roundToLong()
  }

  /**
   * The ON/OFF durations (ms) for one full `... --- ...` cycle.
   *
   * @param ditMs the basic unit length, e.g. from [ditLengthMs].
   * @param farnsworthUnitMs when non-null, stretches the inter-letter and inter-word gaps to this
   *   unit (Farnsworth timing) while leaving the element timing untouched.
   */
  fun sosTimings(ditMs: Long, farnsworthUnitMs: Long? = null): List<Long> {
    val dah = ditMs * 3
    // Every 750 in the reference is a dah/letter-gap (3 units); everything else collapses to a dit.
    val timings = SOS_REFERENCE.map { if (it == 750L) dah else ditMs }.toMutableList()
    timings[timings.lastIndex] = ditMs * 7 // trailing word gap
    if (farnsworthUnitMs != null) {
      timings[5] = farnsworthUnitMs * 3 // S -> O letter gap
      timings[11] = farnsworthUnitMs * 3 // O -> S letter gap
      timings[timings.lastIndex] = farnsworthUnitMs * 7 // word gap
    }
    return timings
  }
}
