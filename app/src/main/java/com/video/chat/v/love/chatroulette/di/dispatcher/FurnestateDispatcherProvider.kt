package com.video.chat.v.love.chatroulette.di.dispatcher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

data class FurnestateDispatcherProvider(
    override val IO: CoroutineDispatcher = Dispatchers.IO,
    override val DEFAULT: CoroutineDispatcher = Dispatchers.Default,
    override val MAIN: CoroutineDispatcher = Dispatchers.Main,
) : DispatcherProvider
