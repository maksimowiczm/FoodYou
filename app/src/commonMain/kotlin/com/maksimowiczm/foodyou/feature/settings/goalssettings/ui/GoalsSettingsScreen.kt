package com.maksimowiczm.foodyou.feature.settings.goalssettings.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.data.model.DailyGoals
import com.maksimowiczm.foodyou.data.model.defaultGoals
import com.maksimowiczm.foodyou.feature.settings.goalssettings.ui.calories.CaloriesGoal
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import foodyou.app.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.androidx.compose.koinViewModel

@Composable
fun GoalsSettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GoalsSettingsViewModel = koinViewModel()
) {
    val dailyGoals by viewModel.dailyGoals.collectAsStateWithLifecycle()

    GoalsSettingsScreen(
        onBack = onBack,
        dailyGoals = dailyGoals,
        onSave = {
            // Okay this is absurd but it will be refactored TF
            viewModel.viewModelScope.launch {
                viewModel.onSaveDailyGoals(it)
                onBack()
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoalsSettingsScreen(
    onBack: () -> Unit,
    dailyGoals: DailyGoals,
    onSave: (DailyGoals) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val contentWindowInsets = ScaffoldDefaults.contentWindowInsets
        .add(WindowInsets.ime)
        .exclude(WindowInsets.systemBars.only(WindowInsetsSides.Bottom))

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(Res.string.headline_daily_goals))
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.action_go_back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = contentWindowInsets
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            item {
                CaloriesGoal(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        ),
                    goals = dailyGoals,
                    onSave = onSave
                )
            }

            item {
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
            }
        }
    }
}

@PreviewFontScale
@Composable
private fun GoalsSettingsScreenPreview() {
    FoodYouTheme {
        GoalsSettingsScreen(
            onBack = {},
            dailyGoals = defaultGoals(),
            onSave = {}
        )
    }
}
