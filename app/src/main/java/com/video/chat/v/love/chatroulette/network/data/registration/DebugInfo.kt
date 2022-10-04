package com.video.chat.v.love.chatroulette.network.data.registration

import com.google.gson.annotations.SerializedName

data class DebugInfo(
    @SerializedName("db_queries") var dbQueries: Int,
    @SerializedName("db_time") var dbTime: Double,
    var time: Double,
)
