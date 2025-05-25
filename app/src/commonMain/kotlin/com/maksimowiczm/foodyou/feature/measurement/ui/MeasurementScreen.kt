package com.maksimowiczm.foodyou.feature.measurement.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumExtendedFloatingActionButton
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
import com.maksimowiczm.foodyou.core.domain.model.Food
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.domain.model.Measurement
import com.maksimowiczm.foodyou.core.domain.model.Recipe
import com.maksimowiczm.foodyou.core.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.component.IncompleteFoodData
import com.maksimowiczm.foodyou.core.ui.component.IncompleteFoodsList
import com.maksimowiczm.foodyou.feature.measurement.ui.advanced.AdvancedMeasurementForm
import com.maksimowiczm.foodyou.feature.measurement.ui.advanced.AdvancedMeasurementFormState
import com.maksimowiczm.foodyou.feature.measurement.ui.advanced.AdvancedMeasurementSummary
import foodyou.app.generated.resources.*
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
    onClone: (() -> Unit)?,
    onIngredientClick: (FoodId) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var showCloneDialog by rememberSaveable { mutableStateOf(false) }
    if (showCloneDialog && onClone != null) {
        CloneRecipeDialog(
            onDismissRequest = { showCloneDialog = false },
            onClone = {
                showCloneDialog = false
                onClone()
            }
        )
    }

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

    Scaffold(
        modifier = modifier,
        topBar = {
            MediumFlexibleTopAppBar(
                title = { Text(food.headline) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                actions = {
                    Menu(
                        onEdit = onEditFood,
                        onDelete = { showDeleteDialog = true },
                        onClone = if (onClone != null) {
                            { showCloneDialog = true }
                        } else {
                            null
                        }
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            MediumExtendedFloatingActionButton(
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
                Spacer(Modifier.width(12.dp))
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

            item {
                Spacer(Modifier.height(8.dp))
            }

            item {
                val measurement = state.measurement ?: Measurement.Gram(100f)
                val weight = measurement.weight(food) ?: 100f
                val nutritionFacts = food.nutritionFacts * weight / 100f

                AdvancedMeasurementSummary(
                    nutritionFacts = nutritionFacts,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            if (food is Recipe) {
                val incompleteFoods = food.ingredients
                    .filter { !it.product.nutritionFacts.isComplete }
                    .map { IncompleteFoodData(it.product.id, it.product.headline) }

                item {
                    IncompleteFoodsList(
                        foods = incompleteFoods,
                        onFoodClick = onIngredientClick,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            item {
                Spacer(Modifier.height(8.dp + 80.dp + 16.dp))
            }
        }
    }
}

@Composable
private fun Menu(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClone: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
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
            onClone?.let {
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.action_copy)) },
                    onClick = {
                        expanded = false
                        it()
                    }
                )
            }
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
private fun CloneRecipeDialog(
    onDismissRequest: () -> Unit,
    onClone: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onClone
            ) {
                Text(stringResource(Res.string.action_copy))
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
        title = {
            Text(stringResource(Res.string.headline_copy_recipe))
        },
        text = {
            Text(stringResource(Res.string.description_copy_recipe))
        }
    )
}
