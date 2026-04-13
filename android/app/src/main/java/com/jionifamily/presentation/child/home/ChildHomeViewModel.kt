package com.jionifamily.presentation.child.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jionifamily.data.local.DataStoreManager
import com.jionifamily.data.remote.api.UserApi
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

data class ChildHomeState(
    val userName: String = "",
    val coinBalance: Int = 0,
    val weekStart: String = "",
    val completions: List<MissionCompletion> = emptyList(),
    val weeklyStats: WeeklyStats? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class ChildHomeViewModel @Inject constructor(
    private val missionRepository: MissionRepository,
    private val userApi: UserApi,
    private val dataStore: DataStoreManager,
) : ViewModel() {

    private val _state = MutableStateFlow(ChildHomeState())
    val state: StateFlow<ChildHomeState> = _state

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val name = dataStore.getUserName() ?: "지온이"
            _state.update { it.copy(userName = name) }

            // Get fresh coin balance
            try {
                val meResponse = userApi.getMe()
                if (meResponse.isSuccessful) {
                    _state.update { it.copy(coinBalance = meResponse.body()!!.coinBalance) }
                }
            } catch (_: Exception) {}

            // Load missions first to trigger lazy completion creation
            missionRepository.getMissions()

            // Load completions
            when (val result = missionRepository.getCompletions()) {
                is Result.Success -> {
                    val weekStart = result.data.firstOrNull()?.weekStart ?: ""
                    _state.update { it.copy(completions = result.data, weekStart = weekStart, isLoading = false, error = null) }
                }
                is Result.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
            }

            // Load weekly stats
            when (val stats = missionRepository.getWeeklyStats()) {
                is Result.Success -> _state.update { it.copy(weeklyStats = stats.data) }
                is Result.Error -> {}
            }
        }
    }

    fun submitMission(missionId: String) {
        viewModelScope.launch {
            missionRepository.submitMission(missionId)
            loadData()
        }
    }
}
