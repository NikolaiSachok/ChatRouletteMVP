package com.video.chat.v.love.chatroulette.presentation.start

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.video.chat.v.love.chatroulette.domain.usecasesImpl.RegisterUserUseCase
import com.video.chat.v.love.chatroulette.network.data.videos.UserVideosData
import com.video.chat.v.love.chatroulette.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TapToStartViewModel  @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase,
) : BaseViewModel() {

    private val _registrationData = MutableLiveData<UserVideosData?>()
    val registrationData: LiveData<UserVideosData?>
        get() = _registrationData

    fun getUserSwipes() {
        scope.launch {
            _registrationData.postValue(registerUserUseCase.getUserVideos())
        }
    }

}