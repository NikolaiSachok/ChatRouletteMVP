package com.video.chat.v.love.chatroulette.domain.usecases

import com.video.chat.v.love.chatroulette.domain.usecasesImpl.RegisterUserUseCase
import com.video.chat.v.love.chatroulette.network.api.RegistrationApi
import com.video.chat.v.love.chatroulette.network.body.RegistrationBody
import com.video.chat.v.love.chatroulette.network.data.registration.RegistrationData
import com.video.chat.v.love.chatroulette.network.data.videos.UserVideosData
import com.video.chat.v.love.chatroulette.network.exeption.ErrorConverter
import com.video.chat.v.love.chatroulette.network.exeption.RequestDataException
import com.video.chat.v.love.chatroulette.di.dispatcher.DispatcherProvider
import javax.inject.Inject

//    private val responseFormatter: ResponseFormatter,
//    private val userDataRepository: UserDataRepository
class RegisterUserUseCaseNetwork @Inject constructor(
    private val api: RegistrationApi,
    private val dispatcherProvider: DispatcherProvider,
    private val errorConverter: ErrorConverter,
) : RegisterUserUseCase {


    override suspend fun registerUser(registrationBody: RegistrationBody): RegistrationData? {
        val response = api.signUpUser(registrationBody)
        if (!response.isSuccessful) {
            try {
                throw RequestDataException(errorConverter.convertRetrofitError(response.errorBody()).message)
            } catch (ex: Exception) {
//                throw Exception(response.errorBody().toString())
            }
        }
        return response.body()
    }

    override suspend fun getUserVideos(): UserVideosData? {
        val response = api.getVideosData()
        return response.body()
    }
}

