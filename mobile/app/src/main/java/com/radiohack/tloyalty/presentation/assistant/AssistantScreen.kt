package com.radiohack.tloyalty.presentation.assistant

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.radiohack.tloyalty.core.ui.components.ConfidenceBadge
import com.radiohack.tloyalty.core.ui.components.EmptyState
import com.radiohack.tloyalty.core.ui.components.ErrorState
import com.radiohack.tloyalty.core.ui.components.EvidenceChip
import com.radiohack.tloyalty.core.ui.components.LoadingState
import com.radiohack.tloyalty.core.ui.components.TBankCard
import com.radiohack.tloyalty.core.ui.theme.TBlack
import com.radiohack.tloyalty.core.ui.theme.TGray
import com.radiohack.tloyalty.core.ui.theme.TMuted
import com.radiohack.tloyalty.core.ui.theme.TSurface2
import com.radiohack.tloyalty.core.ui.theme.TYellow
import com.radiohack.tloyalty.domain.model.AiInsight
import com.radiohack.tloyalty.domain.model.AiInsights
import com.radiohack.tloyalty.domain.model.QuickQuestion
import com.radiohack.tloyalty.presentation.common.UiState
import com.radiohack.tloyalty.presentation.common.appContainer
import com.radiohack.tloyalty.presentation.common.viewModelFactory

@Composable
fun AssistantScreen(userId: Int) {
    val context = LocalContext.current
    val viewModel: AssistantViewModel = viewModel(
        key = "assistant-$userId",
        factory = viewModelFactory { AssistantViewModel(userId, context.appContainer.loyaltyRepository) },
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (val current = state) {
        UiState.Loading -> LoadingState()
        UiState.Empty -> EmptyState(
            title = "Ассистент пока не нашёл инсайтов",
            description = "После появления выплат и офферов рекомендации появятся здесь.",
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
        )
        is UiState.Error -> ErrorState(message = current.message, onRetry = viewModel::load)
        is UiState.Success -> AssistantContent(data = current.data)
    }
}

@Composable
private fun AssistantContent(data: AiInsights) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TBlack),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { AssistantHeader(data = data) }
        items(data.insights, key = { it.insightId.ifBlank { it.title } }) { insight ->
            InsightCard(insight = insight)
        }
        item { QuickQuestionsBlock(questions = data.quickQuestions) }
    }
}

@Composable
private fun AssistantHeader(data: AiInsights) {
    TBankCard(containerColor = TSurface2) {
        Text(
            text = data.title.ifBlank { "AI-ассистент выгоды" },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
        )
        Spacer(modifier = Modifier.height(8.dp))
        EvidenceChip(text = data.method.ifBlank { "rule_based_ai_insights" })
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = data.summary.ifBlank { "Ассистент нашёл закономерности на основе истории выплат и программ лояльности." },
            color = TGray,
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Рекомендации объяснимы и рассчитаны по правилам: они повышают прозрачность лояльности, объясняют офферы, ведут к cross-sell и уменьшают missed benefit.",
            color = TMuted,
        )
    }
}

@Composable
private fun InsightCard(insight: AiInsight) {
    TBankCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = insight.type.ifBlank { "insight" }.uppercase(),
                    color = TYellow,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = insight.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            ConfidenceBadge(confidence = insight.confidence)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = insight.description, color = TGray)
        if (insight.reason.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Почему: ${insight.reason}", color = TMuted)
        }
        if (insight.evidence.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                insight.evidence.forEach { EvidenceChip(text = it) }
            }
        }
        if (!insight.businessGoal.isNullOrBlank() || !insight.businessImpact.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            InnerPanel {
                insight.businessGoal?.let {
                    Text(text = "Цель: $it", color = TMuted)
                }
                insight.businessImpact?.let {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Вклад: $it", color = TMuted)
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = insight.ctaLabel, color = TYellow, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun InnerPanel(
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(TBlack, RoundedCornerShape(18.dp))
            .border(1.dp, TSurface2, RoundedCornerShape(18.dp))
            .padding(14.dp),
        content = content,
    )
}

@Composable
private fun QuickQuestionsBlock(questions: List<QuickQuestion>) {
    if (questions.isEmpty()) return
    var expandedQuestion by remember { mutableStateOf<String?>(null) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "Быстрые вопросы", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        questions.forEach { item ->
            val expanded = expandedQuestion == item.question
            TBankCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        expandedQuestion = if (expanded) null else item.question
                    },
                containerColor = if (expanded) TSurface2 else MaterialTheme.colorScheme.surface,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = item.question, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    Text(text = if (expanded) "Скрыть" else "Открыть", color = TYellow)
                }
                if (expanded) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = item.answer, color = TGray)
                }
            }
        }
    }
}
