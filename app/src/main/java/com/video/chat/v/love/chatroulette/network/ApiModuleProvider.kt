package com.video.chat.v.love.chatroulette.network

import retrofit2.Retrofit

interface ApiModuleProvider<T> {
    fun provideApi(retrofit: Retrofit): T
}