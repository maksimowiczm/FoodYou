package com.maksimowiczm.foodyou.shared.ui.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun FavoriteIconButton(
    isFavorite: Boolean,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
) {
    val iconSize =
        animateDpAsState(
            if (isFavorite) 32.dp else 24.dp,
            animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
        )

    val vector =
        if (isFavorite) painterResource(Res.drawable.ic_kid_star_filled)
        else painterResource(Res.drawable.ic_kid_star)
    val tint = if (isFavorite) MaterialTheme.colorScheme.primary else LocalContentColor.current

    IconButton(
        onClick = { onChange(!isFavorite) },
        enabled = enabled,
        shapes = IconButtonDefaults.shapes(),
        colors = colors,
        modifier = modifier,
    ) {
        Icon(
            painter = vector,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(iconSize.value),
        )
        AnimatedCheckmark(
            visible = isFavorite,
            modifier = Modifier.size(iconSize.value - 16.dp),
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@Composable
private fun AnimatedCheckmark(
    visible: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current,
    strokeWidth: Dp = 2.dp,
) {
    val progress by
        animateFloatAsState(
            targetValue = if (visible) 1f else 0f,
            animationSpec = if (visible) MaterialTheme.motionScheme.slowEffectsSpec() else snap(),
        )

    if (progress > 0f) {
        Canvas(modifier) {
            val w = size.width
            val h = size.height

            val checkPath =
                Path().apply {
                    moveTo(w * 0.20f, h * 0.55f)
                    lineTo(w * 0.42f, h * 0.75f)
                    lineTo(w * 0.80f, h * 0.28f)
                }

            val measure = PathMeasure().apply { setPath(checkPath, false) }
            val length = measure.length

            val animatedPath = Path()
            measure.getSegment(0f, length * progress, animatedPath, true)

            drawPath(
                path = animatedPath,
                color = color,
                style =
                    Stroke(
                        width = strokeWidth.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                    ),
            )
        }
    }
}
