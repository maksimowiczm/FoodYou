package com.maksimowiczm.foodyou.feature.diary.ui.recipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.ext.sumOf
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductWithMeasurement
import com.maksimowiczm.foodyou.feature.diary.data.model.RecipeIngredient
import com.maksimowiczm.foodyou.feature.diary.ui.component.CaloriesProgressIndicator
import com.maksimowiczm.foodyou.feature.diary.ui.component.ListItem
import com.maksimowiczm.foodyou.feature.diary.ui.component.NutrientsList
import com.maksimowiczm.foodyou.ui.res.formatClipZeros
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRecipeDialog(
    onClose: () -> Unit,
    onAddIngredient: () -> Unit,
    onEditIngredient: (RecipeIngredient) -> Unit,
    modifier: Modifier = Modifier,
    createRecipeViewModel: CreateRecipeDialogViewModel = koinViewModel()
) {
    val ingredients by createRecipeViewModel.ingredients.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val topBar = @Composable {
        TopAppBar(
            title = { Text("Create Recipe") },
            navigationIcon = {
                IconButton(
                    onClick = onClose
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.action_go_back)
                    )
                }
            },
            actions = {
                TextButton(
                    onClick = {
                        // TODO
                    }
                ) {
                    Text(stringResource(Res.string.action_create))
                }
            },
            scrollBehavior = scrollBehavior
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = topBar
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            item {
                Text(
                    text = stringResource(Res.string.headline_general),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                Spacer(Modifier.height(8.dp))
            }

            item {
                GeneralSection(
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
            }

            item {
                Text(
                    text = "Ingredients",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                Spacer(Modifier.height(8.dp))
            }

            ingredientsSection(
                onAddIngredient = onAddIngredient,
                ingredients = ingredients,
                onIngredientClick = onEditIngredient
            )

            item {
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
            }

            item {
                Text(
                    text = "Summary",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                Spacer(Modifier.height(8.dp))
            }

            item {
                SummarySection(
                    ingredients = ingredients,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun GeneralSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = "",
            onValueChange = {},
            label = { Text(stringResource(Res.string.product_name)) },
            supportingText = {
                Text("* " + stringResource(Res.string.neutral_required))
            }
        )

        TextField(
            value = "",
            onValueChange = {},
            label = { Text("Servings") },
            supportingText = {
                Text("How many servings does this recipe make?")
            }
        )
    }
}

private fun LazyListScope.ingredientsSection(
    onAddIngredient: () -> Unit,
    ingredients: List<RecipeIngredient>,
    onIngredientClick: (RecipeIngredient) -> Unit
) {
    item {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAddIngredient() }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledIconButton(
                onClick = {},
                modifier = Modifier.clearAndSetSemantics { },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }

            Spacer(Modifier.width(8.dp))

            Text("Add ingredient")
        }
    }

    items(
        items = ingredients
    ) {
        it.ListItem(
            onClick = { onIngredientClick(it) }
        )
    }
}

@Composable
private fun SummarySection(
    ingredients: List<ProductWithMeasurement>,
    modifier: Modifier = Modifier
) {
    val weight = remember(ingredients) { ingredients.sumOf { it.measurement.weight } }
    val proteins = remember(ingredients) { ingredients.sumOf { it.proteins } }
    val carbohydrates = remember(ingredients) { ingredients.sumOf { it.carbohydrates } }
    val fats = remember(ingredients) { ingredients.sumOf { it.fats } }

    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(
                Res.string.in_x_weight_unit,
                weight.formatClipZeros("%.0f"),
                stringResource(Res.string.unit_gram_short)
            ),
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(Modifier.height(8.dp))

        CaloriesProgressIndicator(
            proteins = proteins,
            carbohydrates = carbohydrates,
            fats = fats,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        NutrientsList(
            products = ingredients,
            incompleteValue = {
                {
                    val g = stringResource(Res.string.unit_gram_short)
                    val value = it.formatClipZeros()
                    Text(
                        text = "* $value $g",
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        )
    }
}
