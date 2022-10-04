package com.video.chat.v.love.chatroulette.network.data.registration

import com.google.gson.annotations.SerializedName

data class Data(
    var name: String,
    var email: String,
    @SerializedName("updated_at") var updatedAt: String,
    @SerializedName("created_at") var createdAt: String,
    var id: Int,
)
