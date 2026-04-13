package com.jionifamily.presentation.shared.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jionifamily.domain.model.WeeklyStats
import com.jionifamily.presentation.common.components.MissionCard
import com.jionifamily.presentation.theme.CreamWhite
import com.jionifamily.presentation.theme.PastelMint
import com.jionifamily.presentation.theme.PastelPink
import com.jionifamily.presentation.theme.PastelYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = CreamWhite,
        topBar = {
            TopAppBar(
                title = { Text("지난 미션") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CreamWhite),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // Week selector
            if (state.weeks.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 8.dp),
                ) {
                    items(state.weeks) { week ->
                        WeekChip(
                            week = week,
                            isSelected = week.weekStart == state.selectedWeek,
                            onClick = { viewModel.selectWeek(week.weekStart) },
                        )
                    }
                }

                // Selected week stats
                val selectedStats = state.weeks.find { it.weekStart == state.selectedWeek }
                if (selectedStats != null) {
                    WeekSummaryCard(selectedStats)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Completions for selected week
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (state.weekCompletions.isEmpty() && !state.isLoadingCompletions) {
                        item {
                            Text(
                                text = "이 주에는 미션이 없어요",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(vertical = 24.dp),
                            )
                        }
                    }

                    items(state.weekCompletions) { completion ->
                        MissionCard(
                            missionName = completion.missionName ?: "",
                            category = completion.missionCategory,
                            rewardCoins = completion.missionRewardCoins,
                            status = completion.status,
                        )
                    }
                }
            } else if (!state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "아직 지난 미션 기록이 없어요",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekChip(
    week: WeeklyStats,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val formattedDate = formatWeekLabel(week.weekStart)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) PastelPink else PastelPink.copy(alpha = 0.15f))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            )
            Text(
                text = "${week.approvedMissions}/${week.totalMissions}",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 11.sp,
            )
        }
    }
}

@Composable
private fun WeekSummaryCard(stats: WeeklyStats) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PastelMint.copy(alpha = 0.2f)),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("주간 요약", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("\uD83E\uDE99", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${stats.coinsEarned} 코인 획득",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            val progress = if (stats.totalMissions > 0) {
                stats.approvedMissions.toFloat() / stats.totalMissions
            } else 0f
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = PastelMint,
                trackColor = PastelMint.copy(alpha = 0.15f),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "승인 ${stats.approvedMissions} · 거절 ${stats.rejectedMissions} · 전체 ${stats.totalMissions}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun formatWeekLabel(weekStart: String): String {
    // weekStart is "YYYY-MM-DD" format
    return try {
        val parts = weekStart.split("-")
        "${parts[1]}/${parts[2]}"
    } catch (e: Exception) {
        weekStart
    }
}
