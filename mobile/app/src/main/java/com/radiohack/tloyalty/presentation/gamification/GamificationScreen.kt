package com.radiohack.tloyalty.presentation.gamification

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.radiohack.tloyalty.core.ui.components.EmptyState
import com.radiohack.tloyalty.core.ui.components.ErrorState
import com.radiohack.tloyalty.core.ui.components.LoadingState
import com.radiohack.tloyalty.core.ui.components.ProgressCard
import com.radiohack.tloyalty.core.ui.components.TBankCard
import com.radiohack.tloyalty.core.ui.theme.TBlack
import com.radiohack.tloyalty.core.ui.theme.TGray
import com.radiohack.tloyalty.core.ui.theme.TMuted
import com.radiohack.tloyalty.core.ui.theme.TSurface2
import com.radiohack.tloyalty.core.ui.theme.TYellow
import com.radiohack.tloyalty.core.util.Formatters
import com.radiohack.tloyalty.domain.model.Gamification
import com.radiohack.tloyalty.domain.model.LoyaltyBadge
import com.radiohack.tloyalty.domain.model.LoyaltyChallenge
import com.radiohack.tloyalty.presentation.common.UiState
import com.radiohack.tloyalty.presentation.common.appContainer
import com.radiohack.tloyalty.presentation.common.viewModelFactory

@Composable
fun GamificationScreen(userId: Int) {
    val context = LocalContext.current
    val viewModel: GamificationViewModel = viewModel(
        key = "gamification-$userId",
        factory = viewModelFactory { GamificationViewModel(userId, context.appContainer.loyaltyRepository) },
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (val current = state) {
        UiState.Loading -> LoadingState()
        UiState.Empty -> EmptyState(
            title = "Путь выгоды пока пуст",
            description = "Для игрового прогресса нужны начисления или доступные челленджи.",
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
        )
        is UiState.Error -> ErrorState(message = current.message, onRetry = viewModel::load)
        is UiState.Success -> GamificationContent(data = current.data)
    }
}

@Composable
private fun GamificationContent(data: Gamification) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TBlack),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { LevelCard(data = data) }
        item {
            TBankCard(containerColor = TSurface2) {
                Text(
                    text = "Уровень считается по накопленной выгоде без конвертации валют. Это игровой показатель, не кредитный скоринг.",
                    color = TMuted,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        item { BadgesBlock(badges = data.badges) }
        item { ChallengesBlock(challenges = data.challenges) }
    }
}

@Composable
private fun LevelCard(data: Gamification) {
    val level = data.level
    TBankCard(containerColor = TSurface2) {
        Text(text = "Путь выгоды", color = TYellow, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = level.name,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                )
                Text(text = "${Formatters.integer(level.currentPoints)} баллов", color = TGray)
            }
            Text(
                text = "${Formatters.decimal(level.progressPercent)}%",
                color = TYellow,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        LinearProgressIndicator(
            progress = { (level.progressPercent / 100.0).toFloat().coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp),
            color = TYellow,
            trackColor = TBlack,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Следующий уровень: ${level.nextLevel ?: "максимальный"} · осталось ${Formatters.integer(level.pointsToNextLevel)}",
            color = TGray,
        )
    }
}

@Composable
private fun BadgesBlock(badges: List<LoyaltyBadge>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "Бейджи", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        if (badges.isEmpty()) {
            EmptyState(title = "Бейджей пока нет", description = "Они появятся после первых действий.")
            return
        }
        badges.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { badge ->
                    BadgeCard(badge = badge, modifier = Modifier.weight(1f))
                }
                if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun BadgeCard(
    badge: LoyaltyBadge,
    modifier: Modifier = Modifier,
) {
    TBankCard(modifier = modifier, contentPadding = PaddingValues(16.dp)) {
        Text(text = badge.title, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = badge.description, color = TGray, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun ChallengesBlock(challenges: List<LoyaltyChallenge>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "Челленджи", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        if (challenges.isEmpty()) {
            EmptyState(title = "Челленджей пока нет", description = "Backend не вернул активных задач.")
            return
        }
        challenges.forEach { challenge ->
            ChallengeCard(challenge = challenge)
        }
    }
}

@Composable
private fun ChallengeCard(challenge: LoyaltyChallenge) {
    TBankCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = challenge.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = challenge.description, color = TGray)
            }
            Text(text = challenge.difficulty.uppercase(), color = TYellow, fontWeight = FontWeight.Black)
        }
        Spacer(modifier = Modifier.height(12.dp))
        ProgressCard(
            title = challenge.rewardText.ifBlank { "Прогресс" },
            value = challenge.progressPercent,
            maxValue = 100,
        )
    }
}
