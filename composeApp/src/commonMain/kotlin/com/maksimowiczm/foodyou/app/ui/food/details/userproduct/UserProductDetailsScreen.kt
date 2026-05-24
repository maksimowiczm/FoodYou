package com.maksimowiczm.foodyou.app.ui.food.details.userproduct

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalClipboardManager
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.app.ui.common.utility.resolveBlob
import com.maksimowiczm.foodyou.app.ui.food.details.FavoriteIconButton
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsHeadline
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsImage
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsNutrients
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsTopBar
import com.maksimowiczm.foodyou.app.ui.food.details.rememberNutrientExpanded
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.Nutrient
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.PackageQuantity
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.ServingQuantity
import com.maksimowiczm.foodyou.common.domain.food.scale
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.milliliters
import com.maksimowiczm.foodyou.common.expect
import com.maksimowiczm.foodyou.common.onError
import com.maksimowiczm.foodyou.userproduct.domain.UserProduct
import com.maksimowiczm.foodyou.userproduct.domain.UserProductIdentity
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun UserProductDetailsScreen(
    identity: UserProductIdentity,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: UserProductDetailsViewModel =
        koinViewModel(parameters = { parametersOf(identity) })

    LaunchedCollectWithLifecycle(viewModel.uiEvents) {
        when (it) {
            UserProductDetailsUiEvent.Deleted -> onDelete()
        }
    }

    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    val userFood by viewModel.userFood.collectAsStateWithLifecycle()

    UserProductDetailsScreen(
        isFavorite = isFavorite,
        userFood = userFood,
        onBack = onBack,
        onEdit = onEdit,
        onDelete = viewModel::delete,
        onSetFavorite = viewModel::setFavorite,
        modifier = modifier,
    )
}

@Composable
private fun UserProductDetailsScreen(
    isFavorite: Boolean?,
    userFood: UserProduct?,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSetFavorite: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val nameSelector = LocalFoodNameSelector.current

    var expanded by rememberNutrientExpanded()
    val expandingEnabled =
        remember(userFood) {
            if (userFood?.nutritionFacts == null) return@remember false
            (Nutrient.all - Nutrient.basic).any { userFood.nutritionFacts[it].value != null }
        }

    var quantity by
        rememberSerializable(userFood) {
            val default =
                if (userFood?.servingQuantity != null) ServingQuantity(1.0)
                else if (userFood?.packageQuantity != null) PackageQuantity(1.0)
                else if (userFood?.isLiquid == true) AbsoluteQuantity.Volume(100.milliliters)
                else AbsoluteQuantity.Weight(100.grams)

            mutableStateOf(default)
        }
    val quantitySuggestions =
        remember(userFood) {
            if (userFood == null) return@remember emptyList<Quantity>()
            else
                buildList {
                    if (userFood.isLiquid) add(AbsoluteQuantity.Volume(100.milliliters))
                    else add(AbsoluteQuantity.Weight(100.grams))
                    if (userFood.servingQuantity != null) add(ServingQuantity(1.0))
                    if (userFood.packageQuantity != null) add(PackageQuantity(1.0))
                }
        }

    val headline =
        remember(userFood, nameSelector) {
            userFood?.let { food ->
                buildString {
                    append(nameSelector.select(food.name))
                    append(food.brand?.let { " ($it)" } ?: "")
                }
            }
        }

    val lazyListState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(lazyListState)

    Scaffold(
        modifier = modifier,
        topBar = {
            FoodDetailsTopBar(
                onBack = onBack,
                title = headline,
                actions = {
                    FavoriteIconButton(favorite = isFavorite ?: false, onChange = onSetFavorite)
                    LocalMenu(onEdit = { onEdit() }, onDelete = { onDelete() })
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            state = lazyListState,
            contentPadding = contentPadding.add(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { FoodDetailsHeadline(headline = headline) }
            item {
                FoodDetailsImage(
                    image = userFood?.image?.let { resolveBlob(it) },
                    showPlaceholder = userFood == null,
                )
            }
            if (userFood?.nutritionFacts != null) {
                item {
                    val facts =
                        remember(userFood, quantity) {
                            userFood.nutritionFacts
                                .scale(userFood.packageQuantity, userFood.servingQuantity, quantity)
                                .onError {
                                    return@remember NutritionFacts()
                                }
                                .expect("Can't be error at this point")
                        }

                    FoodDetailsNutrients(
                        nutritionFacts = facts,
                        quantities = quantitySuggestions,
                        selectedQuantity = quantity,
                        servingQuantity = userFood.servingQuantity,
                        packageQuantity = userFood.packageQuantity,
                        onSelectQuantity = { quantity = it },
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        expandingEnabled = expandingEnabled,
                    )
                }
            }
            if (userFood?.note != null) {
                item {
                    Note(
                        note = userFood.note,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun LocalMenu(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(Res.string.headline_delete_food)) },
            text = { Text(stringResource(Res.string.description_delete_food)) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    },
                    shapes = ButtonDefaults.shapes(),
                    colors =
                        ButtonDefaults.buttonColors(
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                        ),
                ) {
                    Text(text = stringResource(Res.string.action_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = stringResource(Res.string.action_cancel))
                }
            },
        )
    }

    Box(modifier) {
        IconButton(
            onClick = { expanded = true },
            shapes = IconButtonDefaults.shapes(),
            colors = colors,
        ) {
            Icon(imageVector = Icons.Outlined.MoreVert, contentDescription = null)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.action_edit)) },
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Edit, contentDescription = null)
                },
                onClick = {
                    expanded = false
                    onEdit()
                },
            )
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.action_delete)) },
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Delete, contentDescription = null)
                },
                onClick = {
                    expanded = false
                    showDeleteDialog = true
                },
            )
        }
    }
}

@Composable
private fun Note(note: String, modifier: Modifier = Modifier) {
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier =
            modifier.combinedClickable(
                interactionSource = null,
                indication = null,
                onClick = {},
                onLongClick = { clipboardManager.copy("note", note) },
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = note, style = MaterialTheme.typography.bodyMedium)
    }
}
