package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class ThemeContrastMode(val label: String) {
    STANDARD("Standard"),
    HIGH_CONTRAST("High Contrast")
}

enum class ThemeAppearance(val label: String) {
    SYSTEM("System"),
    LIGHT("Light"),
    DARK("Dark")
}

@Immutable
data class StenoExtendedColors(
    val surfaceDim: Color,
    val surfaceBright: Color,
    val surfaceContainerLowest: Color,
    val surfaceContainerLow: Color,
    val surfaceContainer: Color,
    val surfaceContainerHigh: Color,
    val surfaceContainerHighest: Color,
    val primaryFixed: Color,
    val primaryFixedDim: Color,
    val onPrimaryFixed: Color,
    val onPrimaryFixedVariant: Color,
    val secondaryFixed: Color,
    val secondaryFixedDim: Color,
    val onSecondaryFixed: Color,
    val onSecondaryFixedVariant: Color,
    val tertiaryFixed: Color,
    val tertiaryFixedDim: Color,
    val onTertiaryFixed: Color,
    val onTertiaryFixedVariant: Color,
    val highlightAmber: Color,
    val judicialRuleLine: Color,
    val scrim: Color
)

val LocalStenoColors = staticCompositionLocalOf {
    LightExtendedColors
}

private val LightExtendedColors = StenoExtendedColors(
    surfaceDim = SurfaceDimLight,
    surfaceBright = SurfaceBrightLight,
    surfaceContainerLowest = SurfaceContainerLowestLight,
    surfaceContainerLow = SurfaceContainerLowLight,
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHigh = SurfaceContainerHighLight,
    surfaceContainerHighest = SurfaceContainerHighestLight,
    primaryFixed = PrimaryFixedLight,
    primaryFixedDim = PrimaryFixedDimLight,
    onPrimaryFixed = OnPrimaryFixedLight,
    onPrimaryFixedVariant = OnPrimaryFixedVariantLight,
    secondaryFixed = SecondaryFixedLight,
    secondaryFixedDim = SecondaryFixedDimLight,
    onSecondaryFixed = OnSecondaryFixedLight,
    onSecondaryFixedVariant = OnSecondaryFixedVariantLight,
    tertiaryFixed = TertiaryFixedLight,
    tertiaryFixedDim = TertiaryFixedDimLight,
    onTertiaryFixed = OnTertiaryFixedLight,
    onTertiaryFixedVariant = OnTertiaryFixedVariantLight,
    highlightAmber = OnTertiaryContainerLight,
    judicialRuleLine = OutlineVariantLight,
    scrim = ScrimLight
)

private val DarkExtendedColors = StenoExtendedColors(
    surfaceDim = SurfaceDimDark,
    surfaceBright = SurfaceBrightDark,
    surfaceContainerLowest = SurfaceContainerLowestDark,
    surfaceContainerLow = SurfaceContainerLowDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    surfaceContainerHighest = SurfaceContainerHighestDark,
    primaryFixed = PrimaryFixedDark,
    primaryFixedDim = PrimaryFixedDimDark,
    onPrimaryFixed = OnPrimaryFixedDark,
    onPrimaryFixedVariant = OnPrimaryFixedVariantDark,
    secondaryFixed = SecondaryFixedDark,
    secondaryFixedDim = SecondaryFixedDimDark,
    onSecondaryFixed = OnSecondaryFixedDark,
    onSecondaryFixedVariant = OnSecondaryFixedVariantDark,
    tertiaryFixed = TertiaryFixedDark,
    tertiaryFixedDim = TertiaryFixedDimDark,
    onTertiaryFixed = OnTertiaryFixedDark,
    onTertiaryFixedVariant = OnTertiaryFixedVariantDark,
    highlightAmber = TertiaryDark,
    judicialRuleLine = OutlineVariantDark,
    scrim = ScrimDark
)

