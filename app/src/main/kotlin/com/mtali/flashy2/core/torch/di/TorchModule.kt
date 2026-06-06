package com.mtali.flashy2.core.torch.di

import com.mtali.flashy2.core.torch.TorchController
import com.mtali.flashy2.core.torch.TorchControllerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TorchModule {
  @Binds
  @Singleton
  abstract fun bindsTorchController(impl: TorchControllerImpl): TorchController
}
