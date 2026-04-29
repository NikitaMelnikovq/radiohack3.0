package com.radiohack.tloyalty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.radiohack.tloyalty.core.ui.theme.TLoyaltyTheme
import com.radiohack.tloyalty.presentation.navigation.AppNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TLoyaltyTheme {
                AppNavGraph()
            }
        }
    }
}
