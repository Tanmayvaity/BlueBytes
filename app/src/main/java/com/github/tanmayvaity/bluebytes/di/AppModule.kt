package com.github.tanmayvaity.bluebytes.di

import android.content.Context
import com.github.tanmayvaity.bluebytes.core.data.bluetooth.BluetoothManagerServiceImpl
import com.github.tanmayvaity.bluebytes.core.domain.repository.BluetoothManagerService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object  AppModule {

    @Provides
    @Singleton
    fun provideBluetoothManagerService(
        @ApplicationContext context : Context
    ) : BluetoothManagerService {
        return BluetoothManagerServiceImpl(context)
    }
}