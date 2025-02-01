package com.maksimowiczm.foodyou.feature.settings.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsHomeScreen(
    onBack: () -> Unit,
    onFoodDatabaseClick: () -> Unit,
    onGoalsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Surface(
        modifier = modifier
    ) {
        Column {
            TopAppBar(
                title = { Text(stringResource(R.string.headline_settings)) },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_go_back)
                        )
                    }
                },
                scrollBehavior = topAppBarScrollBehavior
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
            ) {
                item {
                    ListItem(
                        headlineContent = {
                            Text(
                                text = stringResource(R.string.headline_food_database)
                            )
                        },
                        modifier = Modifier.clickable(onClick = onFoodDatabaseClick),
                        leadingContent = {
                            Icon(
                                painter = painterResource(R.drawable.ic_cloud_download_24),
                                contentDescription = null
                            )
                        },
                        supportingContent = {
                            Text(
                                text = stringResource(R.string.neutral_manage_food_database)
                            )
                        }
                    )
                }
                item {
                    ListItem(
                        headlineContent = {
                            Text(
                                text = stringResource(R.string.headline_daily_goals)
                            )
                        },
                        modifier = Modifier.clickable(onClick = onGoalsClick),
                        leadingContent = {
                            Icon(
                                painter = painterResource(R.drawable.ic_flag_24),
                                contentDescription = null
                            )
                        },
                        supportingContent = {
                            Text(
                                text = stringResource(R.string.neutral_set_your_daily_goals)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun SettingsHomeScreenPreview() {
    FoodYouTheme {
        SettingsHomeScreen(
            onBack = {},
            onFoodDatabaseClick = {},
            onGoalsClick = {}
        )
    }
}
