package com.maksimowiczm.foodyou.core.feature.diary.ui.goalssettings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.R
import com.maksimowiczm.foodyou.core.feature.diary.data.model.defaultGoals
import com.maksimowiczm.foodyou.core.feature.diary.ui.goalssettings.calories.CaloriesGoal
import com.maksimowiczm.foodyou.core.ui.theme.FoodYouTheme
import kotlinx.coroutines.launch
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
            // Okay this is absurd but it will be refactored
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
    dailyGoals: com.maksimowiczm.foodyou.core.feature.diary.data.model.DailyGoals,
    onSave: (com.maksimowiczm.foodyou.core.feature.diary.data.model.DailyGoals) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Surface(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.headline_daily_goals))
                },
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
                scrollBehavior = scrollBehavior
            )

            LazyColumn(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
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
