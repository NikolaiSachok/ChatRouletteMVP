package com.video.chat.v.love.chatroulette.network.interceptor

import com.video.chat.v.love.chatroulette.extensions.headersToStorage
import com.video.chat.v.love.chatroulette.network.header.HeaderStorage
import okhttp3.Headers
import okhttp3.Headers.Companion.toHeaders
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class InterceptorHeader @Inject constructor(
    private val headerStorage: HeaderStorage,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val headers = getHeadersForRequest(chain.request())
        val newRequest = chain.request().newBuilder().headers(headers).build()
        return chain.proceed(newRequest)
    }

    private fun getHeadersForRequest(request: Request): Headers {
        return headerStorage.getApiHeaders(request.headersToStorage()).toHeaders()
    }
}