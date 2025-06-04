package com.maksimowiczm.foodyou.feature.goals.ui.cardsettings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.ext.getBlocking
import com.maksimowiczm.foodyou.core.ext.lambda
import com.maksimowiczm.foodyou.core.ext.observe
import com.maksimowiczm.foodyou.core.ext.set
import com.maksimowiczm.foodyou.core.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.feature.goals.data.GoalsPreferences
import com.maksimowiczm.foodyou.feature.goals.model.defaultGoals
import com.maksimowiczm.foodyou.feature.goals.ui.card.GoalsCard
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
internal fun GoalsCardSettings(
    onBack: () -> Unit,
    onGoalsSettings: () -> Unit,
    modifier: Modifier = Modifier,
    dataStore: DataStore<Preferences> = koinInject()
) {
    val coroutineScope = rememberCoroutineScope()

    val showDetails = dataStore
        .observe(GoalsPreferences.expandGoalsCard)
        .collectAsStateWithLifecycle(dataStore.getBlocking(GoalsPreferences.expandGoalsCard)).value

    GoalsCardSettings(
        onBack = onBack,
        showDetails = showDetails ?: true,
        onShowDetailsChange = coroutineScope.lambda<Boolean> {
            dataStore.set(GoalsPreferences.expandGoalsCard to it)
        },
        onGoalsSettings = onGoalsSettings,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun GoalsCardSettings(
    onBack: () -> Unit,
    onGoalsSettings: () -> Unit,
    showDetails: Boolean,
    onShowDetailsChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            MediumFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_daily_goals)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues
        ) {
            stickyHeader {
                GoalsCard(
                    expand = showDetails,
                    totalCalories = 1600,
                    totalProteins = 50,
                    totalCarbohydrates = 125,
                    totalFats = 100,
                    dailyGoals = defaultGoals(),
                    onClick = {},
                    onLongClick = {},
                    modifier = Modifier.padding(16.dp)
                )
            }

            item {
                HorizontalDivider()
            }

            item {
                ListItem(
                    headlineContent = {
                        Text(stringResource(Res.string.action_show_details))
                    },
                    modifier = Modifier.clickable { onShowDetailsChange(!showDetails) },
                    supportingContent = {
                        Text(stringResource(Res.string.description_show_macronutrients_goals))
                    },
                    trailingContent = {
                        Switch(
                            checked = showDetails,
                            onCheckedChange = onShowDetailsChange
                        )
                    }
                )
            }

            item {
                HorizontalDivider()
            }

            item {
                ListItem(
                    headlineContent = {
                        Text(stringResource(Res.string.headline_daily_goals_settings))
                    },
                    modifier = Modifier.clickable { onGoalsSettings() }
                )
            }
        }
    }
}
