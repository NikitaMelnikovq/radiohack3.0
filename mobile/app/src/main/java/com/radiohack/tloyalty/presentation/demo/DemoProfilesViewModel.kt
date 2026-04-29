package com.radiohack.tloyalty.presentation.demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radiohack.tloyalty.core.network.ApiResult
import com.radiohack.tloyalty.data.local.UserPreferences
import com.radiohack.tloyalty.data.repository.LoyaltyRepository
import com.radiohack.tloyalty.domain.model.DemoProfile
import com.radiohack.tloyalty.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DemoProfilesViewModel(
    private val repository: LoyaltyRepository,
    private val userPreferences: UserPreferences,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<DemoProfilesData>>(UiState.Loading)
    val uiState: StateFlow<UiState<DemoProfilesData>> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = repository.demoProfiles()) {
                is ApiResult.Success -> {
                    _uiState.value = if (result.data.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success(
                            DemoProfilesData(
                                profiles = result.data,
                                selectedUser = userPreferences.selectedUser(),
                            ),
                        )
                    }
                }
                is ApiResult.Error -> _uiState.value = UiState.Error(result.message)
            }
        }
    }

    fun selectProfile(profile: DemoProfile) {
        userPreferences.saveSelectedUser(profile)
    }
}
