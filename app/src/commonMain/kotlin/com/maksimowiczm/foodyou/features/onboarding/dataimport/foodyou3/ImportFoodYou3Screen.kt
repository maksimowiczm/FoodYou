package com.maksimowiczm.foodyou.features.onboarding.dataimport.foodyou3

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LunchDining
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.capabilities.theme.PreviewFoodYouTheme
import com.maksimowiczm.foodyou.shared.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.shared.ui.extension.add
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ImportFoodYou3Screen(
    initialState: FoodYou3ImportState?,
    onBack: () -> Unit,
    onImport: (FoodYou3ImportState) -> Unit,
    modifier: Modifier = Modifier,
) {
    var importState by
        rememberSerializable(initialState) {
            mutableStateOf(initialState ?: FoodYou3ImportState())
        }

    ImportFoodYou3Screen(
        importState = importState,
        onImportStateChange = { importState = it },
        onBack = onBack,
        onImport = { onImport(importState) },
        modifier = modifier,
    )
}

@Composable
internal fun ImportFoodYou3Screen(
    importState: FoodYou3ImportState,
    onImportStateChange: (FoodYou3ImportState) -> Unit,
    onBack: () -> Unit,
    onImport: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val fabHeight = 56.dp
    val colors =
        ListItemDefaults.segmentedColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        )

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_import_your_data)) },
                subtitle = {
                    Text(stringResource(Res.string.description_restore_your_data_from_food_you_3))
                },
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            Button(
                onClick = onImport,
                shapes = ButtonDefaults.shapesFor(fabHeight),
                modifier = Modifier.widthIn(min = 250.dp),
                contentPadding = ButtonDefaults.contentPaddingFor(fabHeight),
            ) {
                Text(
                    text = stringResource(Res.string.action_import),
                    style = ButtonDefaults.textStyleFor(fabHeight),
                )
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier.fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues.add(vertical = 8.dp).add(bottom = fabHeight + 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(Res.string.headline_food_diary),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        SegmentedListItem(
                            selected = importState.mealPlan,
                            onClick = {
                                onImportStateChange(
                                    importState.copy(mealPlan = !importState.mealPlan)
                                )
                            },
                            shapes = ListItemDefaults.segmentedShapes(index = 0, count = 2),
                            colors = colors,
                            content = { Text(stringResource(Res.string.headline_meals)) },
                            supportingContent = {
                                Text(stringResource(Res.string.description_your_saved_meals))
                            },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Outlined.Restaurant,
                                    contentDescription = null,
                                )
                            },
                            trailingContent = {
                                Checkbox(
                                    checked = importState.mealPlan,
                                    onCheckedChange = null,
                                )
                            },
                        )
                        SegmentedListItem(
                            selected = importState.diaryEntries,
                            onClick = {
                                onImportStateChange(
                                    importState.copy(diaryEntries = !importState.diaryEntries)
                                )
                            },
                            shapes = ListItemDefaults.segmentedShapes(index = 1, count = 2),
                            colors = colors,
                            content = { Text(stringResource(Res.string.headline_diary_entries)) },
                            supportingContent = {
                                Text(stringResource(Res.string.description_your_food_diary_history))
                            },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarMonth,
                                    contentDescription = null,
                                )
                            },
                            trailingContent = {
                                Checkbox(
                                    checked = importState.diaryEntries,
                                    onCheckedChange = null,
                                )
                            },
                        )
                    }
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(Res.string.headline_your_food),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        SegmentedListItem(
                            selected = importState.yourProducts || importState.yourRecipes,
                            onClick = {
                                onImportStateChange(
                                    importState.copy(yourProducts = !importState.yourProducts)
                                )
                            },
                            enabled = !importState.yourRecipes,
                            shapes = ListItemDefaults.segmentedShapes(index = 0, count = 2),
                            colors = colors,
                            content = { Text(stringResource(Res.string.headline_your_products)) },
                            supportingContent = {
                                Text(
                                    stringResource(
                                        Res.string.description_food_products_created_by_you
                                    )
                                )
                            },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Outlined.LunchDining,
                                    contentDescription = null,
                                )
                            },
                            trailingContent = {
                                Checkbox(
                                    checked = importState.yourProducts || importState.yourRecipes,
                                    onCheckedChange = null,
                                    enabled = !importState.yourRecipes,
                                )
                            },
                        )
                        SegmentedListItem(
                            selected = importState.yourRecipes,
                            onClick = {
                                onImportStateChange(
                                    importState.copy(yourRecipes = !importState.yourRecipes)
                                )
                            },
                            shapes = ListItemDefaults.segmentedShapes(index = 1, count = 2),
                            colors = colors,
                            content = { Text(stringResource(Res.string.headline_your_recipes)) },
                            supportingContent = {
                                Text(stringResource(Res.string.description_recipes_created_by_you))
                            },
                            leadingContent = {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_skillet_filled),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                )
                            },
                            trailingContent = {
                                Checkbox(
                                    checked = importState.yourRecipes,
                                    onCheckedChange = null,
                                )
                            },
                        )
                    }
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(Res.string.headline_external_databases),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        SegmentedListItem(
                            selected = importState.openFoodFacts,
                            onClick = {
                                onImportStateChange(
                                    importState.copy(openFoodFacts = !importState.openFoodFacts)
                                )
                            },
                            shapes = ListItemDefaults.segmentedShapes(index = 0, count = 2),
                            colors = colors,
                            content = { Text(stringResource(Res.string.headline_open_food_facts)) },
                            leadingContent = {
                                Image(
                                    painter = painterResource(Res.drawable.openfoodfacts_logo),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                )
                            },
                            trailingContent = {
                                Checkbox(
                                    checked = importState.openFoodFacts,
                                    onCheckedChange = null,
                                )
                            },
                        )
                        SegmentedListItem(
                            selected = importState.foodDataCentral,
                            onClick = {
                                onImportStateChange(
                                    importState.copy(foodDataCentral = !importState.foodDataCentral)
                                )
                            },
                            shapes = ListItemDefaults.segmentedShapes(index = 1, count = 2),
                            colors = colors,
                            content = {
                                Text(stringResource(Res.string.headline_fooddata_central))
                            },
                            leadingContent = {
                                Image(
                                    painter = painterResource(Res.drawable.usda_logo),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                )
                            },
                            trailingContent = {
                                Checkbox(
                                    checked = importState.foodDataCentral,
                                    onCheckedChange = null,
                                )
                            },
                        )
                    }
                    SegmentedListItem(
                        selected = importState.skipUnusedRemote,
                        onClick = {
                            onImportStateChange(
                                importState.copy(skipUnusedRemote = !importState.skipUnusedRemote)
                            )
                        },
                        shapes = ListItemDefaults.shapes(MaterialTheme.shapes.large),
                        colors =
                            ListItemDefaults.segmentedColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                selectedContainerColor =
                                    MaterialTheme.colorScheme.secondaryContainer,
                                selectedContentColor =
                                    MaterialTheme.colorScheme.onSecondaryContainer,
                            ),
                        content = { Text(stringResource(Res.string.headline_optimize_import)) },
                        supportingContent = {
                            Text(stringResource(Res.string.description_skip_unused_remote_items))
                        },
                        verticalAlignment = Alignment.CenterVertically,
                        trailingContent = {
                            Switch(
                                checked = importState.skipUnusedRemote,
                                onCheckedChange = null,
                            )
                        },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ImportFoodYou3ScreenPreview() {
    PreviewFoodYouTheme {
        ImportFoodYou3Screen(
            importState = FoodYou3ImportState(),
            onImportStateChange = {},
            onBack = {},
            onImport = {},
        )
    }
}
