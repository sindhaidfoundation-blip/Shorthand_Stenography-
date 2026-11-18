package com.example

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.example.ui.theme.PitmanDarkColorScheme
import com.example.ui.theme.PitmanDarkHighContrastColorScheme
import com.example.ui.theme.PitmanLightColorScheme
import com.example.ui.theme.PitmanLightHighContrastColorScheme
import com.example.ui.theme.resolvePitmanColorScheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.max
import kotlin.math.min

class PitmanColorTokensTest {

    private fun contrastRatio(fg: Color, bg: Color): Double {
        val l1 = fg.luminance() + 0.05
        val l2 = bg.luminance() + 0.05
        return max(l1, l2) / min(l1, l2)
    }

    private fun assertSchemeContrast(name: String, scheme: ColorScheme, minPrimaryRatio: Double) {
        assertTrue(
            "$name primary on surface contrast too low",
            contrastRatio(scheme.primary, scheme.surface) >= minPrimaryRatio
        )
        assertTrue(
            "$name onPrimary on primary contrast too low",
            contrastRatio(scheme.onPrimary, scheme.primary) >= minPrimaryRatio
        )
        assertTrue(
            "$name onSecondary on secondary contrast too low",
            contrastRatio(scheme.onSecondary, scheme.secondary) >= 4.5
        )
        assertTrue(
            "$name onSurface on surface contrast too low",
            contrastRatio(scheme.onSurface, scheme.surface) >= minPrimaryRatio
        )
        assertTrue(
            "$name onSurfaceVariant on surface contrast too low",
            contrastRatio(scheme.onSurfaceVariant, scheme.surface) >= 4.5
        )
        assertTrue(
            "$name onError on error contrast too low",
            contrastRatio(scheme.onError, scheme.error) >= 4.5
        )
    }

    @Test
    fun standardLightAndDarkSchemesMeetWcagContrastAndSurfaceProgression() {
        assertSchemeContrast("Light", PitmanLightColorScheme, 4.5)
        assertSchemeContrast("Dark", PitmanDarkColorScheme, 4.5)

        // Surface container elevation hierarchy should step smoothly in luminance
        assertTrue(
            PitmanLightColorScheme.surfaceContainerLowest.luminance() >
                PitmanLightColorScheme.surfaceContainerLow.luminance()
        )
        assertTrue(
            PitmanLightColorScheme.surfaceContainerLow.luminance() >
                PitmanLightColorScheme.surfaceContainer.luminance()
        )
        assertTrue(
            PitmanLightColorScheme.surfaceContainer.luminance() >
                PitmanLightColorScheme.surfaceContainerHigh.luminance()
        )
        assertTrue(
            PitmanLightColorScheme.surfaceContainerHigh.luminance() >
                PitmanLightColorScheme.surfaceContainerHighest.luminance()
        )
        assertTrue(
            PitmanDarkColorScheme.surfaceContainerLowest.luminance() <
                PitmanDarkColorScheme.surfaceContainerHighest.luminance()
        )
    }

    @Test
    fun highContrastSchemesMeetWcagAaaTargets() {
        assertSchemeContrast("LightHighContrast", PitmanLightHighContrastColorScheme, 7.0)
        assertSchemeContrast("DarkHighContrast", PitmanDarkHighContrastColorScheme, 7.0)

        assertEquals(PitmanLightColorScheme, resolvePitmanColorScheme(darkTheme = false, highContrast = false))
        assertEquals(PitmanDarkColorScheme, resolvePitmanColorScheme(darkTheme = true, highContrast = false))
        assertEquals(PitmanLightHighContrastColorScheme, resolvePitmanColorScheme(darkTheme = false, highContrast = true))
        assertEquals(PitmanDarkHighContrastColorScheme, resolvePitmanColorScheme(darkTheme = true, highContrast = true))
    }
}
