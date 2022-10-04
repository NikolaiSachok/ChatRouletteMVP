package com.video.chat.v.love.chatroulette.network.data.videos

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Videos(
    var id: String,
    var videoUrl: String,
    var image: String,
    var title: String,
    var gender: String,
    var timeToSkip: Long,
): Parcelable
