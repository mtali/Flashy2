package com.mtali.flashy2.core.navigation

import androidx.navigation3.runtime.NavKey

/** Imperative navigation API over a [NavigationState] back stack, mirroring the starapp convention. */
class Navigator(val state: NavigationState) {
  val backStack get() = state.backStack

  fun navigate(key: NavKey) {
    state.backStack.add(key)
  }

  fun goBack() {
    if (state.backStack.size > 1) {
      state.backStack.removeAt(state.backStack.lastIndex)
    }
  }
}
