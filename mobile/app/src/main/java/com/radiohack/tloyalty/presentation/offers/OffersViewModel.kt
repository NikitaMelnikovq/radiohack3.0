package com.radiohack.tloyalty.presentation.offers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radiohack.tloyalty.core.network.ApiResult
import com.radiohack.tloyalty.data.repository.LoyaltyRepository
import com.radiohack.tloyalty.domain.model.OffersBundle
import com.radiohack.tloyalty.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OffersViewModel(
    private val userId: Int,
    private val repository: LoyaltyRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<OffersBundle>>(UiState.Loading)
    val uiState: StateFlow<UiState<OffersBundle>> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        if (userId <= 0) {
            _uiState.value = UiState.Error("Некорректный userId для офферов.")
            return
        }
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = repository.offers(userId)) {
                is ApiResult.Success -> _uiState.value = if (result.data.offers.isEmpty()) UiState.Empty else UiState.Success(result.data)
                is ApiResult.Error -> _uiState.value = UiState.Error(result.message)
            }
        }
    }
}
