package com.maksimowiczm.foodyou.ui.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.feature.home.HomeFeature
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    animatedVisibilityScope: AnimatedVisibilityScope,
    homeFeatures: List<HomeFeature>,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    state: HomeState = rememberHomeState()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.headline_diary)
                    )
                },
                actions = {
                    IconButton(
                        onClick = onSettingsClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(Res.string.action_go_to_settings)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = innerPadding
        ) {
            itemsIndexed(
                items = homeFeatures
            ) { i, feature ->
                feature.card(
                    animatedVisibilityScope = animatedVisibilityScope,
                    modifier = if (feature.applyPadding) {
                        Modifier.padding(
                            horizontal = 8.dp
                        )
                    } else {
                        Modifier
                    },
                    homeState = state
                )

                if (i < homeFeatures.size - 1) {
                    Spacer(Modifier.height(8.dp))
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
