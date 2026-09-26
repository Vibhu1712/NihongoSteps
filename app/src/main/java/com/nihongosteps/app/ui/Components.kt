@file:OptIn(ExperimentalMaterial3Api::class)

package com.nihongosteps.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nihongosteps.app.NihongoApp
import com.nihongosteps.app.R
import com.nihongosteps.app.data.Progress
import com.nihongosteps.app.ui.theme.LocalExtraColors
import com.nihongosteps.app.ui.theme.jp

val LocalApp = staticCompositionLocalOf<NihongoApp> { error("NihongoApp not provided") }

@Composable
fun rememberProgress(): State<Progress> =
    LocalApp.current.repository.progress.collectAsStateWithLifecycle(initialValue = Progress())

@Composable
fun ScreenScaffold(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = actions,
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        content = content,
    )
}

@Composable
fun SpeakButton(text: String, modifier: Modifier = Modifier, tint: Color = MaterialTheme.colorScheme.primary) {
    val speaker = LocalApp.current.speaker
    if (!speaker.available) return
    IconButton(onClick = { speaker.speak(text) }, modifier = modifier) {
        Icon(painterResource(R.drawable.ic_volume), contentDescription = "Listen", tint = tint)
    }
}

/**
 * A square of Japanese practice paper (マス目): outline plus a dashed centre cross.
 */
fun Modifier.masu(grid: Color, radius: Dp = 10.dp, width: Dp = 1.dp): Modifier = this
    .border(BorderStroke(width, grid), RoundedCornerShape(radius))
    .drawBehind {
        val dash = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 5.dp.toPx()))
        val w = width.toPx()
        drawLine(grid, Offset(size.width / 2, 0f), Offset(size.width / 2, size.height), w, pathEffect = dash)
        drawLine(grid, Offset(0f, size.height / 2), Offset(size.width, size.height / 2), w, pathEffect = dash)
    }

enum class OptionState { Idle, Correct, Wrong, Faded }

@Composable
fun OptionButton(
    text: String,
    state: OptionState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    japanese: Boolean = false,
    sub: String? = null,
) {
    val extra = LocalExtraColors.current
    val scheme = MaterialTheme.colorScheme
    val (bg, border, fg) = when (state) {
        OptionState.Idle -> Triple(scheme.surfaceContainerLowest, scheme.outlineVariant, scheme.onSurface)
        OptionState.Correct -> Triple(extra.correct.copy(alpha = 0.14f), extra.correct, extra.correct)
        OptionState.Wrong -> Triple(extra.wrong.copy(alpha = 0.12f), extra.wrong, extra.wrong)
        OptionState.Faded -> Triple(scheme.surfaceContainerLowest, scheme.outlineVariant.copy(alpha = 0.5f), scheme.onSurface.copy(alpha = 0.4f))
    }
    Surface(
        onClick = onClick,
        enabled = state == OptionState.Idle,
        shape = RoundedCornerShape(14.dp),
        color = bg,
        border = BorderStroke(if (state == OptionState.Idle || state == OptionState.Faded) 1.dp else 2.dp, border),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(horizontal = 18.dp, vertical = 14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text,
                style = if (japanese) jp(if (text.length > 6) 19 else 24) else MaterialTheme.typography.titleMedium,
                color = fg,
                textAlign = TextAlign.Center,
            )
            if (sub != null) Text(sub, style = MaterialTheme.typography.bodySmall, color = fg.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun ChoiceChips(options: List<String>, selected: String, onSelect: (String) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { o ->
            FilterChip(selected = o == selected, onClick = { onSelect(o) }, label = { Text(o) })
        }
    }
}

/** The red "well done" stamp a Japanese teacher puts on homework. */
@Composable
fun HankoStamp(good: Boolean, modifier: Modifier = Modifier) {
    val color = LocalExtraColors.current.stamp
    val anim = remember { Animatable(1.6f) }
    LaunchedEffect(Unit) { anim.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow)) }
    Box(
        modifier
            .size(118.dp)
            .scale(anim.value)
            .rotate(-12f)
            .border(3.dp, color, CircleShape)
            .drawBehind { drawCircle(color, radius = size.minDimension / 2 - 8.dp.toPx(), style = Stroke(1.dp.toPx())) },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            if (good) "よく\nできました" else "もう\nすこし",
            style = jp(20, FontWeight.SemiBold),
            color = color,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun ResultPanel(
    score: Int,
    total: Int,
    xp: Int,
    onAgain: () -> Unit,
    onDone: () -> Unit,
    detail: String? = null,
) {
    val good = total > 0 && score * 100 / total >= 70
    Column(
        Modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(16.dp))
        HankoStamp(good)
        Spacer(Modifier.height(28.dp))
        Text("$score / $total", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.SemiBold)
        Text(
            if (good) "Well done — that's a pass." else "Keep going — each round makes it stick.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        if (detail != null) {
            Spacer(Modifier.height(4.dp))
            Text(detail, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(8.dp))
        Text("+$xp XP", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.tertiary)
        Spacer(Modifier.height(28.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onDone) { Text("Done") }
            Button(onClick = onAgain) { Text("Play again") }
        }
    }
}

/** A labelled progress line used on the home and profile screens. */
@Composable
fun StatLine(label: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
    }
}
