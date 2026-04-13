package com.jionifamily.presentation.shared.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jionifamily.domain.model.MissionCompletion
import com.jionifamily.domain.model.WeeklyStats
import com.jionifamily.domain.repository.MissionRepository
import com.jionifamily.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryState(
    val weeks: List<WeeklyStats> = emptyList(),
    val selectedWeek: String? = null,
    val weekCompletions: List<MissionCompletion> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingCompletions: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val missionRepository: MissionRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = missionRepository.getHistory()) {
                is Result.Success -> {
                    // Skip current week (index 0), show only past weeks
                    val pastWeeks = if (result.data.size > 1) result.data.drop(1) else emptyList()
                    _state.update { it.copy(weeks = pastWeeks, isLoading = false, error = null) }
                    if (pastWeeks.isNotEmpty()) {
                        selectWeek(pastWeeks.first().weekStart)
                    }
                }
                is Result.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    fun selectWeek(weekStart: String) {
        _state.update { it.copy(selectedWeek = weekStart, isLoadingCompletions = true) }
        viewModelScope.launch {
            // Trigger lazy creation + close past completions
            missionRepository.getMissions(weekStart)
            when (val result = missionRepository.getCompletions(week = weekStart)) {
                is Result.Success -> {
                    _state.update { it.copy(weekCompletions = result.data, isLoadingCompletions = false) }
                }
                is Result.Error -> {
                    _state.update { it.copy(isLoadingCompletions = false) }
                }
            }
        }
    }
}
