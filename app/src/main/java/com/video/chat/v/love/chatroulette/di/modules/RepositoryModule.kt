package com.video.chat.v.love.chatroulette.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.video.chat.v.love.chatroulette.domain.repository.FurnestateTokenRepository
import com.video.chat.v.love.chatroulette.domain.repositoryImpl.TokenRepository

import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {

//    @Singleton
//    @Provides
//    fun provideUserDataRepository(sharedPreferences: SharedPreferences): UserDataRepository {
//        return FurnestateDataRepository(sharedPreferences)
//    }

    @Singleton
    @Provides
    fun provideTokenRepository(@ApplicationContext context: Context): TokenRepository {
        return FurnestateTokenRepository(context)
    }

}