package com.video.chat.v.love.chatroulette.domain.repository

import android.content.Context
import com.video.chat.v.love.chatroulette.domain.repositoryImpl.TokenRepository
import com.video.chat.v.love.chatroulette.extensions.get
import com.video.chat.v.love.chatroulette.extensions.put
import com.video.chat.v.love.chatroulette.extensions.remove
import javax.inject.Inject

class FurnestateTokenRepository @Inject constructor(
    context: Context,
) : TokenRepository {

    private val sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    override fun saveAuthToken(token: String, refreshToken: String) {
        sharedPreferences.apply {
            put(TOKEN, token)
            put(REFRESH_TOKEN, refreshToken)
        }
    }

    override fun getToken(): String {
        return sharedPreferences.get(TOKEN, "") ?: ""
    }

    override fun deleteToken() {
        sharedPreferences.remove(TOKEN)
    }

    override fun saveToken(token: String) {
        sharedPreferences.put(TOKEN, token)
    }

    override fun getRefreshToken(): String {
        return sharedPreferences.get(REFRESH_TOKEN, "") ?: ""
    }

    override fun saveRefreshToken(refreshToken: String) {
        sharedPreferences.put(REFRESH_TOKEN, refreshToken)
    }

    override fun isTokenExists(): Boolean {
        return getToken().isNotBlank()
    }

    companion object {
        private const val FILE_NAME = "AUTH_TOKENS"
        private const val TOKEN = "TOKEN"
        private const val REFRESH_TOKEN = "REFRESH_TOKEN"
    }
}