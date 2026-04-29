package com.radiohack.tloyalty.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AiInsightsDto(
    @SerializedName("user_id") val userId: Int? = null,
    val method: String? = null,
    val title: String? = null,
    val summary: String? = null,
    val insights: List<AiInsightDto>? = null,
    @SerializedName("quick_questions") val quickQuestions: List<QuickQuestionDto>? = null,
)

data class AiInsightDto(
    @SerializedName("insight_id") val insightId: String? = null,
    val type: String? = null,
    val priority: Int? = null,
    val title: String? = null,
    val description: String? = null,
    val reason: String? = null,
    val evidence: List<String>? = null,
    val confidence: String? = null,
    @SerializedName("cta_label") val ctaLabel: String? = null,
    @SerializedName("business_goal") val businessGoal: String? = null,
    @SerializedName("business_impact") val businessImpact: String? = null,
)

data class QuickQuestionDto(
    val question: String? = null,
    val answer: String? = null,
)
