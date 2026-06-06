package com.mtali.flashy2.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey

@Composable
fun rememberNavigator(startKey: NavKey): Navigator = remember { Navigator(NavigationState(startKey)) }
