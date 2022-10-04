package com.video.chat.v.love.chatroulette.network.data.events

import com.video.chat.v.love.chatroulette.utils.comparator.IdEntity

data class EventsData(
    override val id: Int,
    val time: String,
    val name: String,
    val location: String,
    val titleDescription: String,
    val code: String,
    val description: String
): IdEntity(id)
