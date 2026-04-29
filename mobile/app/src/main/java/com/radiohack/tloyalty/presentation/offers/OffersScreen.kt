package com.radiohack.tloyalty.presentation.offers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.radiohack.tloyalty.core.ui.components.EmptyState
import com.radiohack.tloyalty.core.ui.components.ErrorState
import com.radiohack.tloyalty.core.ui.components.LoadingState
import com.radiohack.tloyalty.core.ui.components.SegmentBadge
import com.radiohack.tloyalty.core.ui.components.TBankButton
import com.radiohack.tloyalty.core.ui.components.TBankCard
import com.radiohack.tloyalty.core.ui.theme.TBlack
import com.radiohack.tloyalty.core.ui.theme.TGray
import com.radiohack.tloyalty.core.ui.theme.TMuted
import com.radiohack.tloyalty.core.ui.theme.TSurface2
import com.radiohack.tloyalty.core.ui.theme.TYellow
import com.radiohack.tloyalty.core.util.CurrencyFormatter
import com.radiohack.tloyalty.core.util.Formatters
import com.radiohack.tloyalty.domain.model.Offer
import com.radiohack.tloyalty.domain.model.OffersBundle
import com.radiohack.tloyalty.presentation.common.UiState
import com.radiohack.tloyalty.presentation.common.appContainer
import com.radiohack.tloyalty.presentation.common.viewModelFactory
import kotlinx.coroutines.launch

@Composable
fun OffersScreen(userId: Int) {
    val context = LocalContext.current
    val viewModel: OffersViewModel = viewModel(
        key = "offers-$userId",
        factory = viewModelFactory { OffersViewModel(userId, context.appContainer.loyaltyRepository) },
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (val current = state) {
        UiState.Loading -> LoadingState()
        UiState.Empty -> EmptyState(
            title = "Для этого сегмента пока нет офферов",
            description = "Backend вернул пустой список персональных предложений.",
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
        )
        is UiState.Error -> ErrorState(message = current.message, onRetry = viewModel::load)
        is UiState.Success -> OffersContent(bundle = current.data)
    }
}

@Composable
private fun OffersContent(bundle: OffersBundle) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(TBlack),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Персональные офферы",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                        )
                        SegmentBadge(segment = bundle.userSegment)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Подборка фильтруется по financial_segment и объясняет причину показа.",
                        color = TGray,
                    )
                }
            }
            items(bundle.offers, key = { it.partnerId }) { offer ->
                OfferCard(
                    offer = offer,
                    onActivate = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Демо: оффер готов к активации")
                        }
                    },
                )
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
        )
    }
}

@Composable
private fun OfferCard(
    offer: Offer,
    onActivate: () -> Unit,
) {
    TBankCard {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(Formatters.parseBrandColor(offer.brandColorHex)),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            PartnerLogo(offer = offer)
            Column(modifier = Modifier.weight(1f)) {
                Text(text = offer.partnerName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = offer.shortDescription, color = TGray, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Text(
                text = CurrencyFormatter.formatPercent(offer.cashbackPercent),
                color = TYellow,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(text = offer.reason, color = TMuted)
        Spacer(modifier = Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            SegmentBadge(segment = offer.financialSegment)
            Spacer(modifier = Modifier.weight(1f))
            TBankButton(text = "Активировать", onClick = onActivate)
        }
    }
}

@Composable
private fun PartnerLogo(offer: Offer) {
    val fallback: @Composable () -> Unit = {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Formatters.parseBrandColor(offer.brandColorHex).copy(alpha = 0.22f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = offer.partnerName.firstOrNull()?.uppercase() ?: "P",
                color = TYellow,
                fontWeight = FontWeight.Black,
            )
        }
    }
    if (offer.logoUrl.isNullOrBlank()) {
        fallback()
    } else {
        SubcomposeAsyncImage(
            model = offer.logoUrl,
            contentDescription = offer.partnerName,
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(TSurface2),
            contentScale = ContentScale.Crop,
            loading = { fallback() },
            error = { fallback() },
            success = { SubcomposeAsyncImageContent() },
        )
    }
}
