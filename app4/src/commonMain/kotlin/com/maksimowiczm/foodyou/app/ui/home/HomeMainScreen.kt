package com.maksimowiczm.foodyou.app.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.headline_welcome_user_message
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeMainScreen(
    selectedProfile: ProfileUiState?,
    onProfile: () -> Unit,
    modifier: Modifier = Modifier.Companion,
) {
    val transition = updateTransition(selectedProfile)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    transition.AnimatedContent(
                        transitionSpec = { fadeIn() togetherWith fadeOut() }
                    ) { profile ->
                        if (profile != null) {
                            Text(
                                stringResource(
                                    Res.string.headline_welcome_user_message,
                                    profile.name,
                                )
                            )
                        }
                    }
                },
                modifier = modifier,
                actions = {
                    transition.AnimatedContent(
                        contentKey = { it != null },
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                    ) { profile ->
                        if (profile != null) {
                            FilledTonalIconButton(
                                onClick = onProfile,
                                shapes = IconButtonDefaults.shapes(),
                            ) {
                                Icon(
                                    imageVector = profile.avatar.toImageVector(),
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
        LazyColumn {}
    }
}
