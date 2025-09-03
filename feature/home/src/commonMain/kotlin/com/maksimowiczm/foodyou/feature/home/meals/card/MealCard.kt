package com.maksimowiczm.foodyou.feature.home.meals.card

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.business.settings.domain.NutrientsOrder
import com.maksimowiczm.foodyou.feature.home.shared.FoodYouHomeCard
import com.maksimowiczm.foodyou.feature.shared.ui.LocalNutrientsOrder
import com.maksimowiczm.foodyou.shared.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.shared.ui.theme.LocalNutrientsPalette
import com.maksimowiczm.foodyou.shared.ui.utils.LocalDateFormatter
import com.maksimowiczm.foodyou.shared.ui.utils.LocalEnergyFormatter
import foodyou.app.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun MealCard(
    meal: MealModel,
    onAddFood: () -> Unit,
    onQuickAdd: () -> Unit,
    onEditEntry: (MealEntryModel) -> Unit,
    onDeleteEntry: (MealEntryModel) -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val nutrientsPalette = LocalNutrientsPalette.current
    val nutrientsOrder = LocalNutrientsOrder.current
    val dateFormatter = LocalDateFormatter.current
    val energyFormatter = LocalEnergyFormatter.current
    val enDash = stringResource(Res.string.en_dash)
    val allDayString = stringResource(Res.string.headline_all_day)

    val timeString =
        remember(dateFormatter, meal, enDash, allDayString) {
            if (meal.isAllDay) {
                allDayString
            } else {
                buildString {
                    append(dateFormatter.formatTime(meal.from))
                    append(" $enDash ")
                    append(dateFormatter.formatTime(meal.to))
                }
            }
        }

    FoodYouHomeCard(modifier = modifier, onClick = onAddFood, onLongClick = onLongClick) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                text = meal.name,
                style = MaterialTheme.typography.headlineMediumEmphasized,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = timeString,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(16.dp))

            FoodContainer(
                foods = meal.foods,
                onEditEntry = onEditEntry,
                onDeleteEntry = onDeleteEntry,
                modifier =
                    Modifier.fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .animateContentSize(MaterialTheme.motionScheme.defaultSpatialSpec()),
            )

            AnimatedVisibility(
                visible = meal.foods.isNotEmpty(),
                enter =
                    expandVertically(
                        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
                    ),
                exit =
                    shrinkVertically(
                        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
                    ),
            ) {
                Spacer(Modifier.height(16.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    ValueColumn(
                        label = energyFormatter.suffix(),
                        value = energyFormatter.formatEnergy(meal.energy, withSuffix = false),
                        suffix = null,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    nutrientsOrder.forEach { field ->
                        when (field) {
                            NutrientsOrder.Proteins ->
                                ValueColumn(
                                    label = stringResource(Res.string.nutriment_proteins_short),
                                    value = meal.proteins.formatClipZeros("%.1f"),
                                    suffix = stringResource(Res.string.unit_gram_short),
                                    color = nutrientsPalette.proteinsOnSurfaceContainer,
                                )

                            NutrientsOrder.Carbohydrates ->
                                ValueColumn(
                                    label =
                                        stringResource(Res.string.nutriment_carbohydrates_short),
                                    value = meal.carbohydrates.formatClipZeros("%.1f"),
                                    suffix = stringResource(Res.string.unit_gram_short),
                                    color = nutrientsPalette.carbohydratesOnSurfaceContainer,
                                )

                            NutrientsOrder.Fats ->
                                ValueColumn(
                                    label = stringResource(Res.string.nutriment_fats_short),
                                    value = meal.fats.formatClipZeros("%.1f"),
                                    suffix = stringResource(Res.string.unit_gram_short),
                                    color = nutrientsPalette.fatsOnSurfaceContainer,
                                )

                            NutrientsOrder.Other,
                            NutrientsOrder.Vitamins,
                            NutrientsOrder.Minerals -> Unit
                        }
                    }
                }

                Spacer(Modifier.weight(1f))
                FilledTonalIconButton(
                    onClick = onQuickAdd,
                    shapes =
                        IconButtonDefaults.shapes(
                            MaterialTheme.shapes.medium,
                            MaterialTheme.shapes.extraSmall,
                        ),
                ) {
                    Icon(imageVector = Icons.Outlined.Bolt, contentDescription = null)
                }
                FilledIconButton(
                    onClick = onAddFood,
                    shapes =
                        IconButtonDefaults.shapes(
                            MaterialTheme.shapes.medium,
                            MaterialTheme.shapes.extraSmall,
                        ),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(Res.string.action_add),
                    )
                }
            }
        }
    }
}

