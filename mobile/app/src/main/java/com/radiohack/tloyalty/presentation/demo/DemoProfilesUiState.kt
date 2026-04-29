package com.radiohack.tloyalty.presentation.demo

import com.radiohack.tloyalty.data.local.SelectedUser
import com.radiohack.tloyalty.domain.model.DemoProfile

data class DemoProfilesData(
    val profiles: List<DemoProfile>,
    val selectedUser: SelectedUser,
)
