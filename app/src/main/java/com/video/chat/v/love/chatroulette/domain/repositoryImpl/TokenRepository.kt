package com.video.chat.v.love.chatroulette.domain.repositoryImpl

interface TokenRepository {
    fun saveAuthToken(token: String, refreshToken: String)
    fun getToken(): String
    fun deleteToken()
    fun saveToken(token: String)
    fun getRefreshToken(): String
    fun saveRefreshToken(refreshToken: String)
    fun isTokenExists(): Boolean
}