package com.video.chat.v.love.chatroulette.domain.usecasesImpl

import com.video.chat.v.love.chatroulette.network.body.RegistrationBody
import com.video.chat.v.love.chatroulette.network.data.registration.RegistrationData
import com.video.chat.v.love.chatroulette.network.data.videos.UserVideosData


interface RegisterUserUseCase {

    suspend fun registerUser(registrationBody: RegistrationBody): RegistrationData?

    suspend fun getUserVideos(): UserVideosData?

}