package com.jionifamily.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jionifamily.domain.model.UserRole
import com.jionifamily.domain.repository.AuthRepository
import com.jionifamily.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashEvent {
    object NavigateToLogin : SplashEvent()
    object NavigateToParentHome : SplashEvent()
    object NavigateToChildHome : SplashEvent()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _event = MutableSharedFlow<SplashEvent>()
    val event: SharedFlow<SplashEvent> = _event

    init {
        viewModelScope.launch {
            delay(1500) // Show splash animation
            when (val result = authRepository.autoLogin()) {
                is Result.Success -> {
                    if (result.data.role == UserRole.PARENT) {
                        _event.emit(SplashEvent.NavigateToParentHome)
                    } else {
                        _event.emit(SplashEvent.NavigateToChildHome)
                    }
                }
                is Result.Error -> {
                    _event.emit(SplashEvent.NavigateToLogin)
                }
            }
        }
    }
}
