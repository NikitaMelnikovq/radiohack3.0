package com.radiohack.tloyalty.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.radiohack.tloyalty.core.ui.theme.TBlack
import com.radiohack.tloyalty.core.ui.theme.TBorder
import com.radiohack.tloyalty.core.ui.theme.TWhite
import com.radiohack.tloyalty.core.ui.theme.TYellow

@Composable
fun TBankButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        modifier = modifier.heightIn(min = 48.dp),
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = TYellow,
            contentColor = TBlack,
            disabledContainerColor = TYellow.copy(alpha = 0.42f),
            disabledContentColor = TBlack.copy(alpha = 0.62f),
        ),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
    ) {
        Text(text = text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TBankSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        modifier = modifier.heightIn(min = 44.dp),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, TBorder),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TWhite, containerColor = Color.Transparent),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Text(text = text, fontWeight = FontWeight.SemiBold)
    }
}
