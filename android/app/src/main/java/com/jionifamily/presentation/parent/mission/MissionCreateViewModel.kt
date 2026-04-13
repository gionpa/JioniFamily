package com.jionifamily.presentation.parent.mission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jionifamily.domain.repository.MissionRepository
import com.jionifamily.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MissionCreateState(
    val name: String = "",
    val description: String = "",
    val rewardCoins: Int = 10,
    val category: String = "other",
    val isRecurring: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class MissionCreateViewModel @Inject constructor(
    private val missionRepository: MissionRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(MissionCreateState())
    val state: StateFlow<MissionCreateState> = _state

    private val _created = MutableSharedFlow<Unit>()
    val created: SharedFlow<Unit> = _created

    fun updateName(name: String) = _state.update { it.copy(name = name) }
    fun updateDescription(desc: String) = _state.update { it.copy(description = desc) }
    fun updateCategory(cat: String) = _state.update { it.copy(category = cat) }
    fun toggleRecurring() = _state.update { it.copy(isRecurring = !it.isRecurring) }

    fun incrementCoins() {
        _state.update { it.copy(rewardCoins = (it.rewardCoins + 5).coerceAtMost(100)) }
    }

    fun decrementCoins() {
        _state.update { it.copy(rewardCoins = (it.rewardCoins - 5).coerceAtLeast(1)) }
    }

    fun createMission() {
        val s = _state.value
        if (s.name.isBlank()) {
            _state.update { it.copy(error = "미션 이름을 입력해주세요") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = missionRepository.createMission(
                name = s.name,
                description = s.description.ifBlank { null },
                rewardCoins = s.rewardCoins,
                category = s.category,
                isRecurring = s.isRecurring,
            )
            when (result) {
                is Result.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    _created.emit(Unit)
                }
                is Result.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }
}
