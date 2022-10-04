package com.video.chat.v.love.chatroulette.di.modules.usecases

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.video.chat.v.love.chatroulette.domain.usecases.RegisterUserUseCaseNetwork
import com.video.chat.v.love.chatroulette.domain.usecasesImpl.RegisterUserUseCase
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface UseCasesModule {

    @Binds
    @Singleton
    fun bindRegisterUserUseCase(useCase: RegisterUserUseCaseNetwork): RegisterUserUseCase
}