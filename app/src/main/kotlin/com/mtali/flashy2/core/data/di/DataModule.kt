package com.mtali.flashy2.core.data.di

import com.mtali.flashy2.core.data.repository.SettingsRepository
import com.mtali.flashy2.core.data.repository.impl.SettingsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
  @Binds
  @Singleton
  abstract fun bindsSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
