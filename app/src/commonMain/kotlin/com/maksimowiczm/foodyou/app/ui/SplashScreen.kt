package com.maksimowiczm.foodyou.app.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.tooling.preview.Preview
import com.maksimowiczm.foodyou.capabilities.theme.PreviewFoodYouTheme
import com.maksimowiczm.foodyou.shared.ui.brand
import com.maksimowiczm.foodyou.shared.ui.component.InteractiveLogo
import foodyou.app.generated.resources.*
import kotlin.time.Duration.Companion.seconds
import org.jetbrains.compose.resources.stringResource

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme
    val offset =
        rememberInfiniteTransition()
            .animateFloat(
                initialValue = 0f,
                targetValue = 2f,
                animationSpec =
                    infiniteRepeatable(
                        animation = tween(durationMillis = 10_000, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse,
                    ),
            )
    val brush =
        remember(offset) {
            object : ShaderBrush() {
                override fun createShader(size: Size): Shader {
                    val widthOffset = size.width * offset.value
                    val heightOffset = size.height * offset.value
                    return LinearGradientShader(
                        colors =
                            listOf(
                                colorScheme.primary,
                                colorScheme.secondary,
                                colorScheme.tertiary,
                            ),
                        from = Offset(widthOffset, heightOffset),
                        to = Offset(widthOffset + size.width, heightOffset + size.height),
                        tileMode = TileMode.Mirror,
                    )
                }
            }
        }
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(Res.string.app_name),
            style = MaterialTheme.typography.brand.displayMedium.copy(brush = brush),
        )
        InteractiveLogo(
            modifier = Modifier.safeContentPadding().fillMaxWidth().aspectRatio(1f),
            rotationAnimationSpec =
                infiniteRepeatable(
                    animation =
                        tween(
                            easing = FastOutSlowInEasing,
                            durationMillis = 2.seconds.inWholeMilliseconds.toInt(),
                        ),
                    repeatMode = RepeatMode.Restart,
                ),
            clickable = false,
            autoCycle = true,
        )
    }
}

@Preview
@Composable
private fun AboutScreenPreview() {
    PreviewFoodYouTheme {
        Surface {
            SplashScreen()
        }
    }
}
