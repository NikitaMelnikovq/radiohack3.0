package com.radiohack.tloyalty.data.repository

import com.radiohack.tloyalty.core.network.ApiResult
import com.radiohack.tloyalty.core.network.NetworkErrorMapper
import com.radiohack.tloyalty.data.remote.LoyaltyApi
import com.radiohack.tloyalty.domain.mapper.toDomain
import com.radiohack.tloyalty.domain.model.AiInsights
import com.radiohack.tloyalty.domain.model.Analytics
import com.radiohack.tloyalty.domain.model.CrossSell
import com.radiohack.tloyalty.domain.model.Dashboard
import com.radiohack.tloyalty.domain.model.DemoProfile
import com.radiohack.tloyalty.domain.model.Gamification
import com.radiohack.tloyalty.domain.model.MissedBenefit
import com.radiohack.tloyalty.domain.model.OffersBundle

class LoyaltyRepositoryImpl(
    private val api: LoyaltyApi,
) : LoyaltyRepository {
    override suspend fun health(): ApiResult<Unit> = safeCall {
        api.health()
        Unit
    }

    override suspend fun demoProfiles(): ApiResult<List<DemoProfile>> = safeCall {
        api.demoProfiles().toDomain()
    }

    override suspend fun dashboard(userId: Int): ApiResult<Dashboard> = safeCall {
        api.dashboard(userId).toDomain()
    }

    override suspend fun analytics(userId: Int): ApiResult<Analytics> = safeCall {
        api.analytics(userId).toDomain()
    }

    override suspend fun offers(userId: Int): ApiResult<OffersBundle> = safeCall {
        api.offers(userId).toDomain()
    }

    override suspend fun aiInsights(userId: Int): ApiResult<AiInsights> = safeCall {
        api.aiInsights(userId).toDomain()
    }

    override suspend fun gamification(userId: Int): ApiResult<Gamification> = safeCall {
        api.gamification(userId).toDomain()
    }

    override suspend fun crossSell(userId: Int): ApiResult<CrossSell> = safeCall {
        api.crossSell(userId).toDomain()
    }

    override suspend fun missedBenefit(userId: Int): ApiResult<MissedBenefit> = safeCall {
        api.missedBenefit(userId).toDomain()
    }

    private suspend fun <T> safeCall(block: suspend () -> T): ApiResult<T> {
        return try {
            ApiResult.Success(block())
        } catch (throwable: Throwable) {
            ApiResult.Error(NetworkErrorMapper.map(throwable), throwable)
        }
    }
}