private val LightHighContrastExtendedColors = LightExtendedColors.copy(
    surfaceDim = SurfaceDimHighContrastLight,
    surfaceBright = SurfaceBrightHighContrastLight,
    surfaceContainerLowest = SurfaceContainerLowestHighContrastLight,
    surfaceContainerLow = SurfaceContainerLowHighContrastLight,
    surfaceContainer = SurfaceContainerHighContrastLight,
    surfaceContainerHigh = SurfaceContainerHighHighContrastLight,
    surfaceContainerHighest = SurfaceContainerHighestHighContrastLight,
    onSecondaryFixedVariant = SecondaryHighContrastLight,
    judicialRuleLine = OutlineHighContrastLight
)

private val DarkHighContrastExtendedColors = DarkExtendedColors.copy(
    surfaceDim = SurfaceDimHighContrastDark,
    surfaceBright = SurfaceBrightHighContrastDark,
    surfaceContainerLowest = SurfaceContainerLowestHighContrastDark,
    surfaceContainerLow = SurfaceContainerLowHighContrastDark,
    surfaceContainer = SurfaceContainerHighContrastDark,
    surfaceContainerHigh = SurfaceContainerHighHighContrastDark,
    surfaceContainerHighest = SurfaceContainerHighestHighContrastDark,
    onSecondaryFixedVariant = SecondaryHighContrastDark,
    highlightAmber = TertiaryHighContrastDark,
    judicialRuleLine = OutlineHighContrastDark
)

val PitmanLightColorScheme: ColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    inversePrimary = InversePrimaryLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = TertiaryContainerLight,
    onTertiaryContainer = OnTertiaryContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    surfaceTint = SurfaceTintLight,
    inverseSurface = InverseSurfaceLight,
    inverseOnSurface = InverseOnSurfaceLight,
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,
    scrim = ScrimLight,
    surfaceBright = SurfaceBrightLight,
    surfaceDim = SurfaceDimLight,
    surfaceContainerLowest = SurfaceContainerLowestLight,
    surfaceContainerLow = SurfaceContainerLowLight,
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHigh = SurfaceContainerHighLight,
    surfaceContainerHighest = SurfaceContainerHighestLight
)

val PitmanDarkColorScheme: ColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    inversePrimary = InversePrimaryDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    surfaceTint = SurfaceTintDark,
    inverseSurface = InverseSurfaceDark,
    inverseOnSurface = InverseOnSurfaceDark,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,
    scrim = ScrimDark,
    surfaceBright = SurfaceBrightDark,
    surfaceDim = SurfaceDimDark,
    surfaceContainerLowest = SurfaceContainerLowestDark,
    surfaceContainerLow = SurfaceContainerLowDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    surfaceContainerHighest = SurfaceContainerHighestDark
)

val PitmanLightHighContrastColorScheme: ColorScheme = lightColorScheme(
    primary = PrimaryHighContrastLight,
    onPrimary = OnPrimaryHighContrastLight,
    primaryContainer = PrimaryContainerHighContrastLight,
    onPrimaryContainer = OnPrimaryContainerHighContrastLight,
    inversePrimary = InversePrimaryHighContrastLight,
    secondary = SecondaryHighContrastLight,
    onSecondary = OnSecondaryHighContrastLight,
    secondaryContainer = SecondaryContainerHighContrastLight,
    onSecondaryContainer = OnSecondaryContainerHighContrastLight,
    tertiary = TertiaryHighContrastLight,
    onTertiary = OnTertiaryHighContrastLight,
    tertiaryContainer = TertiaryContainerHighContrastLight,
    onTertiaryContainer = OnTertiaryContainerHighContrastLight,
    background = SurfaceHighContrastLight,
    onBackground = OnSurfaceHighContrastLight,
    surface = SurfaceHighContrastLight,
    onSurface = OnSurfaceHighContrastLight,
    surfaceVariant = SurfaceVariantHighContrastLight,
    onSurfaceVariant = OnSurfaceVariantHighContrastLight,
    surfaceTint = SurfaceTintHighContrastLight,
    inverseSurface = InverseSurfaceHighContrastLight,
    inverseOnSurface = InverseOnSurfaceHighContrastLight,
    error = ErrorHighContrastLight,
    onError = OnErrorHighContrastLight,
    errorContainer = ErrorContainerHighContrastLight,
    onErrorContainer = OnErrorContainerHighContrastLight,
    outline = OutlineHighContrastLight,
    outlineVariant = OutlineVariantHighContrastLight,
    scrim = ScrimLight,
    surfaceBright = SurfaceBrightHighContrastLight,
    surfaceDim = SurfaceDimHighContrastLight,
    surfaceContainerLowest = SurfaceContainerLowestHighContrastLight,
    surfaceContainerLow = SurfaceContainerLowHighContrastLight,
    surfaceContainer = SurfaceContainerHighContrastLight,
    surfaceContainerHigh = SurfaceContainerHighHighContrastLight,
    surfaceContainerHighest = SurfaceContainerHighestHighContrastLight
)

