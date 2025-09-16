package com.maksimowiczm.foodyou.app.ui.food.recipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.shared.component.FoodErrorListItem
import com.maksimowiczm.foodyou.app.ui.shared.component.FoodListItem
import com.maksimowiczm.foodyou.app.ui.shared.component.FoodListItemSkeleton
import com.maksimowiczm.foodyou.app.ui.shared.utility.LocalEnergyFormatter
import com.maksimowiczm.foodyou.app.ui.shared.utility.stringResourceWithWeight
import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.food.domain.usecase.ObserveFoodUseCase
import com.maksimowiczm.foodyou.shared.compose.extension.add
import com.maksimowiczm.foodyou.shared.compose.form.FormField
import com.maksimowiczm.foodyou.shared.compose.utility.formatClipZeros
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
internal fun RecipeForm(
    state: RecipeFormState,
    onAddIngredient: () -> Unit,
    onEditIngredient: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val layoutDirection = LocalLayoutDirection.current

    val horizontalPadding =
        PaddingValues(
            start = contentPadding.calculateStartPadding(layoutDirection),
            end = contentPadding.calculateStartPadding(layoutDirection),
        )

    val verticalPadding =
        PaddingValues(
            top = contentPadding.calculateTopPadding(),
            bottom = contentPadding.calculateBottomPadding(),
        )

    var ingredientToRemoveIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    LaunchedEffect(state.ingredients) { ingredientToRemoveIndex = null }
    when (val index = ingredientToRemoveIndex) {
        null -> Unit
        else ->
            AlertDialog(
                onDismissRequest = { ingredientToRemoveIndex = null },
                confirmButton = {
                    TextButton(
                        onClick = {
                            state.removeIngredient(state.ingredients[index])
                            ingredientToRemoveIndex = null
                        }
                    ) {
                        Text(stringResource(Res.string.action_delete))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { ingredientToRemoveIndex = null }) {
                        Text(stringResource(Res.string.action_cancel))
                    }
                },
                title = { Text(stringResource(Res.string.action_delete_ingredient)) },
                text = { Text(stringResource(Res.string.description_delete_ingredient)) },
            )
    }

    Column(modifier = modifier.padding(verticalPadding)) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(Res.string.headline_general),
                modifier = Modifier.padding(horizontalPadding),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
            )

            state.name.TextField(
                label = stringResource(Res.string.product_name),
                modifier = Modifier.fillMaxWidth().padding(horizontalPadding),
                supportingText = stringResource(Res.string.neutral_required),
            )

            state.servings.TextField(
                label = stringResource(Res.string.recipe_servings),
                modifier = Modifier.fillMaxWidth().padding(horizontalPadding),
                imeAction = ImeAction.Next,
                supportingText = stringResource(Res.string.description_recipe_servings),
            )

            state.note.TextField(
                label = stringResource(Res.string.headline_note),
                modifier = Modifier.fillMaxWidth().padding(horizontalPadding),
                imeAction = ImeAction.Done,
                supportingText = stringResource(Res.string.description_add_note),
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth()
                        .clickable { state.isLiquid = !state.isLiquid }
                        .padding(vertical = 8.dp)
                        .padding(horizontalPadding),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center,
                    content = { Checkbox(checked = state.isLiquid, onCheckedChange = null) },
                )
                Column {
                    Text(
                        text = stringResource(Res.string.action_treat_as_liquid),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = stringResource(Res.string.description_treat_as_liquid),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))

        Text(
            text = stringResource(Res.string.headline_ingredients),
            modifier = Modifier.padding(horizontalPadding).padding(bottom = 8.dp),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
        )

        AddIngredientButton(
            onAddIngredient = onAddIngredient,
            contentPadding = horizontalPadding,
            modifier = Modifier.fillMaxWidth(),
        )

        val shimmer = rememberShimmer(ShimmerBounds.View)
        state.ingredients.forEachIndexed { i, ingredient ->
            IngredientListItem(
                ingredient = ingredient,
                onEdit = { onEditIngredient(i) },
                onDelete = { ingredientToRemoveIndex = i },
                contentPadding = horizontalPadding.add(vertical = 12.dp),
                shimmer = shimmer,
            )
        }
    }
}

@Composable
private fun IngredientListItem(
    ingredient: MinimalIngredient,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    contentPadding: PaddingValues,
    shimmer: Shimmer,
    modifier: Modifier = Modifier,
) {
    val observeFoodUseCase: ObserveFoodUseCase = koinInject()

    val food = observeFoodUseCase.observe(ingredient.foodId).collectAsStateWithLifecycle(null).value

    if (food == null) {
        return FoodListItemSkeleton(shimmer, modifier)
    }

    val factor = food.weight(ingredient.measurement)?.div(100)
    if (factor == null) {
        return FoodErrorListItem(
            headline = food.headline,
            errorMessage = stringResource(Res.string.error_measurement_error),
            modifier = modifier,
            onClick = onEdit,
        )
    }

    val measurementFacts = food.nutritionFacts * factor
    val proteins = measurementFacts.proteins.value
    val carbohydrates = measurementFacts.carbohydrates.value
    val fats = measurementFacts.fats.value
    val energy = measurementFacts.energy.value
    val measurementString =
        ingredient.measurement.stringResourceWithWeight(
            totalWeight = food.totalWeight,
            servingWeight = food.servingWeight,
            isLiquid = food.isLiquid,
        )

    if (
        proteins == null ||
            carbohydrates == null ||
            fats == null ||
            energy == null ||
            measurementString == null
    ) {
        return FoodErrorListItem(
            headline = food.headline,
            modifier = modifier,
            onClick = onEdit,
            errorMessage = stringResource(Res.string.error_food_is_missing_required_fields),
        )
    }

    val g = stringResource(Res.string.unit_gram_short)

    FoodListItem(
        name = { Text(text = food.headline) },
        proteins = {
            val text = proteins.formatClipZeros()
            Text("$text $g")
        },
        carbohydrates = {
            val text = carbohydrates.formatClipZeros()
            Text("$text $g")
        },
        fats = {
            val text = fats.formatClipZeros()
            Text("$text $g")
        },
        calories = { Text(LocalEnergyFormatter.current.formatEnergy(energy.roundToInt())) },
        measurement = { Text(measurementString) },
        modifier = modifier,
        contentPadding = contentPadding,
        trailingContent = {
            Row {
                IconButton(onEdit) {
                    Icon(imageVector = Icons.Outlined.Edit, contentDescription = null)
                }
                IconButton(onDelete) {
                    Icon(imageVector = Icons.Outlined.Delete, contentDescription = null)
                }
            }
        },
        isRecipe = ingredient.foodId is FoodId.Recipe,
    )
}

@Composable
private inline fun <reified T> FormField<T, RecipeFormFieldError>.TextField(
    label: String,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next,
    supportingText: String? = null,
) {
    OutlinedTextField(
        state = textFieldState,
        modifier = modifier,
        label = { Text(label) },
        supportingText = {
            val error = this.error
            if (error != null) {
                Text(error.stringResource())
            } else if (supportingText != null) {
                Text(supportingText)
            }
        },
        isError = error != null,
        keyboardOptions =
            if (T::class == Int::class) {
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = imeAction)
            } else {
                KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = imeAction)
            },
    )
}

@Composable
private fun AddIngredientButton(
    onAddIngredient: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .clickable { onAddIngredient() }
                .padding(contentPadding)
                .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(40.dp).padding(8.dp),
            )
        }

        Spacer(Modifier.width(12.dp))

        Text(
            text = stringResource(Res.string.action_add_ingredient),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}
