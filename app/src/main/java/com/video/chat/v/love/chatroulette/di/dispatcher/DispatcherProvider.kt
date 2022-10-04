package com.video.chat.v.love.chatroulette.di.dispatcher

import kotlinx.coroutines.CoroutineDispatcher

interface DispatcherProvider {
    val IO: CoroutineDispatcher
    val DEFAULT: CoroutineDispatcher
    val MAIN: CoroutineDispatcher
}