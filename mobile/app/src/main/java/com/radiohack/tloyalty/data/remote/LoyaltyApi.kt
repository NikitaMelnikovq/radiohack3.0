package com.radiohack.tloyalty.data.remote

import com.radiohack.tloyalty.data.remote.dto.AiInsightsDto
import com.radiohack.tloyalty.data.remote.dto.AnalyticsDto
import com.radiohack.tloyalty.data.remote.dto.CrossSellDto
import com.radiohack.tloyalty.data.remote.dto.DashboardDto
import com.radiohack.tloyalty.data.remote.dto.DemoProfilesDto
import com.radiohack.tloyalty.data.remote.dto.GamificationDto
import com.radiohack.tloyalty.data.remote.dto.HealthDto
import com.radiohack.tloyalty.data.remote.dto.MissedBenefitDto
import com.radiohack.tloyalty.data.remote.dto.OffersDto
import retrofit2.http.GET
import retrofit2.http.Path

interface LoyaltyApi {
    @GET("api/health")
    suspend fun health(): HealthDto

    @GET("api/demo/profiles")
    suspend fun demoProfiles(): DemoProfilesDto

    @GET("api/users/{user_id}/dashboard")
    suspend fun dashboard(@Path("user_id") userId: Int): DashboardDto

    @GET("api/users/{user_id}/loyalty/analytics")
    suspend fun analytics(@Path("user_id") userId: Int): AnalyticsDto

    @GET("api/users/{user_id}/offers")
    suspend fun offers(@Path("user_id") userId: Int): OffersDto

    @GET("api/users/{user_id}/ai-insights")
    suspend fun aiInsights(@Path("user_id") userId: Int): AiInsightsDto

    @GET("api/users/{user_id}/gamification")
    suspend fun gamification(@Path("user_id") userId: Int): GamificationDto

    @GET("api/users/{user_id}/cross-sell")
    suspend fun crossSell(@Path("user_id") userId: Int): CrossSellDto

    @GET("api/users/{user_id}/missed-benefit")
    suspend fun missedBenefit(@Path("user_id") userId: Int): MissedBenefitDto
}
