package com.mtali.flashy2.core.morse

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MorseTimerTest {
  @Test
  fun ditLength_matchesParisFormula() {
    // 60 / (50 * wpm) * 1000
    assertThat(MorseTimer.ditLengthMs(5)).isEqualTo(240)
    assertThat(MorseTimer.ditLengthMs(10)).isEqualTo(120)
    assertThat(MorseTimer.ditLengthMs(20)).isEqualTo(60)
  }

  @Test
  fun sosTimings_hasFullCycleShape() {
    val dit = 240L
    val timings = MorseTimer.sosTimings(dit)

    assertThat(timings).hasSize(18)
    // First three S dots are single units (ON, gap, ON, gap, ON).
    assertThat(timings[0]).isEqualTo(dit)
    assertThat(timings[4]).isEqualTo(dit)
    // Letter gap after S and the O dahs are three units.
    assertThat(timings[5]).isEqualTo(dit * 3)
    assertThat(timings[6]).isEqualTo(dit * 3)
    // Trailing word gap is seven units.
    assertThat(timings[17]).isEqualTo(dit * 7)
  }

  @Test
  fun sosTimings_farnsworthStretchesOnlyGaps() {
    val dit = 240L
    val fw = 600L
    val timings = MorseTimer.sosTimings(dit, farnsworthUnitMs = fw)

    // Element timing (dits/dahs) is unchanged...
    assertThat(timings[0]).isEqualTo(dit)
    assertThat(timings[6]).isEqualTo(dit * 3)
    // ...while the inter-letter and inter-word gaps use the Farnsworth unit.
    assertThat(timings[5]).isEqualTo(fw * 3)
    assertThat(timings[11]).isEqualTo(fw * 3)
    assertThat(timings[17]).isEqualTo(fw * 7)
  }
}
