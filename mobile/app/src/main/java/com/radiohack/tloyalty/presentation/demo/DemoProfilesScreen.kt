package com.radiohack.tloyalty.presentation.demo

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.radiohack.tloyalty.core.ui.components.EmptyState
import com.radiohack.tloyalty.core.ui.components.ErrorState
import com.radiohack.tloyalty.core.ui.components.EvidenceChip
import com.radiohack.tloyalty.core.ui.components.LoadingState
import com.radiohack.tloyalty.core.ui.components.SegmentBadge
import com.radiohack.tloyalty.core.ui.components.TBankButton
import com.radiohack.tloyalty.core.ui.components.TBankCard
import com.radiohack.tloyalty.core.ui.components.TBankSecondaryButton
import com.radiohack.tloyalty.core.ui.theme.TBlack
import com.radiohack.tloyalty.core.ui.theme.TGray
import com.radiohack.tloyalty.core.ui.theme.TYellow
import com.radiohack.tloyalty.domain.model.DemoProfile
import com.radiohack.tloyalty.presentation.common.UiState
import com.radiohack.tloyalty.presentation.common.appContainer
import com.radiohack.tloyalty.presentation.common.viewModelFactory

@Composable
fun DemoProfilesScreen(
    onOpenDashboard: (Int) -> Unit,
) {
    val context = LocalContext.current
    val viewModel: DemoProfilesViewModel = viewModel(
        factory = viewModelFactory {
            DemoProfilesViewModel(
                repository = context.appContainer.loyaltyRepository,
                userPreferences = context.appContainer.userPreferences,
            )
        },
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (val current = state) {
        UiState.Loading -> LoadingState()
        UiState.Empty -> EmptyState(
            title = "Нет демо-профилей",
            description = "Backend вернул пустой список. Проверьте CSV и endpoint /api/demo/profiles.",
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
        )
        is UiState.Error -> ErrorState(message = current.message, onRetry = viewModel::load)
        is UiState.Success -> DemoProfilesContent(
            data = current.data,
            onContinue = { userId -> onOpenDashboard(userId) },
            onSelect = { profile ->
                viewModel.selectProfile(profile)
                onOpenDashboard(profile.userId)
            },
        )
    }
}

@Composable
private fun DemoProfilesContent(
    data: DemoProfilesData,
    onContinue: (Int) -> Unit,
    onSelect: (DemoProfile) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TBlack),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column(modifier = Modifier.padding(top = 18.dp, bottom = 8.dp)) {
                Text(
                    text = "T-Loyalty Hub",
                    color = TYellow,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Моя выгода",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Выберите демо-пользователя, чтобы показать жюри разные сценарии лояльности, офферов и рекомендаций.",
                    color = TGray,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }

        data.selectedUser.userId?.let { userId ->
            item {
                TBankCard {
                    Text(
                        text = "Последний выбранный профиль",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = data.selectedUser.label ?: "Пользователь #$userId", color = TGray)
                        Spacer(modifier = Modifier.weight(1f))
                        data.selectedUser.segment?.let { SegmentBadge(segment = it) }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    TBankSecondaryButton(
                        text = "Продолжить с этим пользователем",
                        onClick = { onContinue(userId) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        items(data.profiles, key = { it.userId }) { profile ->
            DemoProfileCard(profile = profile, onClick = { onSelect(profile) })
        }
    }
}

@Composable
private fun DemoProfileCard(
    profile: DemoProfile,
    onClick: () -> Unit,
) {
    TBankCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = profile.label,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = profile.description, color = TGray)
            }
            SegmentBadge(segment = profile.financialSegment)
        }
        if (profile.highlightMetrics.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                profile.highlightMetrics.take(5).forEach { metric ->
                    EvidenceChip(text = metric)
                }
            }
        }
        if (profile.recommendedDemoFlow.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Сценарий показа",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                profile.recommendedDemoFlow.forEachIndexed { index, step ->
                    Text(text = "${index + 1}. $step", color = TGray)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        TBankButton(
            text = "Открыть Мою выгоду",
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
