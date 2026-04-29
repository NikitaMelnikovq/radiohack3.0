package com.radiohack.tloyalty.core.config

object ApiConfig {
    const val DEFAULT_EMULATOR_BASE_URL = "http://10.0.2.2:8000"

    var baseUrl: String = DEFAULT_EMULATOR_BASE_URL

    fun normalizedBaseUrl(): String {
        return if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
    }
}