val PitmanDarkHighContrastColorScheme: ColorScheme = darkColorScheme(
    primary = PrimaryHighContrastDark,
    onPrimary = OnPrimaryHighContrastDark,
    primaryContainer = PrimaryContainerHighContrastDark,
    onPrimaryContainer = OnPrimaryContainerHighContrastDark,
    inversePrimary = InversePrimaryHighContrastDark,
    secondary = SecondaryHighContrastDark,
    onSecondary = OnSecondaryHighContrastDark,
    secondaryContainer = SecondaryContainerHighContrastDark,
    onSecondaryContainer = OnSecondaryContainerHighContrastDark,
    tertiary = TertiaryHighContrastDark,
    onTertiary = OnTertiaryHighContrastDark,
    tertiaryContainer = TertiaryContainerHighContrastDark,
    onTertiaryContainer = OnTertiaryContainerHighContrastDark,
    background = SurfaceHighContrastDark,
    onBackground = OnSurfaceHighContrastDark,
    surface = SurfaceHighContrastDark,
    onSurface = OnSurfaceHighContrastDark,
    surfaceVariant = SurfaceVariantHighContrastDark,
    onSurfaceVariant = OnSurfaceVariantHighContrastDark,
    surfaceTint = SurfaceTintHighContrastDark,
    inverseSurface = InverseSurfaceHighContrastDark,
    inverseOnSurface = InverseOnSurfaceHighContrastDark,
    error = ErrorHighContrastDark,
    onError = OnErrorHighContrastDark,
    errorContainer = ErrorContainerHighContrastDark,
    onErrorContainer = OnErrorContainerHighContrastDark,
    outline = OutlineHighContrastDark,
    outlineVariant = OutlineVariantHighContrastDark,
    scrim = ScrimDark,
    surfaceBright = SurfaceBrightHighContrastDark,
    surfaceDim = SurfaceDimHighContrastDark,
    surfaceContainerLowest = SurfaceContainerLowestHighContrastDark,
    surfaceContainerLow = SurfaceContainerLowHighContrastDark,
    surfaceContainer = SurfaceContainerHighContrastDark,
    surfaceContainerHigh = SurfaceContainerHighHighContrastDark,
    surfaceContainerHighest = SurfaceContainerHighestHighContrastDark
)

val PitmanShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

fun resolvePitmanColorScheme(
    darkTheme: Boolean,
    highContrast: Boolean = false
): ColorScheme = when {
    darkTheme && highContrast -> PitmanDarkHighContrastColorScheme
    darkTheme -> PitmanDarkColorScheme
    highContrast -> PitmanLightHighContrastColorScheme
    else -> PitmanLightColorScheme
}

@Composable
fun PitmanStenoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = resolvePitmanColorScheme(darkTheme = darkTheme, highContrast = highContrast)
    val extendedColors = when {
        darkTheme && highContrast -> DarkHighContrastExtendedColors
        darkTheme -> DarkExtendedColors
        highContrast -> LightHighContrastExtendedColors
        else -> LightExtendedColors
    }

    CompositionLocalProvider(LocalStenoColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = PitmanTypography,
            shapes = PitmanShapes,
            content = content
        )
    }
}
