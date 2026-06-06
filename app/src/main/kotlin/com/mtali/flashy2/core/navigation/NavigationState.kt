package com.mtali.flashy2.core.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.navigation3.runtime.NavKey

/** Holds the Navigation3 back stack as observable Compose state. */
class NavigationState(startKey: NavKey) {
  val backStack = mutableStateListOf(startKey)
}
