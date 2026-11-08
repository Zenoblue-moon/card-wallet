package com.example.cardwallet.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import com.example.cardwallet.R

data class AppPalette(
    val key: String,
    val lightPrimary: Color,
    val lightSecondary: Color,
    val lightTertiary: Color,
    val darkPrimary: Color,
    val darkSecondary: Color,
    val darkTertiary: Color,
) {
    val seed: Color get() = lightPrimary
}

val appPalettes = listOf(
    AppPalette(
        "purple",
        Color(0xFF6750A4), Color(0xFF7B1FA2), Color(0xFF3949AB),
        Color(0xFFD0BCFF), Color(0xFFE8B4F8), Color(0xFFB3B6FF)
    ),
    AppPalette(
        "blue",
        Color(0xFF1565C0), Color(0xFF00ACC1), Color(0xFF0288D1),
        Color(0xFF8AB4F8), Color(0xFF80DEEA), Color(0xFF7FC4FF)
    ),
    AppPalette(
        "teal",
        Color(0xFF00838F), Color(0xFF00B0FF), Color(0xFF00695C),
        Color(0xFF4DD0E1), Color(0xFF80D8FF), Color(0xFF4DB6AC)
    ),
    AppPalette(
        "green",
        Color(0xFF2E7D32), Color(0xFF7CB342), Color(0xFF00897B),
        Color(0xFFA5D6A7), Color(0xFFC5E1A5), Color(0xFF80CBC4)
    ),
    AppPalette(
        "pink",
        Color(0xFFB4005C), Color(0xFFE91E63), Color(0xFF8E24AA),
        Color(0xFFF8BBD0), Color(0xFFF48FB1), Color(0xFFCE93D8)
    ),
)

@Composable
fun paletteLabel(key: String): String = stringResource(
    when (key) {
        "blue" -> R.string.palette_blue
        "teal" -> R.string.palette_teal
        "green" -> R.string.palette_green
        "pink" -> R.string.palette_pink
        else -> R.string.palette_purple
    }
)

fun paletteByKey(key: String): AppPalette =
    appPalettes.firstOrNull { it.key == key } ?: appPalettes.first()

fun appColorScheme(palette: AppPalette, dark: Boolean): ColorScheme {
    val primary = if (dark) palette.darkPrimary else palette.lightPrimary
    val secondary = if (dark) palette.darkSecondary else palette.lightSecondary
    val tertiary = if (dark) palette.darkTertiary else palette.lightTertiary
    val containerBase = if (dark) Color.Black else Color.White
    val containerAmount = if (dark) 0.80f else 0.88f

    val primaryContainer = lerp(primary, containerBase, containerAmount)
    val secondaryContainer = lerp(secondary, containerBase, containerAmount)
    val tertiaryContainer = lerp(tertiary, containerBase, containerAmount)

    return if (dark) darkColorScheme(
        primary = primary,
        onPrimary = contrastOn(primary),
        primaryContainer = primaryContainer,
        onPrimaryContainer = contrastOn(primaryContainer),
        secondary = secondary,
        onSecondary = contrastOn(secondary),
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = contrastOn(secondaryContainer),
        tertiary = tertiary,
        onTertiary = contrastOn(tertiary),
        tertiaryContainer = tertiaryContainer,
        onTertiaryContainer = contrastOn(tertiaryContainer),
    ) else lightColorScheme(
        primary = primary,
        onPrimary = contrastOn(primary),
        primaryContainer = primaryContainer,
        onPrimaryContainer = contrastOn(primaryContainer),
        secondary = secondary,
        onSecondary = contrastOn(secondary),
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = contrastOn(secondaryContainer),
        tertiary = tertiary,
        onTertiary = contrastOn(tertiary),
        tertiaryContainer = tertiaryContainer,
        onTertiaryContainer = contrastOn(tertiaryContainer),
    )
}

private fun contrastOn(background: Color): Color =
    if (background.luminance() > 0.5f) Color(0xFF1D1B20) else Color.White