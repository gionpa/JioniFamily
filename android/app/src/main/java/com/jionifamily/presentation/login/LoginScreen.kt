package com.jionifamily.presentation.login

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jionifamily.presentation.theme.DadColor
import com.jionifamily.presentation.theme.JioniColor
import com.jionifamily.presentation.theme.MomColor
import com.jionifamily.presentation.theme.PastelPinkLight
import com.jionifamily.presentation.theme.SoftRed
import kotlinx.coroutines.flow.collectLatest

data class AvatarInfo(
    val key: String,
    val emoji: String,
    val name: String,
    val color: Color,
)

private val avatars = listOf(
    AvatarInfo("mom", "\uD83C\uDF38", "엄마", MomColor),
    AvatarInfo("dad", "\uD83D\uDC53", "아빠", DadColor),
    AvatarInfo("jioni", "\u2B50", "지온이", JioniColor),
)

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loginSuccess.collectLatest { role ->
            onLoginSuccess(role)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "누구세요?",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Avatar selection
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            avatars.forEach { avatar ->
                AvatarCard(
                    avatar = avatar,
                    isSelected = state.selectedAvatar == avatar.key,
                    onClick = { viewModel.selectAvatar(avatar.key) },
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // PIN display
        if (state.selectedAvatar != null) {
            PinDisplay(pin = state.pin)

            Spacer(modifier = Modifier.height(8.dp))

            // Error message
            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = SoftRed,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // PIN pad
            if (state.isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp),
                )
            } else {
                PinPad(
                    onDigit = { viewModel.appendPin(it) },
                    onDelete = { viewModel.deletePin() },
                )
            }
        }
    }
}

@Composable
private fun AvatarCard(
    avatar: AvatarInfo,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "avatar_scale",
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) avatar.color else Color.Transparent,
        label = "avatar_border",
    )

    Column(
        modifier = Modifier
            .scale(scale)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(if (isSelected) avatar.color.copy(alpha = 0.3f) else avatar.color.copy(alpha = 0.1f))
                .border(3.dp, borderColor, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = avatar.emoji,
                fontSize = 40.sp,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = avatar.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        )
    }
}

@Composable
private fun PinDisplay(pin: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        repeat(4) { index ->
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(
                        if (index < pin.length) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    ),
            )
        }
    }
}

@Composable
private fun PinPad(
    onDigit: (String) -> Unit,
    onDelete: () -> Unit,
) {
    val digits = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("", "0", "del"),
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        digits.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                row.forEach { key ->
                    when (key) {
                        "" -> Spacer(modifier = Modifier.size(72.dp))
                        "del" -> {
                            IconButton(
                                onClick = onDelete,
                                modifier = Modifier.size(72.dp),
                            ) {
                                Icon(
                                    Icons.Default.Backspace,
                                    contentDescription = "삭제",
                                    tint = MaterialTheme.colorScheme.onBackground,
                                )
                            }
                        }
                        else -> {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(PastelPinkLight.copy(alpha = 0.5f))
                                    .clickable { onDigit(key) },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = key,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
