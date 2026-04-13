package com.jionifamily.presentation.parent.home

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jionifamily.presentation.common.components.MissionCard
import com.jionifamily.presentation.theme.CreamWhite
import com.jionifamily.presentation.theme.PastelMint
import com.jionifamily.presentation.theme.PastelPink
import com.jionifamily.presentation.theme.SoftRed

@Composable
fun ParentHomeScreen(
    onNavigateToCreateMission: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: ParentHomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = CreamWhite,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateMission,
                containerColor = PastelPink,
                shape = RoundedCornerShape(16.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = "미션 만들기")
            }
        },
        bottomBar = {
            NavigationBar(containerColor = CreamWhite) {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("홈") },
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToCreateMission,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    label = { Text("미션 만들기") },
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToHistory,
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    label = { Text("지난 미션") },
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToSettings,
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("설정") },
                )
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Greeting + week range
            item {
                Text(
                    text = "안녕하세요, ${state.userName}!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                if (state.weekStart.isNotBlank()) {
                    Text(
                        text = "\uD83D\uDCC5 ${com.jionifamily.util.formatWeekRange(state.weekStart)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Pending approvals
            if (state.pendingApprovals.isNotEmpty()) {
                item {
                    Text(
                        text = "승인 대기 (${state.pendingApprovals.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(state.pendingApprovals) { completion ->
                            ApprovalCard(
                                completion = completion,
                                onApprove = { viewModel.approveCompletion(completion.id) },
                                onReject = { viewModel.rejectCompletion(completion.id) },
                            )
                        }
                    }
                }
            }

            // This week's missions
            item {
                Text(
                    text = "이번 주 미션",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            if (state.completions.isEmpty() && !state.isLoading) {
                item {
                    Text(
                        text = "아직 미션이 없어요",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 24.dp),
                    )
                }
            }

            items(state.completions) { completion ->
                MissionCard(
                    missionName = completion.missionName ?: "",
                    category = completion.missionCategory,
                    rewardCoins = completion.missionRewardCoins,
                    status = completion.status,
                )
            }
        }
    }
}

@Composable
private fun ApprovalCard(
    completion: com.jionifamily.domain.model.MissionCompletion,
    onApprove: () -> Unit,
    onReject: () -> Unit,
) {
    Card(
        modifier = Modifier.width(260.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = completion.missionName ?: "",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "\uD83E\uDE99 ${completion.missionRewardCoins ?: 0} 코인",
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row {
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = PastelMint),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("승인", color = MaterialTheme.colorScheme.onBackground)
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("거절", color = SoftRed)
                }
            }
        }
    }
}
