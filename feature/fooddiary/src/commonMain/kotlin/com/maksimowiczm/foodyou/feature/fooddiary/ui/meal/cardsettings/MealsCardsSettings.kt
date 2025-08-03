package com.maksimowiczm.foodyou.feature.fooddiary.ui.meal.cardsettings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.preferences.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.preferences.getBlocking
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.core.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.ext.add
import com.maksimowiczm.foodyou.core.ui.ext.performToggle
import com.maksimowiczm.foodyou.feature.fooddiary.preferences.IgnoreAllDayMeals
import com.maksimowiczm.foodyou.feature.fooddiary.preferences.MealsCardsLayout
import com.maksimowiczm.foodyou.feature.fooddiary.preferences.MealsCardsLayoutPreference
import com.maksimowiczm.foodyou.feature.fooddiary.preferences.UseTimeBasedSorting
import foodyou.app.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun MealsCardsSettings(
    onBack: () -> Unit,
    onMealSettings: () -> Unit,
    modifier: Modifier = Modifier,
    layoutPreference: MealsCardsLayoutPreference = userPreference(),
    useTimeBasedSortingPreference: UseTimeBasedSorting = userPreference(),
    ignoreAllDayMealsPreference: IgnoreAllDayMeals = userPreference()
) {
    val coroutineScope = rememberCoroutineScope()

    val useTimeBasedSorting = useTimeBasedSortingPreference
        .collectAsStateWithLifecycle(useTimeBasedSortingPreference.getBlocking()).value

    val ignoreAllDayMeals = ignoreAllDayMealsPreference
        .collectAsStateWithLifecycle(ignoreAllDayMealsPreference.getBlocking()).value

    val layout = layoutPreference.collectAsStateWithLifecycle(layoutPreference.getBlocking()).value

    MealCardSettings(
        layout = layout,
        onLayoutChange = {
            coroutineScope.launch {
                layoutPreference.set(it)
            }
        },
        useTimeBasedSorting = useTimeBasedSorting,
        toggleTimeBased = {
            coroutineScope.launch {
                useTimeBasedSortingPreference.set(it)
            }
        },
        ignoreAllDayMeals = ignoreAllDayMeals,
        toggleIgnoreAllDayMeals = {
            coroutineScope.launch {
                ignoreAllDayMealsPreference.set(it)
            }
        },
        onMealsSettings = onMealSettings,
        onBack = onBack,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun MealCardSettings(
    layout: MealsCardsLayout,
    onLayoutChange: (MealsCardsLayout) -> Unit,
    useTimeBasedSorting: Boolean,
    toggleTimeBased: (Boolean) -> Unit,
    ignoreAllDayMeals: Boolean,
    toggleIgnoreAllDayMeals: (Boolean) -> Unit,
    onMealsSettings: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            MediumFlexibleTopAppBar(
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
            contentPadding = paddingValues.add(vertical = 8.dp)
        ) {
            item {
                LayoutPicker(
                    layout = layout,
                    onLayoutChange = {
                        if (layout != it) {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOn)
                            onLayoutChange(it)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
            }

            advancedLayoutSettings(
                useTimeBasedSorting = useTimeBasedSorting,
                toggleTimeBased = {
                    hapticFeedback.performToggle(it)
                    toggleTimeBased(it)
                },
                ignoreAllDayMeals = ignoreAllDayMeals,
                toggleIgnoreAllDayMeals = {
                    hapticFeedback.performToggle(it)
                    toggleIgnoreAllDayMeals(it)
                }
            )

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

private fun LazyListScope.advancedLayoutSettings(
    useTimeBasedSorting: Boolean,
    toggleTimeBased: (Boolean) -> Unit,
    ignoreAllDayMeals: Boolean,
    toggleIgnoreAllDayMeals: (Boolean) -> Unit
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
                Text(stringResource(Res.string.action_ignore_all_day_meals))
            },
            modifier = Modifier.clickable(
                enabled = useTimeBasedSorting
            ) {
                toggleIgnoreAllDayMeals(!ignoreAllDayMeals)
            },
            supportingContent = {
                Text(stringResource(Res.string.description_action_ignore_all_day_meals))
            },
            trailingContent = {
                Switch(
                    checked = ignoreAllDayMeals,
                    onCheckedChange = toggleIgnoreAllDayMeals,
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
