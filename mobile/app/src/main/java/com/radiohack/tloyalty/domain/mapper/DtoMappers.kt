package com.radiohack.tloyalty.domain.mapper

import com.radiohack.tloyalty.data.remote.dto.AiInsightDto
import com.radiohack.tloyalty.data.remote.dto.AiInsightsDto
import com.radiohack.tloyalty.data.remote.dto.AnalyticsDto
import com.radiohack.tloyalty.data.remote.dto.BestProgramDto
import com.radiohack.tloyalty.data.remote.dto.CrossSellDto
import com.radiohack.tloyalty.data.remote.dto.CrossSellRecommendationDto
import com.radiohack.tloyalty.data.remote.dto.CurrencyAmountDto
import com.radiohack.tloyalty.data.remote.dto.DashboardDto
import com.radiohack.tloyalty.data.remote.dto.DashboardNextBestActionDto
import com.radiohack.tloyalty.data.remote.dto.DashboardScoreDto
import com.radiohack.tloyalty.data.remote.dto.DashboardScoreFactorDto
import com.radiohack.tloyalty.data.remote.dto.DemoProfileDto
import com.radiohack.tloyalty.data.remote.dto.DemoProfilesDto
import com.radiohack.tloyalty.data.remote.dto.ForecastDto
import com.radiohack.tloyalty.data.remote.dto.ForecastItemDto
import com.radiohack.tloyalty.data.remote.dto.GamificationDto
import com.radiohack.tloyalty.data.remote.dto.LoyaltyAccountSummaryDto
import com.radiohack.tloyalty.data.remote.dto.LoyaltyBadgeDto
import com.radiohack.tloyalty.data.remote.dto.LoyaltyChallengeDto
import com.radiohack.tloyalty.data.remote.dto.LoyaltyLevelDto
import com.radiohack.tloyalty.data.remote.dto.LoyaltySummaryDto
import com.radiohack.tloyalty.data.remote.dto.LoyaltySummaryUserDto
import com.radiohack.tloyalty.data.remote.dto.MissedBenefitDto
import com.radiohack.tloyalty.data.remote.dto.MissedBenefitItemDto
import com.radiohack.tloyalty.data.remote.dto.MonthlyDynamicDto
import com.radiohack.tloyalty.data.remote.dto.OfferDto
import com.radiohack.tloyalty.data.remote.dto.OffersDto
import com.radiohack.tloyalty.data.remote.dto.ProgramBreakdownDto
import com.radiohack.tloyalty.data.remote.dto.QuickQuestionDto
import com.radiohack.tloyalty.data.remote.dto.UserPreviewDto
import com.radiohack.tloyalty.domain.model.AiInsight
import com.radiohack.tloyalty.domain.model.AiInsights
import com.radiohack.tloyalty.domain.model.Analytics
import com.radiohack.tloyalty.domain.model.BestProgram
import com.radiohack.tloyalty.domain.model.CrossSell
import com.radiohack.tloyalty.domain.model.CrossSellRecommendation
import com.radiohack.tloyalty.domain.model.CurrencyAmount
import com.radiohack.tloyalty.domain.model.Dashboard
import com.radiohack.tloyalty.domain.model.DashboardNextBestAction
import com.radiohack.tloyalty.domain.model.DashboardScore
import com.radiohack.tloyalty.domain.model.DashboardScoreFactor
import com.radiohack.tloyalty.domain.model.DemoProfile
import com.radiohack.tloyalty.domain.model.Forecast
import com.radiohack.tloyalty.domain.model.ForecastItem
import com.radiohack.tloyalty.domain.model.Gamification
import com.radiohack.tloyalty.domain.model.LoyaltyAccountSummary
import com.radiohack.tloyalty.domain.model.LoyaltyBadge
import com.radiohack.tloyalty.domain.model.LoyaltyChallenge
import com.radiohack.tloyalty.domain.model.LoyaltyLevel
import com.radiohack.tloyalty.domain.model.LoyaltySummary
import com.radiohack.tloyalty.domain.model.LoyaltySummaryUser
import com.radiohack.tloyalty.domain.model.MissedBenefit
import com.radiohack.tloyalty.domain.model.MissedBenefitItem
import com.radiohack.tloyalty.domain.model.MonthlyDynamic
import com.radiohack.tloyalty.domain.model.Offer
import com.radiohack.tloyalty.domain.model.OffersBundle
import com.radiohack.tloyalty.domain.model.ProgramBreakdown
import com.radiohack.tloyalty.domain.model.QuickQuestion
import com.radiohack.tloyalty.domain.model.UserPreview
import kotlin.math.roundToInt

