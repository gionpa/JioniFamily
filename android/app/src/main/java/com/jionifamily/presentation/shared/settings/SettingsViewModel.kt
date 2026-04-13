package com.jionifamily.presentation.shared.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jionifamily.domain.repository.AuthRepository
import com.jionifamily.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChangePinState(
    val showDialog: Boolean = false,
    val oldPin: String = "",
    val newPin: String = "",
    val confirmPin: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent: SharedFlow<Unit> = _logoutEvent

    private val _pinSuccessEvent = MutableSharedFlow<Unit>()
    val pinSuccessEvent: SharedFlow<Unit> = _pinSuccessEvent

    private val _changePinState = MutableStateFlow(ChangePinState())
    val changePinState: StateFlow<ChangePinState> = _changePinState

    fun showChangePinDialog() {
        _changePinState.value = ChangePinState(showDialog = true)
    }

    fun dismissChangePinDialog() {
        _changePinState.value = ChangePinState()
    }

    fun updateOldPin(pin: String) {
        if (pin.length <= 4 && pin.all { it.isDigit() }) {
            _changePinState.update { it.copy(oldPin = pin, error = null) }
        }
    }

    fun updateNewPin(pin: String) {
        if (pin.length <= 4 && pin.all { it.isDigit() }) {
            _changePinState.update { it.copy(newPin = pin, error = null) }
        }
    }

    fun updateConfirmPin(pin: String) {
        if (pin.length <= 4 && pin.all { it.isDigit() }) {
            _changePinState.update { it.copy(confirmPin = pin, error = null) }
        }
    }

    fun submitPinChange() {
        val state = _changePinState.value
        if (state.oldPin.length != 4) {
            _changePinState.update { it.copy(error = "현재 PIN 4자리를 입력해주세요") }
            return
        }
        if (state.newPin.length != 4) {
            _changePinState.update { it.copy(error = "새 PIN 4자리를 입력해주세요") }
            return
        }
        if (state.newPin != state.confirmPin) {
            _changePinState.update { it.copy(error = "새 PIN이 일치하지 않아요") }
            return
        }

        viewModelScope.launch {
            _changePinState.update { it.copy(isLoading = true, error = null) }
            when (val result = authRepository.changePin(state.oldPin, state.newPin)) {
                is Result.Success -> {
                    _changePinState.value = ChangePinState()
                    _pinSuccessEvent.emit(Unit)
                }
                is Result.Error -> {
                    _changePinState.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _logoutEvent.emit(Unit)
        }
    }
}
