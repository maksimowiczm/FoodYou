package com.maksimowiczm.foodyou.app.ui.home

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.maksimowiczm.foodyou.app.ui.common.extension.now
import com.valentinilk.shimmer.shimmer
import foodyou.app.generated.resources.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@OptIn(ExperimentalAnimationApi::class)
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

    val transition = updateTransition(selectedProfile)
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val homeState =
        rememberHomeState(navController = navController, initialSelectedDate = LocalDate.now())

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    transition.Crossfade {
                        if (selectedProfile != null) {
                            val welcomeMessage =
                                stringResource(
                                    Res.string.headline_welcome_user_message,
                                    selectedProfile.name,
                                )

                            val colors =
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary,
                                    MaterialTheme.colorScheme.tertiary,
                                )

                            Text(
                                text = animateTextByCharacter(welcomeMessage),
                                style =
                                    LocalTextStyle.current.copy(
                                        brush = Brush.linearGradient(colors = colors)
                                    ),
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
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                            )
                        } else {
                            FilledTonalIconButton(
                                onClick = onProfile,
                                shapes = IconButtonDefaults.shapes(),
                            ) {
                                Icon(
                                    imageVector = selectedProfile.avatar.toImageVector(),
                                    contentDescription = null,
                                )
                            }
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues,
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

@Composable
private fun animateTextByCharacter(
    targetString: String,
    delayDuration: Duration = 20.milliseconds,
): String {
    var displayedString by rememberSaveable(targetString) { mutableStateOf("") }
    var hasAnimated by rememberSaveable(targetString) { mutableStateOf(false) }

    LaunchedEffect(targetString) {
        if (hasAnimated) return@LaunchedEffect

        val iterator = targetString.iterator()
        displayedString = ""
        while (iterator.hasNext()) {
            displayedString += iterator.nextChar()
            delay(delayDuration)
        }

        hasAnimated = true
    }

    return displayedString
}
