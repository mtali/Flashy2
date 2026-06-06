package com.mtali.flashy2.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.mtali.flashy2.core.dispatchers.Dispatcher
import com.mtali.flashy2.core.dispatchers.FlashyDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
  private const val SETTINGS_NAME = "flashy_settings"

  @Provides
  @Singleton
  fun providePreferencesDataStore(
    @ApplicationContext context: Context,
    @Dispatcher(FlashyDispatchers.IO) ioDispatcher: CoroutineDispatcher,
  ): DataStore<Preferences> = PreferenceDataStoreFactory.create(
    corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
    scope = CoroutineScope(ioDispatcher + SupervisorJob()),
    produceFile = { context.preferencesDataStoreFile(SETTINGS_NAME) },
  )
}
