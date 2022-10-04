package com.video.chat.v.love.chatroulette.network.body

import com.google.gson.annotations.SerializedName

data class RegistrationBody(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    val email: String,
    val phone: String,
    val password: String,
    @SerializedName("password_confirmation") val passwordСonfirmation: String,
)
