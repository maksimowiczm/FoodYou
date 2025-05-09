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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ui.component.ArrowBackIconButton
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.action_include_all_day_meals
import foodyou.app.generated.resources.action_use_time_based_ordering
import foodyou.app.generated.resources.description_action_include_all_day_meals
import foodyou.app.generated.resources.description_time_based_meals_sorting
import foodyou.app.generated.resources.headline_meals
import foodyou.app.generated.resources.headline_time_based_ordering
import org.jetbrains.compose.resources.stringResource

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
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(Res.string.action_use_time_based_ordering)
                        )
                    },
                    modifier = Modifier.clickable { toggleTimeBased(!useTimeBasedSorting) },
                    supportingContent = {
                        Text(
                            text = stringResource(
                                Res.string.description_time_based_meals_sorting
                            )
                        )
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
                        Text(
                            text = stringResource(Res.string.action_include_all_day_meals)
                        )
                    },
                    modifier = Modifier.clickable(
                        enabled = useTimeBasedSorting
                    ) {
                        toggleIncludeAllDayMeals(!includeAllDayMeals)
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(
                                Res.string.description_action_include_all_day_meals
                            )
                        )
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

            item {
                HorizontalDivider()
            }

            item {
                ListItem(
                    headlineContent = {
                        Text("Meals settings")
                    },
                    modifier = Modifier.clickable { onMealsSettings() }
                )
            }
        }
    }
}
