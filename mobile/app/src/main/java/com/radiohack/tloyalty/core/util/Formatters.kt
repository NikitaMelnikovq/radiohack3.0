package com.radiohack.tloyalty.core.util

import androidx.compose.ui.graphics.Color
import com.radiohack.tloyalty.core.ui.theme.TYellow
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object Formatters {
    private val numberFormatter = DecimalFormat("#,###", DecimalFormatSymbols(Locale.forLanguageTag("ru-RU")))

    fun integer(value: Int): String = numberFormatter.format(value).replace('\u00A0', ' ')

    fun decimal(value: Double): String {
        val safe = value.takeIf { it.isFinite() } ?: 0.0
        return if (safe % 1.0 == 0.0) safe.toInt().toString() else "%.1f".format(Locale.US, safe)
    }

    fun monthLabel(value: String): String = value.replace("-", ".")

    fun parseBrandColor(hex: String?): Color {
        if (hex.isNullOrBlank()) return TYellow
        return runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrDefault(TYellow)
    }
}
