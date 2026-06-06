package com.mtali.flashy2.core.dispatchers

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val dispatcher: FlashyDispatchers)

enum class FlashyDispatchers {
  Default,
  IO,
  Main,
}
