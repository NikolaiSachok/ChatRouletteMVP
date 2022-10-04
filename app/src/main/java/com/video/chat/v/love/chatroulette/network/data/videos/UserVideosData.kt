package com.video.chat.v.love.chatroulette.network.data.videos

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserVideosData(
    var videosFemale : ArrayList<Videos> = arrayListOf(),
    var videosMale : ArrayList<Videos> = arrayListOf(),
    var swipes : Int
): Parcelable
