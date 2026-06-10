package com.github.tanmayvaity.bluebytes.di

import android.content.Context
import androidx.room.Room
import com.github.tanmayvaity.bluebytes.core.data.bluetooth.BluetoothManagerServiceImpl
import com.github.tanmayvaity.bluebytes.core.data.local.BlueBytesDatabase
import com.github.tanmayvaity.bluebytes.core.data.local.ChatDao
import com.github.tanmayvaity.bluebytes.core.data.repository.ChatRepositoryImpl
import com.github.tanmayvaity.bluebytes.core.data.repository.ThemeRepositoryImpl
import com.github.tanmayvaity.bluebytes.core.domain.repository.BluetoothManagerService
import com.github.tanmayvaity.bluebytes.core.domain.repository.ChatRepository
import com.github.tanmayvaity.bluebytes.core.domain.repository.ThemeRepository
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

    @Provides
    @Singleton
    fun provideBlueBytesDatabase(
        @ApplicationContext context: Context
    ): BlueBytesDatabase {
        return Room.databaseBuilder(
            context,
            BlueBytesDatabase::class.java,
            "bluebytes.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideChatDao(database: BlueBytesDatabase): ChatDao {
        return database.chatDao()
    }

    @Provides
    @Singleton
    fun provideChatRepository(chatDao: ChatDao): ChatRepository {
        return ChatRepositoryImpl(chatDao)
    }

    @Provides
    @Singleton
    fun provideThemeRepository(
        @ApplicationContext context: Context
    ): ThemeRepository {
        return ThemeRepositoryImpl(context)
    }
}
