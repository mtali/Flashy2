package com.mtali.flashy2.core.dispatchers.di

import com.mtali.flashy2.core.dispatchers.Dispatcher
import com.mtali.flashy2.core.dispatchers.FlashyDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
  @Provides
  @Dispatcher(FlashyDispatchers.Default)
  fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

  @Provides
  @Dispatcher(FlashyDispatchers.IO)
  fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

  @Provides
  @Dispatcher(FlashyDispatchers.Main)
  fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}
