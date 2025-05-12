package com.maksimowiczm.foodyou.feature.recipe.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.domain.model.sum
import com.maksimowiczm.foodyou.core.ui.component.BackHandler
import com.maksimowiczm.foodyou.core.ui.component.IncompleteFoodData
import com.maksimowiczm.foodyou.core.ui.component.IncompleteFoodsList
import com.maksimowiczm.foodyou.core.ui.component.NutritionFactsList
import com.maksimowiczm.foodyou.feature.recipe.model.Ingredient
import foodyou.app.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RecipeFormScreen(
    state: RecipeState,
    onNameChange: (String) -> Unit,
    onServingsChange: (String) -> Unit,
    onAddIngredient: () -> Unit,
    onEditIngredient: (Ingredient) -> Unit,
    onRemoveIngredient: (index: Int) -> Unit,
    onEditProduct: (Long) -> Unit,
    onClose: () -> Unit,
    onCreate: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    if (showDiscardDialog) {
        DiscardDialog(
            action = state.action,
            onDismissRequest = { showDiscardDialog = false },
            onConfirm = onClose
        )
    }

    val handleClose = {
        if (state.isModified) {
            showDiscardDialog = true
        } else {
            onClose()
        }
    }

    BackHandler(
        enabled = state.isModified
    ) {
        showDiscardDialog = true
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val topBar = @Composable {
        TopAppBar(
            title = {
                val text = when (state.action) {
                    RecipeAction.Create -> stringResource(Res.string.headline_create_recipe)
                    RecipeAction.Update -> stringResource(Res.string.headline_update_recipe)
                }

                Text(text)
            },
            navigationIcon = {
                IconButton(
                    onClick = handleClose
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(Res.string.action_close)
                    )
                }
            },
            actions = {
                val text = when (state.action) {
                    RecipeAction.Create -> stringResource(Res.string.action_create)
                    RecipeAction.Update -> stringResource(Res.string.action_save)
                }

                TextButton(
                    onClick = onCreate,
                    enabled = state.isValid
                ) {
                    Text(text)
                }
            },
            scrollBehavior = scrollBehavior
        )
    }

    val coroutineScope = rememberCoroutineScope()
    var selectedIngredientIndex by rememberSaveable { mutableStateOf(-1) }
    if (selectedIngredientIndex != -1) {
        val item = state.ingredients.getOrNull(selectedIngredientIndex)

        LaunchedEffect(item) {
            if (item == null) {
                selectedIngredientIndex = -1
            }
        }

        if (item == null) {
            return
        }

        val sheetState = rememberModalBottomSheetState()

        ModalBottomSheet(
            onDismissRequest = {
                selectedIngredientIndex = -1
            },
            sheetState = sheetState
        ) {
            item.ListItem()
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            ListItem(
                headlineContent = {
                    Text(stringResource(Res.string.action_edit_ingredient_measurement))
                },
                modifier = Modifier.clickable {
                    coroutineScope.launch {
                        sheetState.hide()
                        selectedIngredientIndex = -1
                        onEditIngredient(item)
                    }
                },
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
                    Text(stringResource(Res.string.action_delete_ingredient))
                },
                modifier = Modifier.clickable {
                    coroutineScope.launch {
                        onRemoveIngredient(selectedIngredientIndex)
                        sheetState.hide()
                        selectedIngredientIndex = -1
                    }
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null
                    )
                },
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent
                )
            )
        }
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
                FlowRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextField(
                        value = state.name.value,
                        onValueChange = onNameChange,
                        isError = !state.name.isValid,
                        label = { Text(stringResource(Res.string.product_name)) },
                        supportingText = {
                            Text("* " + stringResource(Res.string.neutral_required))
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        )
                    )

                    TextField(
                        value = TextFieldValue(
                            state.servings.value,
                            TextRange(state.servings.value.length)
                        ),
                        onValueChange = { onServingsChange(it.text) },
                        isError = !state.servings.isValid,
                        label = { Text(stringResource(Res.string.recipe_servings)) },
                        supportingText = {
                            Text(stringResource(Res.string.description_recipe_servings))
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        )
                    )
                }
            }

            item {
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
            }

            item {
                Text(
                    text = stringResource(Res.string.headline_ingredients),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAddIngredient() }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledIconButton(
                        onClick = onAddIngredient,
                        modifier = Modifier.clearAndSetSemantics { },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.primary
                        )
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

            items(state.ingredients) {
                it.ListItem(
                    modifier = Modifier
                        .clickable { selectedIngredientIndex = state.ingredients.indexOf(it) }
                )
            }

            if (state.ingredients.isNotEmpty()) {
                item {
                    HorizontalDivider(Modifier.padding(bottom = 8.dp))
                }

                item {
                    Text(
                        text = stringResource(Res.string.headline_summary),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    Spacer(Modifier.height(8.dp))
                }

                item {
                    val nutritionFacts = state.ingredients
                        .map { it.product.nutritionFacts * (it.weight ?: 0f) / 100f }
                        .sum()

                    val anyProductIncomplete = state.ingredients.any {
                        !it.product.nutritionFacts.isComplete
                    }

                    Column {
                        NutritionFactsList(
                            facts = nutritionFacts,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )

                        if (anyProductIncomplete) {
                            IncompleteFoodsList(
                                foods = state.ingredients
                                    .distinctBy { it.product.id }
                                    .map {
                                        IncompleteFoodData(
                                            foodId = it.product.id,
                                            name = it.product.name
                                        )
                                    },
                                onFoodClick = {
                                    it as FoodId.Product
                                    onEditProduct(it.id)
                                },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DiscardDialog(
    action: RecipeAction,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(stringResource(Res.string.action_discard))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(Res.string.action_cancel))
            }
        },
        text = {
            when (action) {
                RecipeAction.Create -> Text(stringResource(Res.string.question_discard_recipe))
                RecipeAction.Update -> Text(stringResource(Res.string.question_discard_changes))
            }
        }
    )
}
