package com.radiohack.tloyalty.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.radiohack.tloyalty.core.ui.components.ConfidenceBadge
import com.radiohack.tloyalty.core.ui.components.EmptyState
import com.radiohack.tloyalty.core.ui.components.ErrorState
import com.radiohack.tloyalty.core.ui.components.EvidenceChip
import com.radiohack.tloyalty.core.ui.components.LoadingState
import com.radiohack.tloyalty.core.ui.components.MetricCard
import com.radiohack.tloyalty.core.ui.components.ProgressCard
import com.radiohack.tloyalty.core.ui.components.SegmentBadge
import com.radiohack.tloyalty.core.ui.components.TBankButton
import com.radiohack.tloyalty.core.ui.components.TBankCard
import com.radiohack.tloyalty.core.ui.components.TBankSecondaryButton
import com.radiohack.tloyalty.core.ui.theme.TBlack
import com.radiohack.tloyalty.core.ui.theme.TBorder
import com.radiohack.tloyalty.core.ui.theme.TGray
import com.radiohack.tloyalty.core.ui.theme.TMuted
import com.radiohack.tloyalty.core.ui.theme.TSurface2
import com.radiohack.tloyalty.core.ui.theme.TYellow
import com.radiohack.tloyalty.core.util.CurrencyFormatter
import com.radiohack.tloyalty.core.util.DateFormatter
import com.radiohack.tloyalty.core.util.Formatters
import com.radiohack.tloyalty.domain.model.AiInsight
import com.radiohack.tloyalty.domain.model.CrossSellRecommendation
import com.radiohack.tloyalty.domain.model.Dashboard
import com.radiohack.tloyalty.domain.model.ForecastItem
import com.radiohack.tloyalty.domain.model.Offer
import com.radiohack.tloyalty.presentation.common.UiState
import com.radiohack.tloyalty.presentation.common.appContainer
import com.radiohack.tloyalty.presentation.common.viewModelFactory
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    userId: Int,
    onOpenAnalytics: (Int) -> Unit,
    onOpenOffers: (Int) -> Unit,
    onOpenAssistant: (Int) -> Unit,
    onOpenGamification: (Int) -> Unit,
) {
    val context = LocalContext.current
    val viewModel: DashboardViewModel = viewModel(
        key = "dashboard-$userId",
        factory = viewModelFactory {
            DashboardViewModel(
                userId = userId,
                repository = context.appContainer.loyaltyRepository,
            )
        },
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (val current = state) {
        UiState.Loading -> LoadingState()
        UiState.Empty -> EmptyState(
            title = "Dashboard пуст",
            description = "Для выбранного пользователя пока нет данных выгоды.",
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
        )
        is UiState.Error -> ErrorState(message = current.message, onRetry = viewModel::load)
        is UiState.Success -> DashboardContent(
            dashboard = current.data,
            userId = userId,
            onOpenAnalytics = onOpenAnalytics,
            onOpenOffers = onOpenOffers,
            onOpenAssistant = onOpenAssistant,
            onOpenGamification = onOpenGamification,
        )
    }
}

@Composable
private fun DashboardContent(
    dashboard: Dashboard,
    userId: Int,
    onOpenAnalytics: (Int) -> Unit,
    onOpenOffers: (Int) -> Unit,
    onOpenAssistant: (Int) -> Unit,
    onOpenGamification: (Int) -> Unit,
) {
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
            item { DashboardHero(dashboard = dashboard) }
            item { UserSummaryCard(dashboard = dashboard) }
            item { DashboardScoreCard(dashboard = dashboard) }
            item {
                ForecastPreview(
                    items = dashboard.forecast.items,
                    period = dashboard.forecast.forecastPeriodDays,
                    method = dashboard.forecast.method,
                    explanation = dashboard.forecast.explanation,
                    onOpenAnalytics = { onOpenAnalytics(userId) },
                )
            }
            item {
                OffersPreview(
                    offers = dashboard.offers.offers.take(3),
                    onOpenOffers = { onOpenOffers(userId) },
                    onActivate = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Демо: оффер готов к активации")
                        }
                    },
                )
            }
            item {
                CrossSellPreview(
                    recommendations = dashboard.crossSell.recommendations.take(3),
                    onOpenDetails = { onOpenOffers(userId) },
                )
            }
            item {
                AiInsightsPreview(
                    insights = dashboard.aiInsights.insights.take(3),
                    onOpenAssistant = { onOpenAssistant(userId) },
                )
            }
            item {
                GamificationPreview(
                    dashboard = dashboard,
                    onOpenGamification = { onOpenGamification(userId) },
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
private fun DashboardHero(dashboard: Dashboard) {
    TBankCard(containerColor = TSurface2) {
        Text(
            text = "Моя выгода от Т-Банка",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Кэшбэк, мили, Браво, офферы и прогноз — в одном разделе.",
            color = TGray,
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(18.dp))
        Text(text = "Т-Банк уже вернул вам", color = TMuted)
        Spacer(modifier = Modifier.height(8.dp))
        val totals = dashboard.loyaltySummary.totalsByCurrency
        if (totals.isEmpty()) {
            Text(text = "Пока нет начислений. Когда backend вернёт выплаты, суммы появятся здесь.", color = TGray)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                totals.forEach { item ->
                    Text(
                        text = CurrencyFormatter.format(item.currency, item.amount),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        color = if (item.currency == "rub") TYellow else MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

@Composable
private fun UserSummaryCard(dashboard: Dashboard) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader(title = "Профиль клиента")
        TBankCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = dashboard.user.fullName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SegmentBadge(segment = dashboard.user.financialSegment)
                }
                Text(
                    text = "#${dashboard.user.id}",
                    color = TGray,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(
                title = "Счета",
                value = dashboard.user.accountsCount.toString(),
                modifier = Modifier.weight(1f),
            )
            MetricCard(
                title = "Операции",
                value = Formatters.integer(dashboard.loyaltySummary.totalTransactions),
                modifier = Modifier.weight(1f),
            )
        }
        MetricCard(
            title = "Последняя выплата",
            value = DateFormatter.formatIsoDate(dashboard.loyaltySummary.lastPayoutDate),
            caption = "Дата последнего loyalty payout",
        )
    }
}

@Composable
private fun DashboardScoreCard(dashboard: Dashboard) {
    val score = dashboard.dashboardScore
    TBankCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Engagement score",
                    color = TYellow,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = score.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = score.description, color = TGray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = score.score.toString(),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                    color = TYellow,
                )
                Text(text = score.status, color = TGray)
            }
        }
        if (score.factors.isNotEmpty()) {
            Spacer(modifier = Modifier.height(18.dp))
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                score.factors.forEach { factor ->
                    ProgressCard(
                        title = factor.label,
                        value = factor.value,
                        maxValue = factor.maxValue,
                        description = factor.explanation,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        InnerPanel {
            Text(
                text = score.nextBestAction.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = score.nextBestAction.description, color = TGray)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = score.nextBestAction.ctaLabel, color = TYellow, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ForecastPreview(
    items: List<ForecastItem>,
    period: Int,
    method: String,
    explanation: String,
    onOpenAnalytics: () -> Unit,
) {
    TBankCard {
        SectionHeader(title = "Прогноз выгоды", action = "Аналитика", onAction = onOpenAnalytics)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "$period дней · $method", color = TGray)
        Spacer(modifier = Modifier.height(12.dp))
        if (items.isEmpty()) {
            Text(text = "Недостаточно истории для прогноза.", color = TGray)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items.forEach { item ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = CurrencyFormatter.format(item.currency, item.predictedAmount),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                            )
                            Text(text = item.currency, color = TGray)
                        }
                        ConfidenceBadge(confidence = item.confidence)
                    }
                }
            }
        }
        if (explanation.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = explanation, color = TGray)
        }
    }
}

