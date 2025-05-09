package com.maksimowiczm.foodyou.feature.meal.ui.cardsettings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.ext.lambda
import com.maksimowiczm.foodyou.core.ext.observe
import com.maksimowiczm.foodyou.core.ext.set
import com.maksimowiczm.foodyou.core.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.ext.performToggle
import com.maksimowiczm.foodyou.feature.meal.data.MealPreferences
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun MealCardSettings(
    onMealsSettings: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    dataStore: DataStore<Preferences> = koinInject()
) {
    val coroutineScope = rememberCoroutineScope()

    val useTimeBasedSorting = dataStore
        .observe(MealPreferences.timeBasedSorting)
        .collectAsStateWithLifecycle(false).value ?: false

    val includeAllDayMeals = dataStore
        .observe(MealPreferences.includeAllDayMeals)
        .collectAsStateWithLifecycle(false).value ?: false

    MealCardSettings(
        useTimeBasedSorting = useTimeBasedSorting,
        toggleTimeBased = coroutineScope.lambda<Boolean> {
            dataStore.set(MealPreferences.timeBasedSorting to it)
        },
        includeAllDayMeals = includeAllDayMeals,
        toggleIncludeAllDayMeals = coroutineScope.lambda<Boolean> {
            dataStore.set(MealPreferences.includeAllDayMeals to it)
        },
        onMealsSettings = onMealsSettings,
        onBack = onBack,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealCardSettings(
    useTimeBasedSorting: Boolean,
    toggleTimeBased: (Boolean) -> Unit,
    includeAllDayMeals: Boolean,
    toggleIncludeAllDayMeals: (Boolean) -> Unit,
    onMealsSettings: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(Res.string.headline_meals)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues
        ) {
            item {
                Text(
                    text = stringResource(Res.string.headline_time_based_ordering),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                val toggle: (Boolean) -> Unit = {
                    hapticFeedback.performToggle(it)
                    toggleTimeBased(it)
                }

                ListItem(
                    headlineContent = {
                        Text(stringResource(Res.string.action_use_time_based_ordering))
                    },
                    modifier = Modifier.clickable { toggle(!useTimeBasedSorting) },
                    supportingContent = {
                        Text(stringResource(Res.string.description_time_based_meals_sorting))
                    },
                    trailingContent = {
                        Switch(
                            checked = useTimeBasedSorting,
                            onCheckedChange = toggle
                        )
                    }
                )
            }

            item {
                val contentColor = if (useTimeBasedSorting) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.outline
                }

                val toggle: (Boolean) -> Unit = {
                    hapticFeedback.performToggle(it)
                    toggleIncludeAllDayMeals(it)
                }

                ListItem(
                    headlineContent = {
                        Text(stringResource(Res.string.action_include_all_day_meals))
                    },
                    modifier = Modifier.clickable(
                        enabled = useTimeBasedSorting
                    ) {
                        toggle(!includeAllDayMeals)
                    },
                    supportingContent = {
                        Text(stringResource(Res.string.description_action_include_all_day_meals))
                    },
                    trailingContent = {
                        Switch(
                            checked = includeAllDayMeals,
                            onCheckedChange = toggle,
                            enabled = useTimeBasedSorting
                        )
                    },
                    colors = ListItemDefaults.colors(
                        headlineColor = contentColor,
                        supportingColor = contentColor
                    )
                )
            }

            item {
                HorizontalDivider()
            }

            item {
                ListItem(
                    headlineContent = {
                        Text(stringResource(Res.string.headline_meals_settings))
                    },
                    modifier = Modifier.clickable { onMealsSettings() }
                )
            }
        }
    }
}
