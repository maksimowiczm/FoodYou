package com.maksimowiczm.foodyou.feature.recipe.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.feature.recipe.domain.Ingredient
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.action_delete_ingredient
import foodyou.app.generated.resources.action_edit_ingredient_measurement
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FormContent(
    ingredients: List<Ingredient>,
    formState: RecipeFormState,
    onBack: () -> Unit,
    onAddIngredient: () -> Unit,
    onEditIngredient: (Ingredient) -> Unit,
    onRemoveIngredient: (index: Int) -> Unit,
    topBar: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    var selectedIngredientIndex = rememberSaveable {
        mutableStateOf<Int?>(null)
    }
    val selectedIngredient = remember(selectedIngredientIndex.value, ingredients) {
        selectedIngredientIndex.value?.let { index ->
            ingredients.getOrNull(index)
        }
    }

    if (selectedIngredient != null) {
        val sheetState = rememberModalBottomSheetState()

        ModalBottomSheet(
            onDismissRequest = { selectedIngredientIndex.value = null },
            sheetState = sheetState
        ) {
            IngredientListItem(
                ingredient = selectedIngredient,
                onClick = null
            )
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            ListItem(
                headlineContent = {
                    Text(stringResource(Res.string.action_edit_ingredient_measurement))
                },
                modifier = Modifier.clickable {
                    coroutineScope.launch {
                        onEditIngredient(selectedIngredient)
                        sheetState.hide()
                        selectedIngredientIndex.value = null
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
                        sheetState.hide()
                        selectedIngredientIndex.value?.let { onRemoveIngredient(it) }
                        selectedIngredientIndex.value = null
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
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding
        ) {
            item {
                RecipeForm(
                    ingredients = ingredients,
                    onAddIngredient = onAddIngredient,
                    onIngredientClick = { index ->
                        selectedIngredientIndex.value = index
                    },
                    formState = formState,
                    contentPadding = PaddingValues(horizontal = 16.dp)
                )
            }
        }
    }
}
