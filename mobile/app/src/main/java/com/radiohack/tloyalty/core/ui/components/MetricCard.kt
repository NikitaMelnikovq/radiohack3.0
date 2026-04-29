package com.radiohack.tloyalty.core.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.radiohack.tloyalty.core.ui.theme.TGray

@Composable
fun MetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    caption: String? = null,
) {
    TBankCard(modifier = modifier) {
        Text(text = title, color = TGray, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
        if (!caption.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = caption, color = TGray, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