fun DemoProfilesDto.toDomain(): List<DemoProfile> {
    return profiles.orEmpty().mapNotNull { it.toDomainOrNull() }
}

private fun DemoProfileDto.toDomainOrNull(): DemoProfile? {
    val id = userId ?: return null
    return DemoProfile(
        userId = id,
        label = label.orDash(),
        description = description.orEmpty(),
        financialSegment = financialSegment.orUnknownSegment(),
        highlightMetrics = highlightMetrics.orEmpty().filter { it.isNotBlank() },
        recommendedDemoFlow = recommendedDemoFlow.orEmpty().filter { it.isNotBlank() },
    )
}

fun DashboardDto.toDomain(): Dashboard {
    return Dashboard(
        user = user.toDomain(),
        loyaltySummary = loyaltySummary.toDomain(),
        analytics = analytics.toDomain(),
        forecast = forecast.toDomain(),
        offers = offers.toDomain(),
        crossSell = crossSell.toDomain(),
        gamification = gamification.toDomain(),
        aiInsights = aiInsights.toDomain(),
        missedBenefit = missedBenefit.toDomain(),
        dashboardScore = dashboardScore.toDomain(),
    )
}

fun AnalyticsDto?.toDomain(): Analytics {
    return Analytics(
        monthlyDynamics = this?.monthlyDynamics.orEmpty().map { it.toDomain() },
        programBreakdown = this?.programBreakdown.orEmpty().map { it.toDomain() },
        bestProgram = this?.bestProgram?.toDomain(),
        averageMonthlyCashback = this?.averageMonthlyCashback.orEmpty().map { it.toDomain() },
    )
}

fun OffersDto?.toDomain(): OffersBundle {
    return OffersBundle(
        userSegment = this?.userSegment.orUnknownSegment(),
        offers = this?.offers.orEmpty().map { it.toDomain() },
    )
}

fun AiInsightsDto?.toDomain(): AiInsights {
    return AiInsights(
        userId = this?.userId ?: 0,
        method = this?.method.orEmpty(),
        title = this?.title.orDefault("AI-ассистент выгоды"),
        summary = this?.summary.orEmpty(),
        insights = this?.insights.orEmpty().map { it.toDomain() }.sortedBy { it.priority },
        quickQuestions = this?.quickQuestions.orEmpty().map { it.toDomain() },
    )
}

fun GamificationDto?.toDomain(): Gamification {
    return Gamification(
        userId = this?.userId ?: 0,
        level = this?.level.toDomain(),
        badges = this?.badges.orEmpty().map { it.toDomain() },
        challenges = this?.challenges.orEmpty().map { it.toDomain() },
    )
}

fun CrossSellDto?.toDomain(): CrossSell {
    return CrossSell(
        userId = this?.userId ?: 0,
        financialSegment = this?.financialSegment.orUnknownSegment(),
        recommendations = this?.recommendations.orEmpty().map { it.toDomain() }.sortedBy { it.priority },
    )
}

private fun LoyaltySummaryDto?.toDomain(): LoyaltySummary {
    return LoyaltySummary(
        user = this?.user.toDomain(),
        accounts = this?.accounts.orEmpty().map { it.toDomain() },
        totalsByCurrency = this?.totalsByCurrency.orEmpty().map { it.toDomain() }.filter { it.amount > 0.0 },
        totalTransactions = this?.totalTransactions ?: 0,
        lastPayoutDate = this?.lastPayoutDate,
    )
}

