package com.video.chat.v.love.chatroulette.extensions

import okhttp3.Request

fun Request.headersToStorage(): Map<String, String> {
    val headersMap = LinkedHashMap<String, String>()
    for (i in 0 until headers.size) {
        headersMap[headers.name(i)] = headers.value(i)
    }
    return headersMap
}