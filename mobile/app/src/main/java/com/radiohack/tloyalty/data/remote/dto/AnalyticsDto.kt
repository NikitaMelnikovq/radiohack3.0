package com.radiohack.tloyalty.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoyaltySummaryDto(
    val user: LoyaltySummaryUserDto? = null,
    val accounts: List<LoyaltyAccountSummaryDto>? = null,
    @SerializedName("totals_by_currency") val totalsByCurrency: List<CurrencyAmountDto>? = null,
    @SerializedName("total_transactions") val totalTransactions: Int? = null,
    @SerializedName("last_payout_date") val lastPayoutDate: String? = null,
)

data class LoyaltySummaryUserDto(
    val id: Int? = null,
    @SerializedName("full_name") val fullName: String? = null,
    @SerializedName("financial_segment") val financialSegment: String? = null,
)

data class LoyaltyAccountSummaryDto(
    @SerializedName("account_id") val accountId: Int? = null,
    @SerializedName("loyalty_program") val loyaltyProgram: String? = null,
    @SerializedName("cashback_currency") val cashbackCurrency: String? = null,
    @SerializedName("current_balance") val currentBalance: Double? = null,
    @SerializedName("total_cashback") val totalCashback: Double? = null,
    @SerializedName("transactions_count") val transactionsCount: Int? = null,
)

data class AnalyticsDto(
    @SerializedName("monthly_dynamics") val monthlyDynamics: List<MonthlyDynamicDto>? = null,
    @SerializedName("program_breakdown") val programBreakdown: List<ProgramBreakdownDto>? = null,
    @SerializedName("best_program") val bestProgram: BestProgramDto? = null,
    @SerializedName("average_monthly_cashback") val averageMonthlyCashback: List<CurrencyAmountDto>? = null,
)

data class MonthlyDynamicDto(
    val month: String? = null,
    val currency: String? = null,
    val amount: Double? = null,
)

data class ProgramBreakdownDto(
    @SerializedName("loyalty_program") val loyaltyProgram: String? = null,
    val currency: String? = null,
    val amount: Double? = null,
    @SerializedName("share_percent") val sharePercent: Double? = null,
)

data class BestProgramDto(
    @SerializedName("loyalty_program") val loyaltyProgram: String? = null,
    val currency: String? = null,
    val amount: Double? = null,
)

data class ForecastDto(
    @SerializedName("forecast_period_days") val forecastPeriodDays: Int? = null,
    val method: String? = null,
    val items: List<ForecastItemDto>? = null,
    val explanation: String? = null,
)

data class ForecastItemDto(
    val currency: String? = null,
    @SerializedName("predicted_amount") val predictedAmount: Double? = null,
    val confidence: String? = null,
)
