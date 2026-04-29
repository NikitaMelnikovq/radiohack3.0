package com.radiohack.tloyalty.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CrossSellDto(
    @SerializedName("user_id") val userId: Int? = null,
    @SerializedName("financial_segment") val financialSegment: String? = null,
    val recommendations: List<CrossSellRecommendationDto>? = null,
)

data class CrossSellRecommendationDto(
    @SerializedName("product_code") val productCode: String? = null,
    @SerializedName("product_name") val productName: String? = null,
    val category: String? = null,
    val priority: Int? = null,
    val score: Int? = null,
    val title: String? = null,
    val description: String? = null,
    val reason: String? = null,
    val evidence: List<String>? = null,
    @SerializedName("cta_label") val ctaLabel: String? = null,
)
