package com.maksimowiczm.foodyou.feature.meal.ui.cardsettings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
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
import com.maksimowiczm.foodyou.feature.meal.data.MealCardsLayout
import com.maksimowiczm.foodyou.feature.meal.data.MealPreferences
import com.maksimowiczm.foodyou.feature.meal.data.collectMealCardsLayout
import com.maksimowiczm.foodyou.feature.meal.data.setMealCardsLayout
import foodyou.app.generated.resources.*
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

    val layout = dataStore.collectMealCardsLayout().value

    MealCardSettings(
        layout = layout,
        onLayoutChange = coroutineScope.lambda<MealCardsLayout> {
            dataStore.setMealCardsLayout(it)
        },
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
    layout: MealCardsLayout,
    onLayoutChange: (MealCardsLayout) -> Unit,
    useTimeBasedSorting: Boolean,
    toggleTimeBased: (Boolean) -> Unit,
    includeAllDayMeals: Boolean,
    toggleIncludeAllDayMeals: (Boolean) -> Unit,
    onMealsSettings: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current
    val onLayoutChange: (MealCardsLayout) -> Unit = {
        if (layout != it) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOn)
            onLayoutChange(it)
        }
    }

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
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LayoutContainer(
                        onLayoutChange = { onLayoutChange(MealCardsLayout.Horizontal) }
                    ) {
                        LayoutHorizontal()
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = stringResource(Res.string.headline_horizontal),
                            style = MaterialTheme.typography.labelLarge
                        )
                        RadioButton(
                            selected = layout == MealCardsLayout.Horizontal,
                            onClick = { onLayoutChange(MealCardsLayout.Horizontal) }
                        )
                    }

                    LayoutContainer(
                        onLayoutChange = { onLayoutChange(MealCardsLayout.Vertical) }
                    ) {
                        LayoutVertical()
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = stringResource(Res.string.headline_vertical),
                            style = MaterialTheme.typography.labelLarge
                        )
                        RadioButton(
                            selected = layout == MealCardsLayout.Vertical,
                            onClick = { onLayoutChange(MealCardsLayout.Vertical) }
                        )
                    }
                }
            }

            if (layout == MealCardsLayout.Horizontal) {
                item {
                    HorizontalDivider()
                }

                horizontalLayoutSettings(
                    useTimeBasedSorting = useTimeBasedSorting,
                    toggleTimeBased = {
                        hapticFeedback.performToggle(it)
                        toggleTimeBased(it)
                    },
                    includeAllDayMeals = includeAllDayMeals,
                    toggleIncludeAllDayMeals = {
                        hapticFeedback.performToggle(it)
                        toggleIncludeAllDayMeals(it)
                    }
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

@Composable
private fun LayoutContainer(
    onLayoutChange: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable { onLayoutChange() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content()
    }
}

private fun LazyListScope.horizontalLayoutSettings(
    useTimeBasedSorting: Boolean,
    toggleTimeBased: (Boolean) -> Unit,
    includeAllDayMeals: Boolean,
    toggleIncludeAllDayMeals: (Boolean) -> Unit
) {
    item {
        Text(
            text = stringResource(Res.string.headline_time_based_ordering),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }

    item {
        ListItem(
            headlineContent = {
                Text(stringResource(Res.string.action_use_time_based_ordering))
            },
            modifier = Modifier.clickable { toggleTimeBased(!useTimeBasedSorting) },
            supportingContent = {
                Text(stringResource(Res.string.description_time_based_meals_sorting))
            },
            trailingContent = {
                Switch(
                    checked = useTimeBasedSorting,
                    onCheckedChange = toggleTimeBased
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

        ListItem(
            headlineContent = {
                Text(stringResource(Res.string.action_include_all_day_meals))
            },
            modifier = Modifier.clickable(
                enabled = useTimeBasedSorting
            ) {
                toggleIncludeAllDayMeals(!includeAllDayMeals)
            },
            supportingContent = {
                Text(stringResource(Res.string.description_action_include_all_day_meals))
            },
            trailingContent = {
                Switch(
                    checked = includeAllDayMeals,
                    onCheckedChange = toggleIncludeAllDayMeals,
                    enabled = useTimeBasedSorting
                )
            },
            colors = ListItemDefaults.colors(
                headlineColor = contentColor,
                supportingColor = contentColor
            )
        )
    }
}
