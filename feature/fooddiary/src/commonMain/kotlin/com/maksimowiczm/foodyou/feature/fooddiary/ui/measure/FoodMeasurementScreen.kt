package com.maksimowiczm.foodyou.feature.fooddiary.ui.measure

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.CallSplit
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ext.minus
import com.maksimowiczm.foodyou.core.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.ext.add
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.core.ui.unorderedList
import com.maksimowiczm.foodyou.core.ui.utils.LocalClipboardManager
import com.maksimowiczm.foodyou.core.ui.utils.LocalDateFormatter
import com.maksimowiczm.foodyou.feature.food.domain.Food
import com.maksimowiczm.foodyou.feature.food.domain.FoodEvent
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.domain.FoodSource
import com.maksimowiczm.foodyou.feature.food.domain.Product
import com.maksimowiczm.foodyou.feature.food.domain.Recipe
import com.maksimowiczm.foodyou.feature.food.domain.RecipeIngredient
import com.maksimowiczm.foodyou.feature.food.domain.weight
import com.maksimowiczm.foodyou.feature.food.ui.EnergyProgressIndicator
import com.maksimowiczm.foodyou.feature.food.ui.FoodErrorListItem
import com.maksimowiczm.foodyou.feature.food.ui.FoodListItem
import com.maksimowiczm.foodyou.feature.food.ui.Icon
import com.maksimowiczm.foodyou.feature.food.ui.IncompleteFoodsList
import com.maksimowiczm.foodyou.feature.food.ui.NutrientList
import com.maksimowiczm.foodyou.feature.food.ui.stringResource
import com.maksimowiczm.foodyou.feature.fooddiary.data.Meal
import com.maksimowiczm.foodyou.feature.measurement.data.Measurement as MeasurementType
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import com.maksimowiczm.foodyou.feature.measurement.ui.MeasurementPicker
import com.maksimowiczm.foodyou.feature.measurement.ui.stringResource
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.days
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun FoodMeasurementScreen(
    onBack: () -> Unit,
    onEditFood: (FoodId) -> Unit,
    onDelete: () -> Unit,
    onMeasure: (Measurement, mealId: Long, LocalDate) -> Unit,
    onUnpack: (Measurement, mealId: Long, LocalDate) -> Unit,
    food: Food,
    foodEvents: List<FoodEvent>?,
    today: LocalDate,
    selectedDate: LocalDate,
    meals: List<Meal>,
    selectedMeal: Meal,
    suggestions: List<Measurement>,
    possibleTypes: Set<MeasurementType>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    selectedMeasurement: Measurement = suggestions.first()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val state = rememberProductMeasurementFormState(
        today = today,
        possibleDates = setOf(selectedDate, today.minus(1.days), today),
        selectedDate = selectedDate,
        meals = meals.toSet(),
        selectedMeal = selectedMeal,
        suggestions = suggestions.toSet(),
        possibleTypes = possibleTypes.toSet(),
        selectedMeasurement = selectedMeasurement
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            MediumTopAppBar(
                title = { Text(food.headline) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                actions = {
                    Menu(
                        onEdit = { onEditFood(food.id) },
                        onDelete = onDelete
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            Column(
                modifier = Modifier.animateFloatingActionButton(
                    visible = !animatedVisibilityScope.transition.isRunning && state.isValid,
                    alignment = Alignment.BottomEnd
                ),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (food is Recipe) {
                    ExtendedFloatingActionButton(
                        onClick = {
                            if (state.isValid) {
                                onUnpack(
                                    state.measurementState.measurement,
                                    meals.first { it.name == state.mealsState.selectedMeal }.id,
                                    state.dateState.selectedDate
                                )
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.CallSplit,
                                contentDescription = null
                            )
                        },
                        text = {
                            Text(stringResource(Res.string.action_unpack))
                        },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                LargeExtendedFloatingActionButton(
                    onClick = {
                        if (state.isValid) {
                            onMeasure(
                                state.measurementState.measurement,
                                meals.first { it.name == state.mealsState.selectedMeal }.id,
                                state.dateState.selectedDate
                            )
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = stringResource(Res.string.action_save)
                        )
                    },
                    text = {
                        Text(stringResource(Res.string.action_save))
                    }
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(horizontal = 8.dp)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues
                .add(vertical = 8.dp)
                .let {
                    if (food is Recipe) {
                        it.add(bottom = 8.dp + 56.dp + 8.dp + 80.dp + 24.dp) // Double FAB
                    } else {
                        it.add(bottom = 80.dp + 24.dp) // FAB
                    }
                }
        ) {
            item {
                HorizontalDivider()
            }

            item {
                FoodMeasurementForm(
                    state = state,
                    modifier = Modifier.padding()
                )
            }

            if (food is Recipe) {
                item {
                    HorizontalDivider()
                    Ingredients(
                        ingredients = remember(food, state.measurementState.measurement) {
                            food.measuredIngredients(
                                state.measurementState.measurement.weight(food)
                            )
                        },
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            item {
                HorizontalDivider()
                NutrientList(
                    food = food,
                    measurement = state.measurementState.measurement,
                    onEditFood = onEditFood
                )
            }

            when (val note = food.note) {
                null -> Unit
                else -> {
                    item {
                        HorizontalDivider()
                        Note(
                            note = note,
                            modifier = Modifier.padding(
                                vertical = 16.dp,
                                horizontal = 8.dp
                            )
                        )
                    }
                }
            }

            if (food is Product) {
                item {
                    HorizontalDivider()

                    val clipboardManger = LocalClipboardManager.current
                    val sourceStr = stringResource(Res.string.headline_source)

                    Source(
                        source = food.source,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    val url = food.source.url
                                    if (url != null) {
                                        clipboardManger.copy(
                                            label = sourceStr,
                                            text = url
                                        )
                                    }
                                }
                            )
                            .padding(
                                vertical = 16.dp,
                                horizontal = 8.dp
                            )
                    )
                }
            }

            if (!foodEvents.isNullOrEmpty()) {
                item {
                    HorizontalDivider()
                    FoodEvents(
                        events = foodEvents,
                        modifier = Modifier.padding(
                            vertical = 16.dp,
                            horizontal = 8.dp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun Menu(onEdit: () -> Unit, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteDialog(
            onDismissRequest = { showDeleteDialog = false },
            onDelete = onDelete
        )
    }

    Box(modifier) {
        IconButton(
            onClick = { expanded = true }
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(Res.string.action_show_more)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.action_edit)) },
                onClick = {
                    expanded = false
                    onEdit()
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.action_delete)) },
                onClick = {
                    expanded = false
                    showDeleteDialog = true
                }
            )
        }
    }
}

@Composable
private fun DeleteDialog(
    onDismissRequest: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onDelete
            ) {
                Text(stringResource(Res.string.action_delete))
            }
        },
        modifier = modifier,
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(Res.string.action_cancel))
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null
            )
        },
        title = {
            Text(stringResource(Res.string.headline_delete_product))
        },
        text = {
            Text(stringResource(Res.string.description_delete_product))
        }
    )
}

@Composable
private fun Note(note: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(
            text = stringResource(Res.string.headline_note),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = note,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun Source(source: FoodSource, modifier: Modifier = Modifier) {
    val url = source.url

    Column(modifier) {
        Text(
            text = stringResource(Res.string.headline_source),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            source.type.Icon()

            Column {
                Text(
                    text = source.type.stringResource(),
                    style = MaterialTheme.typography.bodyMedium
                )
                if (url != null) {
                    Text(
                        text = url,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun Ingredients(ingredients: List<RecipeIngredient>, modifier: Modifier = Modifier) {
    val g = stringResource(Res.string.unit_gram_short)
    val kcal = stringResource(Res.string.unit_kcal)

    val contentPadding = PaddingValues(
        horizontal = 0.dp,
        vertical = 8.dp
    )

    Column(modifier) {
        Text(
            text = stringResource(Res.string.headline_ingredients),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        ingredients.forEach { ingredient ->

            val facts = ingredient.nutritionFacts
            val proteins = facts?.proteins?.value
            val carbs = facts?.carbohydrates?.value
            val fats = facts?.fats?.value
            val energy = facts?.energy?.value

            if (proteins == null || carbs == null || fats == null || energy == null) {
                FoodErrorListItem(
                    headline = ingredient.food.headline,
                    errorMessage = stringResource(Res.string.error_food_is_missing_required_fields),
                    contentPadding = contentPadding
                )
            } else {
                FoodListItem(
                    name = { Text(ingredient.food.headline) },
                    proteins = {
                        val text = proteins.formatClipZeros() + " $g"
                        Text(text)
                    },
                    carbohydrates = {
                        val text = carbs.formatClipZeros() + " $g"
                        Text(text)
                    },
                    fats = {
                        val text = fats.formatClipZeros() + " $g"
                        Text(text)
                    },
                    calories = {
                        val text = energy.roundToInt().toString() + " $kcal"
                        Text(text)
                    },
                    measurement = { Text(ingredient.measurement.stringResource()) },
                    contentPadding = contentPadding
                )
            }
        }
    }
}

@Composable
private fun NutrientList(
    food: Food,
    measurement: Measurement,
    onEditFood: (FoodId) -> Unit,
    modifier: Modifier = Modifier
) {
    val facts = remember(food, measurement) {
        val weight = measurement.weight(food)
            ?: error("Invalid measurement: $measurement for food: ${food.headline}")
        food.nutritionFacts * (weight / 100)
    }

    Column(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ViewList,
                    contentDescription = null
                )
            }

            val proteins = facts.proteins.value
            val carbohydrates = facts.carbohydrates.value
            val fats = facts.fats.value

            if (proteins != null && carbohydrates != null && fats != null) {
                EnergyProgressIndicator(
                    proteins = proteins,
                    carbohydrates = carbohydrates,
                    fats = fats,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        val weight = measurement.weight(food)
            ?: error("Invalid measurement: $measurement for food: ${food.headline}")

        val text = buildString {
            append(measurement.stringResource())

            when (measurement) {
                is Measurement.Gram,
                is Measurement.Milliliter -> Unit

                is Measurement.Package,
                is Measurement.Serving -> {
                    val suffix = if (food.isLiquid) {
                        stringResource(Res.string.unit_milliliter_short)
                    } else {
                        stringResource(Res.string.unit_gram_short)
                    }

                    append(" (${weight.formatClipZeros()} $suffix)")
                }
            }
        }

        Text(
            text = text,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(bottom = 8.dp),
            style = MaterialTheme.typography.labelLarge
        )

        NutrientList(facts)

        if (food is Recipe && !food.nutritionFacts.isComplete) {
            val foods = food
                .flatIngredients()
                .filter { it is Product }
                .filter { !it.nutritionFacts.isComplete }

            IncompleteFoodsList(
                foods = foods.map { it.headline }.distinct(),
                onFoodClick = { name ->
                    val id = foods.firstOrNull {
                        it.headline == name
                    }?.id

                    if (id != null) {
                        onEditFood(id)
                    }
                },
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
private fun FoodMeasurementForm(state: ProductMeasurementFormState, modifier: Modifier = Modifier) {
    Column(modifier) {
        ChipsDatePicker(
            state = state.dateState,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        HorizontalDivider()
        ChipsMealPicker(
            state = state.mealsState,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        HorizontalDivider()
        MeasurementPicker(
            state = state.measurementState,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
private fun FoodEvents(events: List<FoodEvent>, modifier: Modifier = Modifier) {
    val dateFormatter = LocalDateFormatter.current
    val strings = events
        .filterNot { it is FoodEvent.Measured }
        .map {
            it.stringResource() + ", " + dateFormatter.formatDateTime(it.date)
        }
    val list = unorderedList(strings)

    Column(modifier) {
        Text(
            text = stringResource(Res.string.headline_history),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = list,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FoodEvent.stringResource(): String = when (this) {
    is FoodEvent.Created -> stringResource(Res.string.headline_created)
    is FoodEvent.Downloaded -> stringResource(Res.string.headline_downloaded)
    is FoodEvent.Imported -> stringResource(Res.string.headline_imported)
    is FoodEvent.Edited -> stringResource(Res.string.headline_edited)
    is FoodEvent.Measured -> error("FoodEvent.Used should not be displayed in the history")
    is FoodEvent.ImportedFromFoodYou2 ->
        stringResource(Res.string.headline_imported_from_food_you_2)
}
