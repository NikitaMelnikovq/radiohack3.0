package com.radiohack.tloyalty.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GamificationDto(
    @SerializedName("user_id") val userId: Int? = null,
    val level: LoyaltyLevelDto? = null,
    val badges: List<LoyaltyBadgeDto>? = null,
    val challenges: List<LoyaltyChallengeDto>? = null,
)

data class LoyaltyLevelDto(
    val code: String? = null,
    val name: String? = null,
    @SerializedName("current_points") val currentPoints: Int? = null,
    @SerializedName("next_level") val nextLevel: String? = null,
    @SerializedName("points_to_next_level") val pointsToNextLevel: Int? = null,
    @SerializedName("progress_percent") val progressPercent: Double? = null,
)

data class LoyaltyBadgeDto(
    val code: String? = null,
    val title: String? = null,
    val description: String? = null,
)

data class LoyaltyChallengeDto(
    @SerializedName("challenge_id") val challengeId: String? = null,
    val title: String? = null,
    val description: String? = null,
    @SerializedName("reward_text") val rewardText: String? = null,
    @SerializedName("progress_percent") val progressPercent: Int? = null,
    val difficulty: String? = null,
)
