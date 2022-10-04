package com.video.chat.v.love.chatroulette.network

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.video.chat.v.love.chatroulette.App
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object OkHttpModuleProvider {

    private const val TIMEOUT = 60L

    fun provideOkHttpClient(headerInterceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder().apply {
            addInterceptor(getHttpLoggingInterceptor())
            addInterceptor(headerInterceptor)
            addInterceptor(ChuckerInterceptor(App.applicationContext()))
            connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            readTimeout(TIMEOUT, TimeUnit.SECONDS)
        }.build()
    }

    private fun getHttpLoggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
}