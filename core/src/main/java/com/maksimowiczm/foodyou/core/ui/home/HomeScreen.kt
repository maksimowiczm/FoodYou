package com.maksimowiczm.foodyou.core.ui.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.R
import com.maksimowiczm.foodyou.core.feature.HomeFeature

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeFeatures: List<HomeFeature>,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    state: HomeState = rememberHomeState()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val contentWindowInsets = ScaffoldDefaults.contentWindowInsets
        .exclude(WindowInsets.systemBars.only(WindowInsetsSides.Bottom))

    val topBarWindowInsets = TopAppBarDefaults.windowInsets
        .add(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.headline_diary)
                    )
                },
                actions = {
                    IconButton(
                        onClick = onSettingsClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.action_go_to_settings)
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                windowInsets = topBarWindowInsets
            )
        },
        contentWindowInsets = contentWindowInsets
    ) { innerPadding ->
        // Don't use LazyColumn because size of items must be known in advance. Use scrollable
        // Column instead. For now it is fine since there aren't many items.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .animateContentSize()
        ) {
            homeFeatures.forEachIndexed { i, feature ->
                feature.card(
                    modifier = if (feature.applyPadding) Modifier.padding(horizontal = 8.dp) else Modifier,
                    homeState = state
                )

                if (i < homeFeatures.size - 1) {
                    Spacer(Modifier.height(8.dp))
                }
            }

            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
        }
    }
}
