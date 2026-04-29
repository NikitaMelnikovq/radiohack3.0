package com.radiohack.tloyalty.presentation.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radiohack.tloyalty.core.network.ApiResult
import com.radiohack.tloyalty.data.repository.LoyaltyRepository
import com.radiohack.tloyalty.domain.model.Analytics
import com.radiohack.tloyalty.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnalyticsViewModel(
    private val userId: Int,
    private val repository: LoyaltyRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<Analytics>>(UiState.Loading)
    val uiState: StateFlow<UiState<Analytics>> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        if (userId <= 0) {
            _uiState.value = UiState.Error("Некорректный userId для аналитики.")
            return
        }
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = repository.analytics(userId)) {
                is ApiResult.Success -> _uiState.value = if (
                    result.data.monthlyDynamics.isEmpty() &&
                    result.data.programBreakdown.isEmpty()
                ) {
                    UiState.Empty
                } else {
                    UiState.Success(result.data)
                }
                is ApiResult.Error -> _uiState.value = UiState.Error(result.message)
            }
        }
    }
}
