package com.jionifamily.presentation.login

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

data class LoginUiState(
    val selectedAvatar: String? = null,
    val pin: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state

    private val _loginSuccess = MutableSharedFlow<String>() // emits role
    val loginSuccess: SharedFlow<String> = _loginSuccess

    fun selectAvatar(avatarKey: String) {
        _state.update { it.copy(selectedAvatar = avatarKey, pin = "", error = null) }
    }

    fun appendPin(digit: String) {
        if (_state.value.pin.length >= 4) return
        val newPin = _state.value.pin + digit
        _state.update { it.copy(pin = newPin, error = null) }
        if (newPin.length == 4) {
            login()
        }
    }

    fun deletePin() {
        if (_state.value.pin.isEmpty()) return
        _state.update { it.copy(pin = it.pin.dropLast(1), error = null) }
    }

    fun clearPin() {
        _state.update { it.copy(pin = "", error = null) }
    }

    private fun login() {
        val avatar = _state.value.selectedAvatar ?: return
        val pin = _state.value.pin

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = authRepository.login(avatar, pin)) {
                is Result.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    _loginSuccess.emit(result.data.role.name.lowercase())
                }
                is Result.Error -> {
                    _state.update { it.copy(isLoading = false, pin = "", error = result.message) }
                }
            }
        }
    }
}
