package com.radiohack.tloyalty.data.repository

import com.radiohack.tloyalty.core.network.ApiResult
import com.radiohack.tloyalty.domain.model.AiInsights
import com.radiohack.tloyalty.domain.model.Analytics
import com.radiohack.tloyalty.domain.model.CrossSell
import com.radiohack.tloyalty.domain.model.Dashboard
import com.radiohack.tloyalty.domain.model.DemoProfile
import com.radiohack.tloyalty.domain.model.Gamification
import com.radiohack.tloyalty.domain.model.MissedBenefit
import com.radiohack.tloyalty.domain.model.OffersBundle

interface LoyaltyRepository {
    suspend fun health(): ApiResult<Unit>
    suspend fun demoProfiles(): ApiResult<List<DemoProfile>>
    suspend fun dashboard(userId: Int): ApiResult<Dashboard>
    suspend fun analytics(userId: Int): ApiResult<Analytics>
    suspend fun offers(userId: Int): ApiResult<OffersBundle>
    suspend fun aiInsights(userId: Int): ApiResult<AiInsights>
    suspend fun gamification(userId: Int): ApiResult<Gamification>
    suspend fun crossSell(userId: Int): ApiResult<CrossSell>
    suspend fun missedBenefit(userId: Int): ApiResult<MissedBenefit>
}
