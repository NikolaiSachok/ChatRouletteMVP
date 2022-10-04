package com.video.chat.v.love.chatroulette.network.header

interface HeaderStorage {
    fun getApiHeaders(existingHeaders: Map<String, String>): Map<String, String>
}