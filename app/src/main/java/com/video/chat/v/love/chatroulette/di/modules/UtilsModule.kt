package com.video.chat.v.love.chatroulette.di.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.video.chat.v.love.chatroulette.network.exeption.ErrorConverter
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class UtilsModule {

    companion object {

        @Provides
        @Singleton
        fun provideErrorConverter(retrofit: Retrofit) = ErrorConverter(retrofit)
    }
}