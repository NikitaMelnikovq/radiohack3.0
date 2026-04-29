package com.radiohack.tloyalty.domain.model

data class DemoProfile(
    val userId: Int,
    val label: String,
    val description: String,
    val financialSegment: String,
    val highlightMetrics: List<String>,
    val recommendedDemoFlow: List<String>,
)

data class UserPreview(
    val id: Int,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val financialSegment: String,
    val accountsCount: Int,
    val totalCashbackValue: Double,
)

data class Dashboard(
    val user: UserPreview,
    val loyaltySummary: LoyaltySummary,
    val analytics: Analytics,
    val forecast: Forecast,
    val offers: OffersBundle,
    val crossSell: CrossSell,
    val gamification: Gamification,
    val aiInsights: AiInsights,
    val missedBenefit: MissedBenefit,
    val dashboardScore: DashboardScore,
)

data class LoyaltySummary(
    val user: LoyaltySummaryUser,
    val accounts: List<LoyaltyAccountSummary>,
    val totalsByCurrency: List<CurrencyAmount>,
    val totalTransactions: Int,
    val lastPayoutDate: String?,
)

data class LoyaltySummaryUser(
    val id: Int,
    val fullName: String,
    val financialSegment: String,
)

data class LoyaltyAccountSummary(
    val accountId: Int,
    val loyaltyProgram: String,
    val cashbackCurrency: String,
    val currentBalance: Double,
    val totalCashback: Double,
    val transactionsCount: Int,
)

data class CurrencyAmount(
    val currency: String,
    val amount: Double,
)

data class Analytics(
    val monthlyDynamics: List<MonthlyDynamic>,
    val programBreakdown: List<ProgramBreakdown>,
    val bestProgram: BestProgram?,
    val averageMonthlyCashback: List<CurrencyAmount>,
)

data class MonthlyDynamic(
    val month: String,
    val currency: String,
    val amount: Double,
)

data class ProgramBreakdown(
    val loyaltyProgram: String,
    val currency: String,
    val amount: Double,
    val sharePercent: Double,
)

data class BestProgram(
    val loyaltyProgram: String,
    val currency: String,
    val amount: Double,
)

data class Forecast(
    val forecastPeriodDays: Int,
    val method: String,
    val items: List<ForecastItem>,
    val explanation: String,
)

data class ForecastItem(
    val currency: String,
    val predictedAmount: Double,
    val confidence: String,
)

data class OffersBundle(
    val userSegment: String,
    val offers: List<Offer>,
)

data class Offer(
    val partnerId: Int,
    val partnerName: String,
    val shortDescription: String,
    val logoUrl: String?,
    val brandColorHex: String?,
    val cashbackPercent: Double,
    val financialSegment: String,
    val reason: String,
)

data class CrossSell(
    val userId: Int,
    val financialSegment: String,
    val recommendations: List<CrossSellRecommendation>,
)

data class CrossSellRecommendation(
    val productCode: String,
    val productName: String,
    val category: String,
    val priority: Int,
    val score: Int,
    val title: String,
    val description: String,
    val reason: String,
    val evidence: List<String>,
    val ctaLabel: String,
)

data class AiInsights(
    val userId: Int,
    val method: String,
    val title: String,
    val summary: String,
    val insights: List<AiInsight>,
    val quickQuestions: List<QuickQuestion>,
)

data class AiInsight(
    val insightId: String,
    val type: String,
    val priority: Int,
    val title: String,
    val description: String,
    val reason: String,
    val evidence: List<String>,
    val confidence: String,
    val ctaLabel: String,
    val businessGoal: String?,
    val businessImpact: String?,
)

data class QuickQuestion(
    val question: String,
    val answer: String,
)

data class Gamification(
    val userId: Int,
    val level: LoyaltyLevel,
    val badges: List<LoyaltyBadge>,
    val challenges: List<LoyaltyChallenge>,
)

data class LoyaltyLevel(
    val code: String,
    val name: String,
    val currentPoints: Int,
    val nextLevel: String?,
    val pointsToNextLevel: Int,
    val progressPercent: Double,
)

data class LoyaltyBadge(
    val code: String,
    val title: String,
    val description: String,
)

data class LoyaltyChallenge(
    val challengeId: String,
    val title: String,
    val description: String,
    val rewardText: String,
    val progressPercent: Int,
    val difficulty: String,
)

data class DashboardScore(
    val score: Int,
    val status: String,
    val title: String,
    val description: String,
    val factors: List<DashboardScoreFactor>,
    val nextBestAction: DashboardNextBestAction,
)

data class DashboardScoreFactor(
    val code: String,
    val label: String,
    val value: Int,
    val maxValue: Int,
    val explanation: String,
)

data class DashboardNextBestAction(
    val title: String,
    val description: String,
    val ctaLabel: String,
)

data class MissedBenefit(
    val userId: Int,
    val method: String,
    val upliftFactor: Double,
    val items: List<MissedBenefitItem>,
    val topOfferCashbackPercent: Double?,
    val explanation: String,
)

data class MissedBenefitItem(
    val currency: String,
    val averageMonthlyAmount: Double,
    val potentialExtraAmount: Double,
)
