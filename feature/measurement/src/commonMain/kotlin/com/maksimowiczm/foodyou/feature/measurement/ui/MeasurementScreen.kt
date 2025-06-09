package com.maksimowiczm.foodyou.feature.measurement.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.CallSplit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.CallSplit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.model.Food
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.model.Recipe
import com.maksimowiczm.foodyou.core.model.RecipeIngredient
import com.maksimowiczm.foodyou.core.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.component.FoodErrorListItem
import com.maksimowiczm.foodyou.core.ui.component.FoodListItem
import com.maksimowiczm.foodyou.core.ui.component.IncompleteFoodData
import com.maksimowiczm.foodyou.core.ui.component.IncompleteFoodsList
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.feature.measurement.ui.advanced.AdvancedMeasurementForm
import com.maksimowiczm.foodyou.feature.measurement.ui.advanced.AdvancedMeasurementFormState
import com.maksimowiczm.foodyou.feature.measurement.ui.advanced.AdvancedMeasurementSummary
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun MeasurementScreen(
    state: AdvancedMeasurementFormState,
    food: Food,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onEditFood: () -> Unit,
    onDelete: () -> Unit,
    onIngredientClick: (FoodId) -> Unit,
    onExplode: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    if (showDeleteDialog) {
        DeleteDialog(
            onDismissRequest = { showDeleteDialog = false },
            onDelete = {
                showDeleteDialog = false
                onDelete()
            }
        )
    }

    val measurement = state.measurement ?: Measurement.Gram(100f)
    val weight = measurement.weight(food) ?: 100f

    Scaffold(
        modifier = modifier,
        topBar = {
            MediumFlexibleTopAppBar(
                title = { Text(food.headline) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                actions = {
                    Menu(
                        onEdit = onEditFood,
                        onDelete = { showDeleteDialog = true }
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            LargeExtendedFloatingActionButton(
                modifier = Modifier.animateFloatingActionButton(
                    visible = !animatedVisibilityScope.transition.isRunning && state.isValid,
                    alignment = Alignment.BottomEnd
                ),
                onClick = {
                    if (state.isValid) {
                        onSave()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null
                )
                Spacer(Modifier.width(16.dp))
                Text(stringResource(Res.string.action_save))
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues
        ) {
            item {
                HorizontalDivider()
            }

            item {
                AdvancedMeasurementForm(
                    state = state
                )
            }

            item {
                HorizontalDivider()
            }

            if (food is Recipe) {
                item {
                    Spacer(Modifier.height(8.dp))
                }

                item {
                    Ingredients(
                        recipe = food,
                        weight = weight,
                        onExplode = onExplode
                    )
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
            }

            item {
                val nutritionFacts = food.nutritionFacts * weight / 100f

                AdvancedMeasurementSummary(
                    measurement = measurement,
                    nutritionFacts = nutritionFacts,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            if (food is Recipe) {
                val incompleteIngredients =
                    IncompleteFoodData.fromFoodList(food.ingredients.map { it.food })

                if (incompleteIngredients.isNotEmpty()) {
                    item {
                        IncompleteFoodsList(
                            foods = incompleteIngredients,
                            onFoodClick = onIngredientClick,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(16.dp + 96.dp + 16.dp))
            }
        }
    }
}

@Composable
private fun Menu(onEdit: () -> Unit, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    var expanded by rememberSaveable { mutableStateOf(false) }

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
                    onDelete()
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
private fun Ingredients(
    recipe: Recipe,
    weight: Float,
    onExplode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.headline_ingredients),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = onExplode
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.CallSplit,
                    contentDescription = null
                )
            }
        }

        val fractions = recipe.ingredientFractions()

        recipe.ingredients.forEach { ingredient ->
            val ingredientWeight = weight.times(fractions[ingredient.food.id] ?: 1f)
            val nutritionFacts =
                ingredient.nutritionFacts?.times(ingredientWeight / 100f)
            val measurementString = ingredientWeight.let {
                ingredient.measurementString(ingredientWeight)
            }
            val caloriesString =
                ingredientWeight.let {
                    ingredient.caloriesString(ingredientWeight)
                }

            if (nutritionFacts == null ||
                measurementString == null ||
                caloriesString == null
            ) {
                FoodErrorListItem(ingredient.food.headline)
            } else {
                val g = stringResource(Res.string.unit_gram_short)
                val proteins = nutritionFacts.proteins.value.formatClipZeros("%.1f")
                val carbohydrates =
                    nutritionFacts.carbohydrates.value.formatClipZeros("%.1f")
                val fats = nutritionFacts.fats.value.formatClipZeros("%.1f")

                FoodListItem(
                    name = { Text(ingredient.food.headline) },
                    proteins = { Text("$proteins $g") },
                    carbohydrates = { Text("$carbohydrates $g") },
                    fats = { Text("$fats $g") },
                    calories = { Text(caloriesString) },
                    measurement = { Text(measurementString) }
                )
            }

            HorizontalDivider()
        }
    }
}

@Composable
private fun RecipeIngredient.measurementStringShort(weight: Float): String? = with(measurement) {
    when (this) {
        is Measurement.Package -> {
            val packageWeight = food.totalWeight ?: return null
            val quantity = weight / packageWeight

            stringResource(
                Res.string.x_times_y,
                quantity.formatClipZeros(),
                stringResource(Res.string.product_package)
            )
        }

        is Measurement.Serving -> {
            val servingWeight = food.servingWeight ?: return null
            val quantity = weight / servingWeight

            stringResource(
                Res.string.x_times_y,
                quantity.formatClipZeros(),
                stringResource(Res.string.product_serving)
            )
        }

        is Measurement.Gram -> "${weight.formatClipZeros()} " +
            stringResource(Res.string.unit_gram_short)
    }
}

@Composable
private fun RecipeIngredient.measurementString(weight: Float): String? {
    val short = measurementStringShort(weight) ?: return null
    val weightString = weight.formatClipZeros()

    return when (measurement) {
        is Measurement.Gram -> short
        is Measurement.Package,
        is Measurement.Serving ->
            "$short ($weightString ${stringResource(Res.string.unit_gram_short)})"
    }
}

@Composable
private fun RecipeIngredient.caloriesString(weight: Float): String? {
    val calories = nutritionFacts?.calories?.value?.times(weight / 100f) ?: return null
    return "${calories.roundToInt()} " + stringResource(Res.string.unit_kcal)
}
