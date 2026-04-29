package com.radiohack.tloyalty.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DemoProfilesDto(
    val profiles: List<DemoProfileDto>? = null,
)

data class DemoProfileDto(
    @SerializedName("user_id") val userId: Int? = null,
    val label: String? = null,
    val description: String? = null,
    @SerializedName("financial_segment") val financialSegment: String? = null,
    @SerializedName("highlight_metrics") val highlightMetrics: List<String>? = null,
    @SerializedName("recommended_demo_flow") val recommendedDemoFlow: List<String>? = null,
)
