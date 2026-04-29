package com.radiohack.tloyalty.core.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.roundToLong

object CurrencyFormatter {
    private val formatter = DecimalFormat("#,###", DecimalFormatSymbols(Locale.forLanguageTag("ru-RU")))

    fun format(currency: String, amount: Double): String {
        val safeAmount = amount.takeIf { it.isFinite() } ?: 0.0
        val value = formatter.format(safeAmount.roundToLong()).replace('\u00A0', ' ')
        return when (currency.lowercase()) {
            "rub" -> "$value ₽"
            "miles" -> "$value миль"
            "bravo-points" -> "$value Браво"
            else -> "$value ${currency.ifBlank { "баллов" }}"
        }
    }

    fun formatPercent(value: Double): String {
        val safeValue = value.takeIf { it.isFinite() } ?: 0.0
        return if (safeValue % 1.0 == 0.0) {
            "${safeValue.toInt()}%"
        } else {
            "${"%.1f".format(Locale.US, safeValue)}%"
        }
    }
}
