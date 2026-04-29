package com.radiohack.tloyalty.data.remote.dto

import com.google.gson.annotations.SerializedName

data class HealthDto(
    val status: String? = null,
    val service: String? = null,
)

data class UserPreviewDto(
    val id: Int? = null,
    @SerializedName("full_name") val fullName: String? = null,
    val email: String? = null,
    @SerializedName("phone_number") val phoneNumber: String? = null,
    @SerializedName("financial_segment") val financialSegment: String? = null,
    @SerializedName("accounts_count") val accountsCount: Int? = null,
    @SerializedName("total_cashback_value") val totalCashbackValue: Double? = null,
)

data class CurrencyAmountDto(
    val currency: String? = null,
    val amount: Double? = null,
)
