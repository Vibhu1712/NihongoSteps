package com.nihongosteps.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.nihongosteps.app.R

/**
 * Palette taken from a Japanese school notebook:
 * 紺 kon (indigo ink), 朱 shu (the teacher's red stamp), 抹茶 matcha (correct),
 * 半紙 practice paper, and the pale red grid of 原稿用紙 writing sheets.
 */
object Palette {
    val Kon = Color(0xFF1E2A5A)
    val KonLight = Color(0xFFB9C4F2)
    val Shu = Color(0xFFC8392B)
    val ShuLight = Color(0xFFFF8A7A)
    val Matcha = Color(0xFF4F7A2E)
    val MatchaLight = Color(0xFF9CCB77)
    val Paper = Color(0xFFF7F7F4)
    val Ink = Color(0xFF1B1C22)
    val Night = Color(0xFF15171C)
    val NightSurface = Color(0xFF1F222A)
}

@Immutable
data class ExtraColors(
    val correct: Color,
    val wrong: Color,
    val grid: Color,
    val stamp: Color,
    val paper: Color,
)

val LocalExtraColors = staticCompositionLocalOf {
    ExtraColors(Palette.Matcha, Palette.Shu, Color(0xFFE8B4A6), Palette.Shu, Color.White)
}

/** Handwriting-textbook typeface (Klee One) for Japanese glyphs. */
val KanaFont = FontFamily(
    Font(R.font.klee_one, FontWeight.Normal),
    Font(R.font.klee_one_semibold, FontWeight.SemiBold),
)

private val LightScheme = lightColorScheme(
    primary = Palette.Kon,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDE2F7),
    onPrimaryContainer = Palette.Kon,
    secondary = Palette.Shu,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFBE0DB),
    onSecondaryContainer = Color(0xFF5A140C),
    tertiary = Palette.Matcha,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFDCEBCB),
    onTertiaryContainer = Color(0xFF1E3309),
    background = Palette.Paper,
    onBackground = Palette.Ink,
    surface = Palette.Paper,
    onSurface = Palette.Ink,
    surfaceVariant = Color(0xFFE9E9E4),
    onSurfaceVariant = Color(0xFF55565E),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF1F1EC),
    surfaceContainer = Color(0xFFECECE6),
    surfaceContainerHigh = Color(0xFFE6E6E0),
    outline = Color(0xFF8C8D95),
    outlineVariant = Color(0xFFD5D5CF),
)

private val DarkScheme = darkColorScheme(
    primary = Palette.KonLight,
    onPrimary = Color(0xFF14204A),
    primaryContainer = Color(0xFF2E3B70),
    onPrimaryContainer = Color(0xFFDDE2F7),
    secondary = Palette.ShuLight,
    onSecondary = Color(0xFF5A140C),
    secondaryContainer = Color(0xFF6E2419),
    onSecondaryContainer = Color(0xFFFBE0DB),
    tertiary = Palette.MatchaLight,
    onTertiary = Color(0xFF1E3309),
    tertiaryContainer = Color(0xFF34521C),
    onTertiaryContainer = Color(0xFFDCEBCB),
    background = Palette.Night,
    onBackground = Color(0xFFE4E4E9),
    surface = Palette.Night,
    onSurface = Color(0xFFE4E4E9),
    surfaceVariant = Color(0xFF2A2D35),
    onSurfaceVariant = Color(0xFFB6B8C2),
    surfaceContainerLowest = Color(0xFF101217),
    surfaceContainerLow = Color(0xFF1B1D24),
    surfaceContainer = Palette.NightSurface,
    surfaceContainerHigh = Color(0xFF262932),
    outline = Color(0xFF8A8C96),
    outlineVariant = Color(0xFF3A3D46),
)

private val AppTypography = Typography().let { t ->
    t.copy(
        headlineMedium = t.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
        titleLarge = t.titleLarge.copy(fontWeight = FontWeight.SemiBold),
        titleMedium = t.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        bodyLarge = t.bodyLarge.copy(lineHeight = 26.sp),
        bodyMedium = t.bodyMedium.copy(lineHeight = 22.sp),
    )
}

/** Text style for Japanese display text. */
fun jp(size: Int, weight: FontWeight = FontWeight.Normal) =
    TextStyle(fontFamily = KanaFont, fontSize = size.sp, fontWeight = weight, lineHeight = (size * 1.35f).sp)

@Composable
fun NihongoTheme(dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val extra = if (dark) {
        ExtraColors(Palette.MatchaLight, Palette.ShuLight, Color(0xFF5A3A36), Palette.ShuLight, Palette.NightSurface)
    } else {
        ExtraColors(Palette.Matcha, Palette.Shu, Color(0xFFE8B4A6), Palette.Shu, Color.White)
    }
    androidx.compose.runtime.CompositionLocalProvider(LocalExtraColors provides extra) {
        MaterialTheme(
            colorScheme = if (dark) DarkScheme else LightScheme,
            typography = AppTypography,
            content = content,
        )
    }
}
