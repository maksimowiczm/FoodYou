package com.maksimowiczm.foodyou.feature.home.meals.settings

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.business.opensource.domain.fooddiary.MealsCardsLayout
import com.maksimowiczm.foodyou.app.ui.shared.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.shared.compose.extension.add
import com.maksimowiczm.foodyou.shared.compose.extension.performToggle
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MealsCardsSettingsScreen(
    onBack: () -> Unit,
    onMealSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: MealsCardsSettingsViewModel = koinViewModel()
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()

    val layout = preferences.layout
    val useTimeBasedSorting = preferences.useTimeBasedSorting
    val ignoreAllDayMeals = preferences.ignoreAllDayMeals

    MealCardSettings(
        layout = layout,
        onLayoutChange = { viewModel.updatePreferences(preferences.copy(layout = it)) },
        useTimeBasedSorting = useTimeBasedSorting,
        toggleTimeBased = {
            viewModel.updatePreferences(preferences.copy(useTimeBasedSorting = it))
        },
        ignoreAllDayMeals = ignoreAllDayMeals,
        toggleIgnoreAllDayMeals = {
            viewModel.updatePreferences(preferences.copy(ignoreAllDayMeals = it))
        },
        onMealsSettings = onMealSettings,
        onBack = onBack,
        modifier = modifier,
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
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            MediumFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_meals)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = modifier,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues.add(vertical = 8.dp),
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
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
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
                },
            )

            item { HorizontalDivider() }

            item {
                ListItem(
                    headlineContent = { Text(stringResource(Res.string.headline_meals_settings)) },
                    modifier = Modifier.clickable { onMealsSettings() },
                )
            }
        }
    }
}

private fun LazyListScope.advancedLayoutSettings(
    useTimeBasedSorting: Boolean,
    toggleTimeBased: (Boolean) -> Unit,
    ignoreAllDayMeals: Boolean,
    toggleIgnoreAllDayMeals: (Boolean) -> Unit,
) {
    item {
        Text(
            text = stringResource(Res.string.headline_time_based_ordering),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }

    item {
        ListItem(
            headlineContent = { Text(stringResource(Res.string.action_use_time_based_ordering)) },
            modifier = Modifier.clickable { toggleTimeBased(!useTimeBasedSorting) },
            supportingContent = {
                Text(stringResource(Res.string.description_time_based_meals_sorting))
            },
            trailingContent = {
                Switch(checked = useTimeBasedSorting, onCheckedChange = toggleTimeBased)
            },
        )
    }

    item {
        val contentColor =
            if (useTimeBasedSorting) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.outline
            }

        ListItem(
            headlineContent = { Text(stringResource(Res.string.action_ignore_all_day_meals)) },
            modifier =
                Modifier.clickable(enabled = useTimeBasedSorting) {
                    toggleIgnoreAllDayMeals(!ignoreAllDayMeals)
                },
            supportingContent = {
                Text(stringResource(Res.string.description_action_ignore_all_day_meals))
            },
            trailingContent = {
                Switch(
                    checked = ignoreAllDayMeals,
                    onCheckedChange = toggleIgnoreAllDayMeals,
                    enabled = useTimeBasedSorting,
                )
            },
            colors =
                ListItemDefaults.colors(
                    headlineColor = contentColor,
                    supportingColor = contentColor,
                ),
        )
    }
}
