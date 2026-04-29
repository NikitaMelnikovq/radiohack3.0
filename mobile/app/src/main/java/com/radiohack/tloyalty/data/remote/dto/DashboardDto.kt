package com.radiohack.tloyalty.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DashboardDto(
    val user: UserPreviewDto? = null,
    @SerializedName("loyalty_summary") val loyaltySummary: LoyaltySummaryDto? = null,
    val analytics: AnalyticsDto? = null,
    val forecast: ForecastDto? = null,
    val offers: OffersDto? = null,
    @SerializedName("cross_sell") val crossSell: CrossSellDto? = null,
    val gamification: GamificationDto? = null,
    @SerializedName("ai_insights") val aiInsights: AiInsightsDto? = null,
    @SerializedName("missed_benefit") val missedBenefit: MissedBenefitDto? = null,
    @SerializedName("dashboard_score") val dashboardScore: DashboardScoreDto? = null,
)

data class DashboardScoreDto(
    val score: Int? = null,
    val status: String? = null,
    val title: String? = null,
    val description: String? = null,
    val factors: List<DashboardScoreFactorDto>? = null,
    @SerializedName("next_best_action") val nextBestAction: DashboardNextBestActionDto? = null,
)

data class DashboardScoreFactorDto(
    val code: String? = null,
    val label: String? = null,
    val value: Int? = null,
    @SerializedName("max_value") val maxValue: Int? = null,
    val explanation: String? = null,
)

data class DashboardNextBestActionDto(
    val title: String? = null,
    val description: String? = null,
    @SerializedName("cta_label") val ctaLabel: String? = null,
)
