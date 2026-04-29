package com.radiohack.tloyalty.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radiohack.tloyalty.core.network.ApiResult
import com.radiohack.tloyalty.data.repository.LoyaltyRepository
import com.radiohack.tloyalty.domain.model.Dashboard
import com.radiohack.tloyalty.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val userId: Int,
    private val repository: LoyaltyRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<Dashboard>>(UiState.Loading)
    val uiState: StateFlow<UiState<Dashboard>> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        if (userId <= 0) {
            _uiState.value = UiState.Error("Некорректный userId для dashboard.")
            return
        }
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = repository.dashboard(userId)) {
                is ApiResult.Success -> _uiState.value = UiState.Success(result.data)
                is ApiResult.Error -> _uiState.value = UiState.Error(result.message)
            }
        }
    }
}
