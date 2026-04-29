package com.radiohack.tloyalty.presentation.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radiohack.tloyalty.core.network.ApiResult
import com.radiohack.tloyalty.data.repository.LoyaltyRepository
import com.radiohack.tloyalty.domain.model.AiInsights
import com.radiohack.tloyalty.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AssistantViewModel(
    private val userId: Int,
    private val repository: LoyaltyRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<AiInsights>>(UiState.Loading)
    val uiState: StateFlow<UiState<AiInsights>> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        if (userId <= 0) {
            _uiState.value = UiState.Error("Некорректный userId для ассистента.")
            return
        }
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = repository.aiInsights(userId)) {
                is ApiResult.Success -> _uiState.value = if (result.data.insights.isEmpty()) UiState.Empty else UiState.Success(result.data)
                is ApiResult.Error -> _uiState.value = UiState.Error(result.message)
            }
        }
    }
}
