package com.jionifamily.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.jionifamily.domain.model.CompletionStatus
import com.jionifamily.domain.model.MissionCategory
import com.jionifamily.presentation.theme.PastelBlueLight
import com.jionifamily.presentation.theme.PastelMint
import com.jionifamily.presentation.theme.PastelYellow
import com.jionifamily.presentation.theme.SoftRed
import com.jionifamily.presentation.theme.WarmGray

@Composable
fun MissionCard(
    missionName: String,
    category: MissionCategory?,
    rewardCoins: Int?,
    status: CompletionStatus? = null,
    modifier: Modifier = Modifier,
    actions: @Composable (() -> Unit)? = null,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Category emoji
                if (category != null) {
                    Text(
                        text = category.emoji,
                        fontSize = 28.sp,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = missionName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    if (rewardCoins != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "\uD83E\uDE99", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$rewardCoins 코인",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                // Status chip
                if (status != null) {
                    StatusChip(status = status)
                }
            }

            if (actions != null) {
                Spacer(modifier = Modifier.height(12.dp))
                actions()
            }
        }
    }
}

@Composable
fun StatusChip(status: CompletionStatus) {
    val (bgColor, textColor) = when (status) {
        CompletionStatus.PENDING -> PastelBlueLight to MaterialTheme.colorScheme.onBackground
        CompletionStatus.SUBMITTED -> PastelYellow to MaterialTheme.colorScheme.onBackground
        CompletionStatus.APPROVED -> PastelMint to MaterialTheme.colorScheme.onBackground
        CompletionStatus.REJECTED -> SoftRed to MaterialTheme.colorScheme.onBackground
        CompletionStatus.MISSED -> Color(0xFFE0E0E0) to WarmGray
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