private fun UserPreviewDto?.toDomain(): UserPreview {
    return UserPreview(
        id = this?.id ?: 0,
        fullName = this?.fullName.orDefault("Демо-пользователь"),
        email = this?.email.orEmpty(),
        phoneNumber = this?.phoneNumber.orEmpty(),
        financialSegment = this?.financialSegment.orUnknownSegment(),
        accountsCount = this?.accountsCount ?: 0,
        totalCashbackValue = this?.totalCashbackValue.safeDouble(),
    )
}

private fun LoyaltySummaryUserDto?.toDomain(): LoyaltySummaryUser {
    return LoyaltySummaryUser(
        id = this?.id ?: 0,
        fullName = this?.fullName.orDefault("Демо-пользователь"),
        financialSegment = this?.financialSegment.orUnknownSegment(),
    )
}

private fun LoyaltyAccountSummaryDto.toDomain(): LoyaltyAccountSummary {
    return LoyaltyAccountSummary(
        accountId = accountId ?: 0,
        loyaltyProgram = loyaltyProgram.orDash(),
        cashbackCurrency = cashbackCurrency.orEmpty(),
        currentBalance = currentBalance.safeDouble(),
        totalCashback = totalCashback.safeDouble(),
        transactionsCount = transactionsCount ?: 0,
    )
}

private fun CurrencyAmountDto.toDomain(): CurrencyAmount {
    return CurrencyAmount(
        currency = currency.orEmpty(),
        amount = amount.safeDouble(),
    )
}

private fun MonthlyDynamicDto.toDomain(): MonthlyDynamic {
    return MonthlyDynamic(
        month = month.orEmpty(),
        currency = currency.orEmpty(),
        amount = amount.safeDouble(),
    )
}

private fun ProgramBreakdownDto.toDomain(): ProgramBreakdown {
    return ProgramBreakdown(
        loyaltyProgram = loyaltyProgram.orDash(),
        currency = currency.orEmpty(),
        amount = amount.safeDouble(),
        sharePercent = sharePercent.safeDouble().coerceIn(0.0, 100.0),
    )
}

private fun BestProgramDto.toDomain(): BestProgram {
    return BestProgram(
        loyaltyProgram = loyaltyProgram.orDash(),
        currency = currency.orEmpty(),
        amount = amount.safeDouble(),
    )
}

private fun ForecastDto?.toDomain(): Forecast {
    return Forecast(
        forecastPeriodDays = this?.forecastPeriodDays ?: 0,
        method = this?.method.orEmpty(),
        items = this?.items.orEmpty().map { it.toDomain() },
        explanation = this?.explanation.orEmpty(),
    )
}

private fun ForecastItemDto.toDomain(): ForecastItem {
    return ForecastItem(
        currency = currency.orEmpty(),
        predictedAmount = predictedAmount.safeDouble(),
        confidence = confidence.orDefault("medium"),
    )
}

private fun OfferDto.toDomain(): Offer {
    return Offer(
        partnerId = partnerId ?: 0,
        partnerName = partnerName.orDash(),
        shortDescription = shortDescription.orEmpty(),
        logoUrl = logoUrl?.takeIf { it.isNotBlank() },
        brandColorHex = brandColorHex?.takeIf { it.isNotBlank() },
        cashbackPercent = cashbackPercent.safeDouble(),
        financialSegment = financialSegment.orUnknownSegment(),
        reason = reason.orEmpty(),
    )
}

private fun CrossSellRecommendationDto.toDomain(): CrossSellRecommendation {
    return CrossSellRecommendation(
        productCode = productCode.orEmpty(),
        productName = productName.orDash(),
        category = category.orEmpty(),
        priority = priority ?: 99,
        score = (score ?: 0).coerceIn(0, 100),
        title = title.orDash(),
        description = description.orEmpty(),
        reason = reason.orEmpty(),
        evidence = evidence.orEmpty().filter { it.isNotBlank() },
        ctaLabel = ctaLabel.orDefault("Подробнее"),
    )
}

