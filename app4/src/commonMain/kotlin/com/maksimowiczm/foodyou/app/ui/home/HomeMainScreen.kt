package com.maksimowiczm.foodyou.app.ui.home

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.maksimowiczm.foodyou.app.ui.common.component.UiProfileAvatar
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import com.maksimowiczm.foodyou.app.ui.common.extension.now
import com.valentinilk.shimmer.shimmer
import foodyou.app.generated.resources.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun HomeMainScreen(
    navController: NavController,
    selectedProfile: ProfileUiState?,
    onProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: HomeViewModel = koinInject()
    val savedHomeOrder by viewModel.homeOrder.collectAsStateWithLifecycle()
    val order =
        remember(savedHomeOrder) { savedHomeOrder.mapNotNull { homeCardComposablesMap[it] } }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val homeState =
        rememberHomeState(navController = navController, initialSelectedDate = LocalDate.now())

    Scaffold(
        modifier = modifier,
        topBar = {
            TopBar(
                selectedProfile = selectedProfile,
                onProfile = onProfile,
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues.add(vertical = 8.dp),
        ) {
            order.forEach { feature ->
                item(key = feature.feature) {
                    feature.HomeCard(
                        homeState = homeState,
                        paddingValues = PaddingValues(horizontal = 8.dp),
                        modifier =
                            Modifier.animateItem(
                                fadeInSpec = null,
                                placementSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
                                fadeOutSpec = null,
                            ),
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun TopBar(
    selectedProfile: ProfileUiState?,
    onProfile: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
) {
    val transition = updateTransition(selectedProfile)

    TopAppBar(
        title = {
            transition.Crossfade {
                if (it != null) {
                    val welcomeMessage =
                        stringResource(Res.string.headline_welcome_user_message, it.name)

                    val colorScheme = MaterialTheme.colorScheme
                    val colors =
                        remember(colorScheme) {
                            listOf(colorScheme.primary, colorScheme.secondary, colorScheme.tertiary)
                        }

                    val infiniteTransition = rememberInfiniteTransition()
                    val offset =
                        infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 2f,
                            animationSpec =
                                infiniteRepeatable(
                                    animation =
                                        tween(durationMillis = 20_000, easing = LinearEasing),
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
                                        colors = colors,
                                        from = Offset(widthOffset, heightOffset),
                                        to =
                                            Offset(
                                                widthOffset + size.width,
                                                heightOffset + size.height,
                                            ),
                                        tileMode = TileMode.Mirror,
                                    )
                                }
                            }
                        }

                    Text(
                        text = animateTextByCharacter(welcomeMessage),
                        style = LocalTextStyle.current.copy(brush = brush),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        },
        modifier = modifier,
        actions = {
            transition.Crossfade {
                if (selectedProfile == null) {
                    Box(
                        Modifier.shimmer()
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    )
                } else {
                    when (val avatar = selectedProfile.avatar) {
                        is UiProfileAvatar.Photo -> {
                            FilledTonalIconButton(
                                onClick = onProfile,
                                shapes = IconButtonDefaults.shapes(),
                            ) {
                                avatar.Avatar(Modifier.size(40.dp))
                            }
                        }

                        is UiProfileAvatar.Predefined ->
                            FilledTonalIconButton(
                                onClick = onProfile,
                                shapes = IconButtonDefaults.shapes(),
                            ) {
                                avatar.Avatar(Modifier.size(24.dp))
                            }
                    }
                }
            }
        },
        scrollBehavior = scrollBehavior,
    )
}

@Composable
private fun animateTextByCharacter(
    targetString: String,
    delayDuration: Duration = 20.milliseconds,
): String {
    val coroutineScope = rememberCoroutineScope()
    var hasAnimated by rememberSaveable(targetString) { mutableStateOf(false) }
    var displayedString by remember(targetString) { mutableStateOf("") }

    DisposableEffect(targetString) {
        displayedString = ""

        val job =
            coroutineScope.launch {
                val builder = StringBuilder()
                targetString.forEach { char ->
                    builder.append(char)
                    displayedString = builder.toString()
                    delay(delayDuration)
                }

                hasAnimated = true
            }

        onDispose {
            job.cancel()
            hasAnimated = true
        }
    }

    return if (hasAnimated) targetString else displayedString
}
