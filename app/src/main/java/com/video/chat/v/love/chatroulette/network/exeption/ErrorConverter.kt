package com.video.chat.v.love.chatroulette.network.exeption

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit

class ErrorConverter(private val retrofit: Retrofit) {
    fun convertRetrofitError(errorBody: ResponseBody?): ErrorData {
        return errorBody?.let {
            val errorConverter: Converter<ResponseBody, ErrorData> =
                retrofit.responseBodyConverter(ErrorData::class.java, arrayOf())
            errorConverter.convert(errorBody)
        } ?: throw IllegalArgumentException("Error body should not be null")
    }
}

data class ErrorData(val status: String, val code: String, val message: String)