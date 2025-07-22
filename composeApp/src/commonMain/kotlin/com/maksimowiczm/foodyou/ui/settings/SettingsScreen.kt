package com.maksimowiczm.foodyou.ui.settings

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.ext.add
import com.maksimowiczm.foodyou.feature.about.AboutSettingsListItem
import com.maksimowiczm.foodyou.feature.fooddiary.ui.MealSettingsListItem
import com.maksimowiczm.foodyou.feature.language.LanguageSettingsListItem
import com.maksimowiczm.foodyou.ui.personalization.PersonalizationSettingsListItem
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.description_sponsor_short_2
import foodyou.app.generated.resources.headline_settings
import foodyou.app.generated.resources.headline_sponsor
import kotlin.math.PI
import kotlin.math.sin
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onPersonalization: () -> Unit,
    onMeals: () -> Unit,
    onLanguage: () -> Unit,
    onSponsor: () -> Unit,
    onAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val color = MaterialTheme.colorScheme.surface
    val contentColor = MaterialTheme.colorScheme.onSurface
    val shape = RectangleShape

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Text(stringResource(Res.string.headline_settings))
                },
                navigationIcon = {
                    ArrowBackIconButton(onBack)
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues.add(vertical = 8.dp)
        ) {
            item {
                SponsorSettingsListItem(
                    onClick = onSponsor,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                AnimatedWavyLine(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                        .height(16.dp)
                )
            }

            item {
                PersonalizationSettingsListItem(
                    onClick = onPersonalization,
                    shape = shape,
                    color = color,
                    contentColor = contentColor
                )
            }

            item {
                MealSettingsListItem(
                    onClick = onMeals,
                    shape = shape,
                    color = color,
                    contentColor = contentColor
                )
            }

            item {
                LanguageSettingsListItem(
                    onClick = onLanguage,
                    shape = shape,
                    containerColor = color,
                    contentColor = contentColor
                )
            }

            item {
                AboutSettingsListItem(
                    onClick = onAbout,
                    shape = shape,
                    color = color,
                    contentColor = contentColor
                )
            }
        }
    }
}

@Composable
fun AnimatedWavyLine(
    color: Color,
    strokeWidth: Dp,
    modifier: Modifier = Modifier,
    frequency: Float = 0.05f,
    animationSpec: InfiniteRepeatableSpec<Float> = infiniteRepeatable(
        animation = tween(5_000, easing = LinearEasing),
        repeatMode = RepeatMode.Restart
    )
) {
    val infiniteTransition = rememberInfiniteTransition()
    val phase by infiniteTransition.animateFloat(
        initialValue = 2 * PI.toFloat(),
        targetValue = 0f,
        animationSpec = animationSpec
    )

    Canvas(modifier) {
        val centerY = size.height / 2
        val amplitude = size.height / 4

        val path = Path().apply {
            for (x in 0..size.width.toInt()) {
                val y = centerY + amplitude * sin(frequency * x + phase)
                if (x == 0) {
                    moveTo(x.toFloat(), y)
                } else {
                    lineTo(x.toFloat(), y)
                }
            }
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = strokeWidth.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SponsorSettingsListItem(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(96.dp),
        shapes = ButtonDefaults.shapes(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        contentPadding = PaddingValues(horizontal = 48.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.VolunteerActivism,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Column {
                Text(
                    text = stringResource(Res.string.headline_sponsor),
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = stringResource(Res.string.description_sponsor_short_2),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
