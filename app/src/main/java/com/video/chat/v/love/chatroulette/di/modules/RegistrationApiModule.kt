package com.video.chat.v.love.chatroulette.di.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.video.chat.v.love.chatroulette.network.ApiModuleProvider
import com.video.chat.v.love.chatroulette.network.api.RegistrationApi

import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RegistrationApiModule : ApiModuleProvider<RegistrationApi> {
    @Singleton
    @Provides
    override fun provideApi(retrofit: Retrofit): RegistrationApi {
        return retrofit.create(RegistrationApi::class.java)
    }
}
