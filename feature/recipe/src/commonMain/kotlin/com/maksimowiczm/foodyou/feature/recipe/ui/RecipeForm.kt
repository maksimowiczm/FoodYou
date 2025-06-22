package com.maksimowiczm.foodyou.feature.recipe.ui

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.ui.component.IncompleteFoodData
import com.maksimowiczm.foodyou.core.ui.component.IncompleteFoodsList
import com.maksimowiczm.foodyou.core.ui.nutrition.NutritionFactsList
import com.maksimowiczm.foodyou.core.ui.simpleform.FormField
import com.maksimowiczm.foodyou.feature.recipe.domain.Ingredient
import com.maksimowiczm.foodyou.feature.recipe.domain.nutritionFacts
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun RecipeForm(
    ingredients: List<Ingredient>,
    onAddIngredient: () -> Unit,
    onIngredientClick: (index: Int) -> Unit,
    onEditFood: (FoodId) -> Unit,
    formState: RecipeFormState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    val layoutDirection = LocalLayoutDirection.current

    val horizontalPadding = PaddingValues(
        start = contentPadding.calculateStartPadding(layoutDirection),
        end = contentPadding.calculateStartPadding(layoutDirection)
    )

    val verticalPadding = PaddingValues(
        top = contentPadding.calculateTopPadding(),
        bottom = contentPadding.calculateBottomPadding()
    )

    Column(
        modifier = modifier.padding(verticalPadding)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(Res.string.headline_general),
                modifier = Modifier.padding(horizontalPadding),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )

            NameTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontalPadding),
                state = formState.nameState
            )

            ServingsTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontalPadding),
                state = formState.servingsState
            )

            NoteTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontalPadding),
                state = formState.noteState
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { formState.isLiquid = !formState.isLiquid }
                    .padding(vertical = 8.dp)
                    .padding(horizontalPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center,
                    content = {
                        Checkbox(
                            checked = formState.isLiquid,
                            onCheckedChange = null
                        )
                    }
                )
                Column {
                    Text(
                        text = stringResource(Res.string.action_treat_as_liquid),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(Res.string.description_treat_as_liquid),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        HorizontalDivider()

        Text(
            text = stringResource(Res.string.headline_ingredients),
            modifier = Modifier
                .padding(horizontalPadding)
                .padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge
        )

        Column {
            AddIngredientButton(
                onAddIngredient = onAddIngredient,
                contentPadding = horizontalPadding,
                modifier = Modifier.fillMaxWidth()
            )

            ingredients.forEachIndexed { index, it ->
                key(it.uniqueId) {
                    IngredientListItem(
                        ingredient = it,
                        onClick = { onIngredientClick(index) }
                    )
                }
            }
        }

        if (ingredients.isNotEmpty()) {
            HorizontalDivider()

            Text(
                text = stringResource(Res.string.headline_summary),
                modifier = Modifier
                    .padding(horizontalPadding)
                    .padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )

            NutritionFactsList(
                facts = ingredients.nutritionFacts(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontalPadding)
            )

            val incompleteIngredients = IncompleteFoodData.fromFoodList(ingredients.map { it.food })

            if (incompleteIngredients.isNotEmpty()) {
                IncompleteFoodsList(
                    foods = incompleteIngredients,
                    onFoodClick = onEditFood,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontalPadding)
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun NameTextField(
    state: FormField<String, RecipeFormFieldError>,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        modifier = modifier,
        state = state.textFieldState,
        label = {
            Text(stringResource(Res.string.product_name))
        },
        supportingText = {
            Text(stringResource(Res.string.neutral_required))
        },
        isError = state.error != null,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next
        )
    )
}

@Composable
private fun ServingsTextField(
    state: FormField<Int, RecipeFormFieldError>,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        modifier = modifier,
        state = state.textFieldState,
        label = {
            Text(stringResource(Res.string.recipe_servings))
        },
        supportingText = {
            val error = state.error

            if (error != null) {
                Text(error.stringResource())
            } else {
                Text(stringResource(Res.string.description_recipe_servings))
            }
        },
        isError = state.error != null,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        )
    )
}

@Composable
private fun NoteTextField(state: FormField<String, Nothing>, modifier: Modifier = Modifier) {
    OutlinedTextField(
        modifier = modifier,
        state = state.textFieldState,
        label = {
            Text(stringResource(Res.string.headline_note))
        },
        supportingText = {
            Text(stringResource(Res.string.description_add_note))
        },
        isError = false
    )
}

@Composable
private fun AddIngredientButton(
    onAddIngredient: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable { onAddIngredient() }
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledTonalIconButton(
            onClick = onAddIngredient,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clearAndSetSemantics { }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
        }

        Spacer(Modifier.width(8.dp))

        Text(
            text = stringResource(Res.string.action_add_ingredient),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
