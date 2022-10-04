package com.video.chat.v.love.chatroulette.network

import androidx.annotation.Nullable
import com.video.chat.v.love.chatroulette.BuildConfig.BASE_URL
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.video.chat.v.love.chatroulette.domain.repositoryImpl.TokenRepository
import com.video.chat.v.love.chatroulette.network.header.FurnestateTokenHeaderStorage
import com.video.chat.v.love.chatroulette.network.header.HeaderStorage
import com.video.chat.v.love.chatroulette.network.interceptor.InterceptorHeader
import com.video.chat.v.love.chatroulette.di.dispatcher.DispatcherProvider
import com.video.chat.v.love.chatroulette.di.dispatcher.FurnestateDispatcherProvider
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {
    private const val TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'"

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider {
        return FurnestateDispatcherProvider()
    }

    @Provides
    @Singleton
    fun provideRetrofit(builder: Retrofit.Builder, client: OkHttpClient): Retrofit {
        return builder
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofitBuilder(converterFactory: Converter.Factory): Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(converterFactory)
    }

    @Provides
    @Singleton
    fun provideConverterFactory(@Nullable gson: Gson): Converter.Factory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setDateFormat(TIME_PATTERN)
            .create()
    }

    @Provides
    @Singleton
    fun provideTokenHeaderStorage(tokenRepository: TokenRepository): HeaderStorage {
        return FurnestateTokenHeaderStorage(tokenRepository)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(headerInterceptor: Interceptor): OkHttpClient {
        return OkHttpModuleProvider.provideOkHttpClient(headerInterceptor)
    }

    @Provides
    @Singleton
    fun provideTokenHeaderInterceptor(headerStorage: HeaderStorage): Interceptor {
        return InterceptorHeader(headerStorage)
    }
}