package com.video.chat.v.love.chatroulette.presentation.auth.login

import com.video.chat.v.love.chatroulette.domain.usecasesImpl.RegisterUserUseCase
import com.video.chat.v.love.chatroulette.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase,
) : BaseViewModel() {

    //TODO add isTokenExists when we can signup
    fun isTokenExists(): Boolean {
        return false
    }

}