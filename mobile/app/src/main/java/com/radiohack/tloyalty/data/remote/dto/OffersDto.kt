package com.radiohack.tloyalty.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OffersDto(
    @SerializedName("user_segment") val userSegment: String? = null,
    val offers: List<OfferDto>? = null,
)

data class OfferDto(
    @SerializedName("partner_id") val partnerId: Int? = null,
    @SerializedName("partner_name") val partnerName: String? = null,
    @SerializedName("short_description") val shortDescription: String? = null,
    @SerializedName("logo_url") val logoUrl: String? = null,
    @SerializedName("brand_color_hex") val brandColorHex: String? = null,
    @SerializedName("cashback_percent") val cashbackPercent: Double? = null,
    @SerializedName("financial_segment") val financialSegment: String? = null,
    val reason: String? = null,
)
