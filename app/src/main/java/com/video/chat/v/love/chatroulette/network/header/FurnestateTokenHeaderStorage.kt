package com.video.chat.v.love.chatroulette.network.header

import com.video.chat.v.love.chatroulette.domain.repositoryImpl.TokenRepository
import com.video.chat.v.love.chatroulette.constants.Constants
import javax.inject.Inject

class FurnestateTokenHeaderStorage @Inject constructor(
    private val tokenRepository: TokenRepository,
) : HeaderStorage {

    override fun getApiHeaders(existingHeaders: Map<String, String>): Map<String, String> {
        val headers = HashMap(existingHeaders)
        if (tokenRepository.isTokenExists()) {
            headers[Constants.AUTHORIZATION] = "Bearer ${getToken()}"
        }
        return headers
    }

    private fun getToken(): String {
        return tokenRepository.getToken()
    }
}