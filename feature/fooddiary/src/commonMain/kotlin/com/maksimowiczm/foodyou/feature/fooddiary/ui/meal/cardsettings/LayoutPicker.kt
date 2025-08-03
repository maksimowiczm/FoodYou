package com.maksimowiczm.foodyou.feature.fooddiary.ui.meal.cardsettings

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.feature.fooddiary.preferences.MealsCardsLayout
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun LayoutPicker(
    layout: MealsCardsLayout,
    onLayoutChange: (MealsCardsLayout) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LayoutContainer(
            selected = layout == MealsCardsLayout.Horizontal,
            onClick = { onLayoutChange(MealsCardsLayout.Horizontal) },
            layout = { LayoutHorizontal() },
            label = {
                Text(
                    text = stringResource(Res.string.headline_horizontal),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        )

        LayoutContainer(
            selected = layout == MealsCardsLayout.Vertical,
            onClick = { onLayoutChange(MealsCardsLayout.Vertical) },
            layout = { LayoutVertical() },
            label = {
                Text(
                    text = stringResource(Res.string.headline_vertical),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LayoutContainer(
    selected: Boolean,
    onClick: () -> Unit,
    layout: @Composable () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainer
        },
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()
    )

    val animatedCornerSize by animateDpAsState(
        targetValue = if (selected) 28.dp else 12.dp,
        animationSpec = MaterialTheme.motionScheme.fastEffectsSpec()
    )

    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .graphicsLayer {
                    clip = true
                    shape = RoundedCornerShape(animatedCornerSize)
                }
                .drawBehind { drawRect(containerColor) }
                .clickable { onClick() }
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                layout()
                label()
            }
        }
    }
}
