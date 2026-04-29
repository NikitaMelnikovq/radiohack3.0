package com.radiohack.tloyalty

import android.content.Context
import com.radiohack.tloyalty.core.network.NetworkModule
import com.radiohack.tloyalty.data.local.UserPreferences
import com.radiohack.tloyalty.data.repository.LoyaltyRepository
import com.radiohack.tloyalty.data.repository.LoyaltyRepositoryImpl

class AppContainer(context: Context) {
    val userPreferences: UserPreferences = UserPreferences(context)
    val loyaltyRepository: LoyaltyRepository = LoyaltyRepositoryImpl(NetworkModule.createApi())
}
