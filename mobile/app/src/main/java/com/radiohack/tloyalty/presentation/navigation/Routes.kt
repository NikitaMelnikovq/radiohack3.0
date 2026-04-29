package com.radiohack.tloyalty.presentation.navigation

object Routes {
    const val DEMO = "demo"
    const val DASHBOARD = "dashboard/{userId}"
    const val ANALYTICS = "analytics/{userId}"
    const val OFFERS = "offers/{userId}"
    const val ASSISTANT = "assistant/{userId}"
    const val GAMIFICATION = "gamification/{userId}"

    fun dashboard(userId: Int) = "dashboard/$userId"
    fun analytics(userId: Int) = "analytics/$userId"
    fun offers(userId: Int) = "offers/$userId"
    fun assistant(userId: Int) = "assistant/$userId"
    fun gamification(userId: Int) = "gamification/$userId"
}
