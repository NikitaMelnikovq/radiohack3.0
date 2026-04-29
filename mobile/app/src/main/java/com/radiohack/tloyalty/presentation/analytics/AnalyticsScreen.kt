package com.radiohack.tloyalty.presentation.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.radiohack.tloyalty.core.ui.components.EmptyState
import com.radiohack.tloyalty.core.ui.components.ErrorState
import com.radiohack.tloyalty.core.ui.components.LoadingState
import com.radiohack.tloyalty.core.ui.components.MetricCard
import com.radiohack.tloyalty.core.ui.components.TBankCard
import com.radiohack.tloyalty.core.ui.theme.TBlack
import com.radiohack.tloyalty.core.ui.theme.TBravo
import com.radiohack.tloyalty.core.ui.theme.TGray
import com.radiohack.tloyalty.core.ui.theme.TMiles
import com.radiohack.tloyalty.core.ui.theme.TSurface2
import com.radiohack.tloyalty.core.ui.theme.TYellow
import com.radiohack.tloyalty.core.util.CurrencyFormatter
import com.radiohack.tloyalty.core.util.Formatters
import com.radiohack.tloyalty.domain.model.Analytics
import com.radiohack.tloyalty.presentation.common.UiState
import com.radiohack.tloyalty.presentation.common.appContainer
import com.radiohack.tloyalty.presentation.common.viewModelFactory

@Composable
fun AnalyticsScreen(userId: Int) {
    val context = LocalContext.current
    val viewModel: AnalyticsViewModel = viewModel(
        key = "analytics-$userId",
        factory = viewModelFactory {
            AnalyticsViewModel(userId, context.appContainer.loyaltyRepository)
        },
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (val current = state) {
        UiState.Loading -> LoadingState()
        UiState.Empty -> EmptyState(
            title = "Аналитики пока нет",
            description = "У пользователя нет истории начислений для графиков.",
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
        )
        is UiState.Error -> ErrorState(message = current.message, onRetry = viewModel::load)
        is UiState.Success -> AnalyticsContent(analytics = current.data)
    }
}

@Composable
private fun AnalyticsContent(analytics: Analytics) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TBlack),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column {
                Text(
                    text = "Аналитика лояльности",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Динамика выплат, вклад программ и средняя выгода без смешивания валют.",
                    color = TGray,
                )
            }
        }
        item { AverageCashbackBlock(analytics = analytics) }
        item { BestProgramBlock(analytics = analytics) }
        item { MonthlyDynamicsChart(analytics = analytics) }
        item { ProgramBreakdownChart(analytics = analytics) }
    }
}

@Composable
private fun AverageCashbackBlock(analytics: Analytics) {
    if (analytics.averageMonthlyCashback.isEmpty()) {
        EmptyState(title = "Среднее не рассчитано", description = "Недостаточно выплат по месяцам.")
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "Средняя выгода в месяц", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        analytics.averageMonthlyCashback.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { item ->
                    MetricCard(
                        title = item.currency,
                        value = CurrencyFormatter.format(item.currency, item.amount),
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun BestProgramBlock(analytics: Analytics) {
    val best = analytics.bestProgram
    if (best == null) {
        EmptyState(title = "Лучшая программа пока не определена", description = "История начислений пуста.")
    } else {
        MetricCard(
            title = "Лучшая программа",
            value = best.loyaltyProgram,
            caption = CurrencyFormatter.format(best.currency, best.amount),
        )
    }
}

@Composable
private fun MonthlyDynamicsChart(analytics: Analytics) {
    TBankCard {
        Text(text = "Monthly dynamics", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        if (analytics.monthlyDynamics.isEmpty()) {
            Text(text = "Нет данных по месяцам.", color = TGray)
            return@TBankCard
        }
        val maxAmount = analytics.monthlyDynamics.maxOfOrNull { it.amount }?.takeIf { it > 0.0 } ?: 1.0
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            analytics.monthlyDynamics.forEach { item ->
                ChartRow(
                    label = "${Formatters.monthLabel(item.month)} · ${item.currency}",
                    value = CurrencyFormatter.format(item.currency, item.amount),
                    progress = (item.amount / maxAmount).toFloat(),
                    color = currencyColor(item.currency),
                )
            }
        }
    }
}

@Composable
private fun ProgramBreakdownChart(analytics: Analytics) {
    TBankCard {
        Text(text = "Program breakdown", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        if (analytics.programBreakdown.isEmpty()) {
            Text(text = "Нет разбивки по программам.", color = TGray)
            return@TBankCard
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            analytics.programBreakdown.forEach { item ->
                ChartRow(
                    label = item.loyaltyProgram,
                    value = "${Formatters.decimal(item.sharePercent)}% · ${CurrencyFormatter.format(item.currency, item.amount)}",
                    progress = (item.sharePercent / 100.0).toFloat(),
                    color = currencyColor(item.currency),
                )
            }
        }
    }
}

@Composable
private fun ChartRow(
    label: String,
    value: String,
    progress: Float,
    color: Color,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = label, fontWeight = FontWeight.SemiBold)
            Text(text = value, color = TGray)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp),
            color = color,
            trackColor = TSurface2,
        )
    }
}

private fun currencyColor(currency: String): Color {
    return when (currency.lowercase()) {
        "rub" -> TYellow
        "miles" -> TMiles
        "bravo-points" -> TBravo
        else -> TGray
    }
}
