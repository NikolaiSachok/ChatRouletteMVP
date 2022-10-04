package com.video.chat.v.love.chatroulette.network.api

import com.video.chat.v.love.chatroulette.network.body.RegistrationBody
import com.video.chat.v.love.chatroulette.network.data.registration.RegistrationData
import com.video.chat.v.love.chatroulette.network.data.videos.UserVideosData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RegistrationApi {

    @POST("config.json")
    suspend fun signUpUser(@Body body: RegistrationBody): Response<RegistrationData>

    @POST("config.json")
    suspend fun getVideosData(): Response<UserVideosData>
}