@Composable
private fun OffersPreview(
    offers: List<Offer>,
    onOpenOffers: () -> Unit,
    onActivate: () -> Unit,
) {
    TBankCard {
        SectionHeader(title = "Персональные офферы", action = "Все", onAction = onOpenOffers)
        Spacer(modifier = Modifier.height(12.dp))
        if (offers.isEmpty()) {
            Text(text = "Для этого сегмента пока нет офферов.", color = TGray)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                offers.forEach { offer ->
                    OfferCompactCard(offer = offer, onActivate = onActivate)
                }
            }
        }
    }
}

@Composable
private fun OfferCompactCard(
    offer: Offer,
    onActivate: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TBlack, MaterialTheme.shapes.large)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        PartnerLogo(offer = offer)
        Column(modifier = Modifier.weight(1f)) {
            Text(text = offer.partnerName, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = offer.shortDescription, color = TGray, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = offer.reason, color = TMuted, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = CurrencyFormatter.formatPercent(offer.cashbackPercent),
                color = TYellow,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
            )
            TBankSecondaryButton(text = "Активировать", onClick = onActivate)
        }
    }
}

@Composable
private fun PartnerLogo(offer: Offer) {
    val fallback: @Composable () -> Unit = {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Formatters.parseBrandColor(offer.brandColorHex).copy(alpha = 0.24f)),
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
                .size(46.dp)
                .clip(CircleShape)
                .background(TSurface2),
            contentScale = ContentScale.Crop,
            loading = { fallback() },
            error = { fallback() },
            success = { SubcomposeAsyncImageContent() },
        )
    }
}

