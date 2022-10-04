package com.video.chat.v.love.chatroulette.network.data.registration

import com.google.gson.annotations.SerializedName

data class RegistrationData(
    var data: Data,
    var status: String,
    @SerializedName("debug_info") var debugInfo: DebugInfo,
)
