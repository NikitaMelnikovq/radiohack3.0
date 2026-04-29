package com.radiohack.tloyalty.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.radiohack.tloyalty.core.ui.theme.TBlack
import com.radiohack.tloyalty.core.ui.theme.TGray
import com.radiohack.tloyalty.core.ui.theme.TSuccess
import com.radiohack.tloyalty.core.ui.theme.TSurface2
import com.radiohack.tloyalty.core.ui.theme.TWarning
import com.radiohack.tloyalty.core.ui.theme.TYellow

@Composable
fun SegmentBadge(
    segment: String,
    modifier: Modifier = Modifier,
) {
    val normalized = segment.uppercase()
    val colors = when (normalized) {
        "HIGH" -> BadgeColors(background = TYellow, content = TBlack)
        "MEDIUM" -> BadgeColors(background = TWarning.copy(alpha = 0.22f), content = TYellow)
        "LOW" -> BadgeColors(background = TSuccess.copy(alpha = 0.16f), content = TSuccess)
        else -> BadgeColors(background = TGray.copy(alpha = 0.16f), content = TGray)
    }
    Badge(text = normalized, colors = colors, modifier = modifier)
}

@Composable
fun ConfidenceBadge(
    confidence: String,
    modifier: Modifier = Modifier,
) {
    val normalized = confidence.lowercase()
    val colors = when (normalized) {
        "high" -> BadgeColors(background = TSuccess.copy(alpha = 0.16f), content = TSuccess)
        "medium" -> BadgeColors(background = TYellow.copy(alpha = 0.18f), content = TYellow)
        "low" -> BadgeColors(background = TWarning.copy(alpha = 0.14f), content = TWarning)
        else -> BadgeColors(background = TGray.copy(alpha = 0.16f), content = TGray)
    }
    Badge(text = normalized.ifBlank { "medium" }, colors = colors, modifier = modifier)
}

@Composable
fun EvidenceChip(
    text: String,
    modifier: Modifier = Modifier,
) {
    Badge(
        text = text,
        colors = BadgeColors(background = TSurface2, content = TGray),
        modifier = modifier,
    )
}

@Composable
private fun Badge(
    text: String,
    colors: BadgeColors,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(colors.background, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            color = colors.content,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}

private data class BadgeColors(
    val background: Color,
    val content: Color,
)