@Composable
private fun CrossSellPreview(
    recommendations: List<CrossSellRecommendation>,
    onOpenDetails: () -> Unit,
) {
    TBankCard {
        SectionHeader(title = "Следующий продукт", action = "Подробнее", onAction = onOpenDetails)
        Spacer(modifier = Modifier.height(12.dp))
        if (recommendations.isEmpty()) {
            Text(text = "Рекомендаций пока нет.", color = TGray)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                recommendations.forEach { recommendation ->
                    InnerPanel {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = recommendation.title, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = recommendation.description, color = TGray)
                            }
                            Text(
                                text = recommendation.score.toString(),
                                color = TYellow,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = recommendation.reason, color = TMuted, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun AiInsightsPreview(
    insights: List<AiInsight>,
    onOpenAssistant: () -> Unit,
) {
    TBankCard {
        SectionHeader(title = "AI insights", action = "Ассистент", onAction = onOpenAssistant)
        Spacer(modifier = Modifier.height(12.dp))
        if (insights.isEmpty()) {
            Text(text = "Ассистент пока не нашёл закономерностей.", color = TGray)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                insights.forEach { insight ->
                    InnerPanel {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = insight.title,
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.Bold,
                            )
                            ConfidenceBadge(confidence = insight.confidence)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = insight.description, color = TGray)
                        if (insight.evidence.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                insight.evidence.take(2).forEach { EvidenceChip(text = it) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GamificationPreview(
    dashboard: Dashboard,
    onOpenGamification: () -> Unit,
) {
    val level = dashboard.gamification.level
    TBankCard {
        SectionHeader(title = "Путь выгоды", action = "Открыть", onAction = onOpenGamification)
        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = level.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                Text(
                    text = "${Formatters.integer(level.currentPoints)} баллов · до ${level.nextLevel ?: "следующего уровня"} ${Formatters.integer(level.pointsToNextLevel)}",
                    color = TGray,
                )
            }
            Text(text = "${Formatters.decimal(level.progressPercent)}%", color = TYellow, fontWeight = FontWeight.Black)
        }
        Spacer(modifier = Modifier.height(12.dp))
        LinearProgressIndicator(
            progress = { (level.progressPercent / 100.0).toFloat().coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(9.dp),
            color = TYellow,
            trackColor = TSurface2,
        )
        if (dashboard.gamification.badges.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                dashboard.gamification.badges.take(3).forEach { badge ->
                    EvidenceChip(text = badge.title)
                }
            }
        }
        if (dashboard.gamification.challenges.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                dashboard.gamification.challenges.take(2).forEach { challenge ->
                    Text(text = challenge.title, color = TMuted, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    action: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        if (action != null && onAction != null) {
            TBankSecondaryButton(text = action, onClick = onAction)
        }
    }
}

@Composable
private fun InnerPanel(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TBlack, RoundedCornerShape(18.dp))
            .border(1.dp, TBorder, RoundedCornerShape(18.dp))
            .padding(16.dp),
        content = content,
    )
}
