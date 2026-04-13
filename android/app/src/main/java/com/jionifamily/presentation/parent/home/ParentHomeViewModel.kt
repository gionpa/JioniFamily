package com.jionifamily.presentation.parent.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jionifamily.data.local.DataStoreManager
import com.jionifamily.domain.model.MissionCompletion
import com.jionifamily.domain.repository.MissionRepository
import com.jionifamily.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ParentHomeState(
    val userName: String = "",
    val weekStart: String = "",
    val pendingApprovals: List<MissionCompletion> = emptyList(),
    val completions: List<MissionCompletion> = emptyList(),
    val childCoinBalance: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class ParentHomeViewModel @Inject constructor(
    private val missionRepository: MissionRepository,
    private val dataStore: DataStoreManager,
) : ViewModel() {

    private val _state = MutableStateFlow(ParentHomeState())
    val state: StateFlow<ParentHomeState> = _state

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val name = dataStore.getUserName() ?: "부모"
            _state.update { it.copy(userName = name) }

            // Load missions first to trigger lazy completion creation
            missionRepository.getMissions()

            // Then load completions
            when (val result = missionRepository.getCompletions()) {
                is Result.Success -> {
                    val pending = result.data.filter { it.status.name == "SUBMITTED" }
                    val weekStart = result.data.firstOrNull()?.weekStart ?: ""
                    _state.update {
                        it.copy(
                            completions = result.data,
                            pendingApprovals = pending,
                            weekStart = weekStart,
                            isLoading = false,
                            error = null,
                        )
                    }
                }
                is Result.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    fun approveCompletion(completionId: String) {
        viewModelScope.launch {
            missionRepository.approveCompletion(completionId, null)
            loadData()
        }
    }

    fun rejectCompletion(completionId: String) {
        viewModelScope.launch {
            missionRepository.rejectCompletion(completionId, null)
            loadData()
        }
    }
}
