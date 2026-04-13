package com.jionifamily.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val JioniColorScheme = lightColorScheme(
    primary = PastelPink,
    onPrimary = DarkCharcoal,
    primaryContainer = PastelPinkLight,
    onPrimaryContainer = DarkCharcoal,
    secondary = PastelMint,
    onSecondary = DarkCharcoal,
    secondaryContainer = PastelMintLight,
    onSecondaryContainer = DarkCharcoal,
    tertiary = PastelBlue,
    onTertiary = DarkCharcoal,
    tertiaryContainer = PastelBlueLight,
    onTertiaryContainer = DarkCharcoal,
    background = CreamWhite,
    onBackground = DarkCharcoal,
    surface = CreamWhite,
    onSurface = DarkCharcoal,
    surfaceVariant = PastelYellowLight,
    onSurfaceVariant = WarmGray,
    error = SoftRed,
    onError = DarkCharcoal,
    errorContainer = SoftRedLight,
    onErrorContainer = DarkCharcoal,
    outline = PastelPink,
)

@Composable
fun JioniFamilyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = JioniColorScheme,
        typography = JioniTypography,
        shapes = JioniShapes,
        content = content,
    )
}
