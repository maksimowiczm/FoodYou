package com.maksimowiczm.foodyou.app.ui.onboarding

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.common.component.InteractiveLogo
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import com.maksimowiczm.foodyou.app.ui.common.theme.PreviewFoodYouTheme
import com.maksimowiczm.foodyou.app.ui.common.theme.brand
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalAppConfig
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BeforeYouStartScreen(onContinue: () -> Unit, modifier: Modifier = Modifier) {
    val appConfig = LocalAppConfig.current
    val uriHandler = LocalUriHandler.current

    BeforeYouStartScreen(
        onContinue = onContinue,
        onPrivacyPolicy = { uriHandler.openUri(appConfig.privacyPolicyUri) },
        modifier = modifier,
    )
}

@Composable
private fun BeforeYouStartScreen(
    onContinue: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val fabHeight = ButtonDefaults.LargeContainerHeight
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
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.headline_before_you_start),
                        style = MaterialTheme.typography.displaySmall,
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            Button(
                onClick = onContinue,
                shapes = ButtonDefaults.shapesFor(fabHeight),
                contentPadding = ButtonDefaults.contentPaddingFor(fabHeight),
            ) {
                Text(
                    text = stringResource(Res.string.action_agree_and_continue),
                    style = ButtonDefaults.textStyleFor(fabHeight),
                )
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues.add(bottom = fabHeight + 16.dp).add(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    InteractiveLogo(Modifier.widthIn(max = 400.dp).aspectRatio(1f).fillMaxSize())
                    Text(
                        text = stringResource(Res.string.app_name),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.brand.displayMedium.copy(brush = brush),
                    )
                }
            }
            item {
                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AssistChip(
                        onClick = onPrivacyPolicy,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.PrivacyTip,
                                contentDescription = null,
                                modifier = Modifier.size(AssistChipDefaults.IconSize),
                            )
                        },
                        label = { Text(stringResource(Res.string.headline_privacy_policy)) },
                    )
                }
            }
            item {
                Text(
                    text = stringResource(Res.string.onboarding_privacy_tip),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

@Preview
@Composable
private fun BeforeYouStartScreenPreview() {
    PreviewFoodYouTheme { BeforeYouStartScreen(onContinue = {}, onPrivacyPolicy = {}) }
}
