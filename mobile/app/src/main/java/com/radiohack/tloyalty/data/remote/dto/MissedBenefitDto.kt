package com.radiohack.tloyalty.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MissedBenefitDto(
    @SerializedName("user_id") val userId: Int? = null,
    val method: String? = null,
    @SerializedName("uplift_factor") val upliftFactor: Double? = null,
    val items: List<MissedBenefitItemDto>? = null,
    @SerializedName("top_offer_cashback_percent") val topOfferCashbackPercent: Double? = null,
    val explanation: String? = null,
)

data class MissedBenefitItemDto(
    val currency: String? = null,
    @SerializedName("average_monthly_amount") val averageMonthlyAmount: Double? = null,
    @SerializedName("potential_extra_amount") val potentialExtraAmount: Double? = null,
)