@Composable
private fun FoodContainer(
    foods: List<MealEntryModel>,
    onEditEntry: (MealEntryModel) -> Unit,
    onDeleteEntry: (MealEntryModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        foods.forEachIndexed { i, entry ->
            val key =
                remember(entry) {
                    when (entry) {
                        is FoodMealEntryModel -> entry.id.toString()
                        is ManualMealEntryModel -> entry.id.toString()
                    }
                }

            key(key) {
                val topStart = animateTopCornerRadius(i)
                val topEnd = animateTopCornerRadius(i)
                val bottomStart = foods.animateBottomCornerRadius(i)
                val bottomEnd = foods.animateBottomCornerRadius(i)
                val shape = RoundedCornerShape(topStart, topEnd, bottomStart, bottomEnd)

                FoodContainerItem(
                    entry = entry,
                    onEditEntry = onEditEntry,
                    onDeleteEntry = onDeleteEntry,
                    shape = shape,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun animateTopCornerRadius(index: Int, defaultRadius: Dp = 12.dp): Dp =
    animateDpAsState(
            targetValue =
                when (index) {
                    0 -> defaultRadius
                    else -> 0.dp
                },
            animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
        )
        .value
        .coerceAtLeast(0.dp)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun <T> List<T>.animateBottomCornerRadius(index: Int, defaultRadius: Dp = 12.dp): Dp =
    animateDpAsState(
            targetValue =
                when (index) {
                    lastIndex -> defaultRadius
                    else -> 0.dp
                },
            animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
        )
        .value
        .coerceAtLeast(0.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FoodContainerItem(
    entry: MealEntryModel,
    onEditEntry: (MealEntryModel) -> Unit,
    onDeleteEntry: (MealEntryModel) -> Unit,
    shape: Shape,
    modifier: Modifier = Modifier,
) {
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    if (showBottomSheet) {
        val sheetState = rememberModalBottomSheetState()

        ModalBottomSheet(onDismissRequest = { showBottomSheet = false }, sheetState = sheetState) {
            BottomSheetContent(
                entry = entry,
                onEdit = {
                    coroutineScope.launch {
                        onEditEntry(entry)
                        sheetState.hide()
                        showBottomSheet = false
                    }
                },
                onDelete = {
                    coroutineScope.launch {
                        sheetState.hide()
                        onDeleteEntry(entry)
                        showBottomSheet = false
                    }
                },
            )
        }
    }

    MealFoodListItem(
        entry = entry,
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = shape,
        modifier = modifier.clickable { showBottomSheet = true },
    )
}

@Composable
private fun ValueColumn(
    label: String,
    value: String,
    suffix: String?,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        CompositionLocalProvider(
            LocalContentColor provides color,
            LocalTextStyle provides MaterialTheme.typography.labelMedium,
        ) {
            Text(text = label, style = MaterialTheme.typography.labelMedium)

            Text(
                text =
                    if (value == "0") {
                        stringResource(Res.string.em_dash)
                    } else {
                        value + (suffix?.let { " $suffix" } ?: "")
                    }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheetContent(
    entry: MealEntryModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteDialog(
            onDismissRequest = { showDeleteDialog = false },
            onDeleteEntry = {
                onDelete()
                showDeleteDialog = false
            },
        )
    }

    Column(modifier = modifier) {
        MealFoodListItem(
            entry = entry,
            color = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface,
            shape = RectangleShape,
        )
        HorizontalDivider(Modifier.padding(horizontal = 16.dp))
        ListItem(
            headlineContent = { Text(stringResource(Res.string.action_edit_entry)) },
            modifier = Modifier.clickable { onEdit() },
            leadingContent = { Icon(imageVector = Icons.Default.Edit, contentDescription = null) },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        )
        ListItem(
            headlineContent = { Text(stringResource(Res.string.action_delete_entry)) },
            modifier = Modifier.clickable { showDeleteDialog = true },
            leadingContent = {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            },
            colors =
                ListItemDefaults.colors(
                    headlineColor = MaterialTheme.colorScheme.error,
                    leadingIconColor = MaterialTheme.colorScheme.error,
                    containerColor = Color.Transparent,
                ),
        )
    }
}

@Composable
private fun DeleteDialog(onDismissRequest: () -> Unit, onDeleteEntry: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onDeleteEntry,
                colors =
                    ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
            ) {
                Text(stringResource(Res.string.action_delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(Res.string.action_cancel))
            }
        },
        title = { Text(stringResource(Res.string.action_delete_entry)) },
        text = { Text(stringResource(Res.string.description_delete_product_entry)) },
    )
}