private fun AiInsightDto.toDomain(): AiInsight {
    return AiInsight(
        insightId = insightId.orEmpty(),
        type = type.orEmpty(),
        priority = priority ?: 99,
        title = title.orDash(),
        description = description.orEmpty(),
        reason = reason.orEmpty(),
        evidence = evidence.orEmpty().filter { it.isNotBlank() },
        confidence = confidence.orDefault("medium"),
        ctaLabel = ctaLabel.orDefault("Подробнее"),
        businessGoal = businessGoal?.takeIf { it.isNotBlank() },
        businessImpact = businessImpact?.takeIf { it.isNotBlank() },
    )
}

private fun QuickQuestionDto.toDomain(): QuickQuestion {
    return QuickQuestion(
        question = question.orDash(),
        answer = answer.orEmpty(),
    )
}

private fun LoyaltyLevelDto?.toDomain(): LoyaltyLevel {
    return LoyaltyLevel(
        code = this?.code.orEmpty(),
        name = this?.name.orDefault("Start"),
        currentPoints = this?.currentPoints ?: 0,
        nextLevel = this?.nextLevel?.takeIf { it.isNotBlank() },
        pointsToNextLevel = this?.pointsToNextLevel ?: 0,
        progressPercent = this?.progressPercent.safeDouble().coerceIn(0.0, 100.0),
    )
}

private fun LoyaltyBadgeDto.toDomain(): LoyaltyBadge {
    return LoyaltyBadge(
        code = code.orEmpty(),
        title = title.orDash(),
        description = description.orEmpty(),
    )
}

private fun LoyaltyChallengeDto.toDomain(): LoyaltyChallenge {
    return LoyaltyChallenge(
        challengeId = challengeId.orEmpty(),
        title = title.orDash(),
        description = description.orEmpty(),
        rewardText = rewardText.orEmpty(),
        progressPercent = (progressPercent ?: 0).coerceIn(0, 100),
        difficulty = difficulty.orDefault("medium"),
    )
}

private fun DashboardScoreDto?.toDomain(): DashboardScore {
    return DashboardScore(
        score = (this?.score ?: 0).coerceIn(0, 100),
        status = this?.status.orDefault("starting"),
        title = this?.title.orDefault("Engagement score"),
        description = this?.description.orEmpty(),
        factors = this?.factors.orEmpty().map { it.toDomain() },
        nextBestAction = this?.nextBestAction.toDomain(),
    )
}

private fun DashboardScoreFactorDto.toDomain(): DashboardScoreFactor {
    return DashboardScoreFactor(
        code = code.orEmpty(),
        label = label.orDash(),
        value = (value ?: 0).coerceAtLeast(0),
        maxValue = (maxValue ?: 100).coerceAtLeast(1),
        explanation = explanation.orEmpty(),
    )
}

private fun DashboardNextBestActionDto?.toDomain(): DashboardNextBestAction {
    return DashboardNextBestAction(
        title = this?.title.orDefault("Откройте персональные офферы"),
        description = this?.description.orEmpty(),
        ctaLabel = this?.ctaLabel.orDefault("Подробнее"),
    )
}

fun MissedBenefitDto?.toDomain(): MissedBenefit {
    return MissedBenefit(
        userId = this?.userId ?: 0,
        method = this?.method.orEmpty(),
        upliftFactor = this?.upliftFactor.safeDouble(),
        items = this?.items.orEmpty().map { it.toDomain() },
        topOfferCashbackPercent = this?.topOfferCashbackPercent?.takeIf { it.isFinite() },
        explanation = this?.explanation.orEmpty(),
    )
}

private fun MissedBenefitItemDto.toDomain(): MissedBenefitItem {
    return MissedBenefitItem(
        currency = currency.orEmpty(),
        averageMonthlyAmount = averageMonthlyAmount.safeDouble(),
        potentialExtraAmount = potentialExtraAmount.safeDouble(),
    )
}

private fun String?.orDash(): String = this?.takeIf { it.isNotBlank() } ?: "—"

private fun String?.orDefault(default: String): String = this?.takeIf { it.isNotBlank() } ?: default

private fun String?.orUnknownSegment(): String = this?.takeIf { it.isNotBlank() } ?: "UNKNOWN"

private fun Double?.safeDouble(): Double = if (this != null && this.isFinite()) this else 0.0

fun Double.safePercentInt(): Int = if (isFinite()) roundToInt().coerceIn(0, 100) else 0
