package com.radiohack.tloyalty.core.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateFormatter {
    private val outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    fun formatIsoDate(value: String?): String {
        if (value.isNullOrBlank()) return "Нет выплат"
        return runCatching {
            LocalDate.parse(value).format(outputFormatter)
        }.getOrDefault(value)
    }
}
