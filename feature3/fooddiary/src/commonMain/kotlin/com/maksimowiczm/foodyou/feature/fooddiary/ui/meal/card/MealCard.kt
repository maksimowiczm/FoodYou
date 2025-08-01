package com.maksimowiczm.foodyou.feature.fooddiary.ui.meal.card

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.maksimowiczm.foodyou.core.ui.FoodYouHomeCard
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.core.ui.theme.LocalNutrientsPalette
import com.maksimowiczm.foodyou.core.ui.utils.LocalDateFormatter
import com.maksimowiczm.foodyou.feature.food.preferences.NutrientsOrder
import com.maksimowiczm.foodyou.feature.fooddiary.domain.FoodWithMeasurement
import com.maksimowiczm.foodyou.feature.fooddiary.domain.Meal
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun MealCard(
    meal: Meal,
    order: List<NutrientsOrder>,
    onAddFood: () -> Unit,
    onEditMeasurement: (Long) -> Unit,
    onDeleteEntry: (Long) -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val nutrientsPalette = LocalNutrientsPalette.current
    val dateFormatter = LocalDateFormatter.current
    val enDash = stringResource(Res.string.en_dash)
    val allDayString = stringResource(Res.string.headline_all_day)

    val timeString = remember(dateFormatter, meal, enDash, allDayString) {
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

    FoodYouHomeCard(
        modifier = modifier,
        onClick = onAddFood,
        onLongClick = onLongClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = meal.name,
                style = MaterialTheme.typography.headlineMediumEmphasized,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = timeString,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            FoodContainer(
                food = meal.food,
                onEditMeasurement = onEditMeasurement,
                onDeleteEntry = onDeleteEntry,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .animateContentSize(MaterialTheme.motionScheme.defaultSpatialSpec())
            )

            AnimatedVisibility(
                visible = meal.food.isNotEmpty(),
                enter = expandVertically(
                    animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
                ),
                exit = shrinkVertically(
                    animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
                )
            ) {
                Spacer(Modifier.height(16.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ValueColumn(
                    label = stringResource(Res.string.unit_kcal),
                    value = meal.energy.roundToInt().toString(),
                    suffix = null,
                    color = MaterialTheme.colorScheme.onSurface
                )

                order.forEach { field ->
                    when (field) {
                        NutrientsOrder.Proteins -> ValueColumn(
                            label = stringResource(Res.string.nutriment_proteins_short),
                            value = meal.proteins.formatClipZeros("%.1f"),
                            suffix = stringResource(Res.string.unit_gram_short),
                            color = nutrientsPalette.proteinsOnSurfaceContainer
                        )

                        NutrientsOrder.Carbohydrates -> ValueColumn(
                            label = stringResource(Res.string.nutriment_carbohydrates_short),
                            value = meal.carbohydrates.formatClipZeros("%.1f"),
                            suffix = stringResource(Res.string.unit_gram_short),
                            color = nutrientsPalette.carbohydratesOnSurfaceContainer
                        )

                        NutrientsOrder.Fats -> ValueColumn(
                            label = stringResource(Res.string.nutriment_fats_short),
                            value = meal.fats.formatClipZeros("%.1f"),
                            suffix = stringResource(Res.string.unit_gram_short),
                            color = nutrientsPalette.fatsOnSurfaceContainer
                        )

                        NutrientsOrder.Other,
                        NutrientsOrder.Vitamins,
                        NutrientsOrder.Minerals -> Unit
                    }
                }

                Spacer(Modifier.weight(1f))

                FilledIconButton(
                    onClick = onAddFood,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(Res.string.action_add)
                    )
                }
            }
        }
    }
}

@Composable
private fun FoodContainer(
    food: List<FoodWithMeasurement>,
    onEditMeasurement: (Long) -> Unit,
    onDeleteEntry: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        food.forEachIndexed { i, foodWithMeasurement ->
            key(foodWithMeasurement.measurementId) {
                val topStart = food.animateTopCornerRadius(i)
                val topEnd = food.animateTopCornerRadius(i)
                val bottomStart = food.animateBottomCornerRadius(i)
                val bottomEnd = food.animateBottomCornerRadius(i)
                val shape = RoundedCornerShape(topStart, topEnd, bottomStart, bottomEnd)

                FoodContainerItem(
                    foodWithMeasurement = foodWithMeasurement,
                    onEditMeasurement = onEditMeasurement,
                    onDeleteEntry = onDeleteEntry,
                    shape = shape
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun <T> List<T>.animateTopCornerRadius(index: Int, defaultRadius: Dp = 12.dp): Dp =
    animateDpAsState(
        targetValue = when (index) {
            0 -> defaultRadius
            else -> 0.dp
        },
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
    ).value.coerceAtLeast(0.dp)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun <T> List<T>.animateBottomCornerRadius(index: Int, defaultRadius: Dp = 12.dp): Dp =
    animateDpAsState(
        targetValue = when (index) {
            lastIndex -> defaultRadius
            else -> 0.dp
        },
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
    ).value.coerceAtLeast(0.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FoodContainerItem(
    foodWithMeasurement: FoodWithMeasurement,
    onEditMeasurement: (Long) -> Unit,
    onDeleteEntry: (Long) -> Unit,
    shape: Shape,
    modifier: Modifier = Modifier
) {
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    if (showBottomSheet) {
        val sheetState = rememberModalBottomSheetState()

        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            BottomSheetContent(
                food = foodWithMeasurement,
                onEdit = {
                    coroutineScope.launch {
                        onEditMeasurement(foodWithMeasurement.measurementId)
                        sheetState.hide()
                        showBottomSheet = false
                    }
                },
                onDelete = {
                    coroutineScope.launch {
                        sheetState.hide()
                        onDeleteEntry(foodWithMeasurement.measurementId)
                        showBottomSheet = false
                    }
                }
            )
        }
    }

    MealFoodListItem(
        foodWithMeasurement = foodWithMeasurement,
        modifier = modifier.clickable { showBottomSheet = true },
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = shape
    )
}

@Composable
private fun ValueColumn(
    label: String,
    value: String,
    suffix: String?,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        CompositionLocalProvider(
            LocalContentColor provides color,
            LocalTextStyle provides MaterialTheme.typography.labelMedium
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = if (value == "0") {
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
    food: FoodWithMeasurement,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteDialog(
            onDismissRequest = { showDeleteDialog = false },
            onDeleteEntry = {
                onDelete()
                showDeleteDialog = false
            }
        )
    }

    Column(
        modifier = modifier
    ) {
        MealFoodListItem(
            foodWithMeasurement = food,
            color = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface,
            shape = RectangleShape
        )
        HorizontalDivider(Modifier.padding(horizontal = 16.dp))
        ListItem(
            headlineContent = {
                Text(stringResource(Res.string.action_edit_entry))
            },
            modifier = Modifier.clickable { onEdit() },
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent
            )
        )
        ListItem(
            headlineContent = {
                Text(stringResource(Res.string.action_delete_entry))
            },
            modifier = Modifier.clickable { showDeleteDialog = true },
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null
                )
            },
            colors = ListItemDefaults.colors(
                headlineColor = MaterialTheme.colorScheme.error,
                leadingIconColor = MaterialTheme.colorScheme.error,
                containerColor = Color.Transparent
            )
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
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(stringResource(Res.string.action_delete))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(Res.string.action_cancel))
            }
        },
        title = {
            Text(stringResource(Res.string.action_delete_entry))
        },
        text = {
            Text(stringResource(Res.string.description_delete_product_entry))
        }
    )
}
