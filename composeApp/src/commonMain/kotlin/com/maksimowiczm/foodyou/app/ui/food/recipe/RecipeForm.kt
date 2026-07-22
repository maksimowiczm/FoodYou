package com.maksimowiczm.foodyou.app.ui.food.recipe

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.InteractionShapes
import com.maksimowiczm.foodyou.app.ui.common.component.FoodListItemSkeleton
import com.maksimowiczm.foodyou.app.ui.common.component.Image
import com.maksimowiczm.foodyou.app.ui.common.component.TwoRowFoodListItem
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import com.maksimowiczm.foodyou.app.ui.common.extension.horizontal
import com.maksimowiczm.foodyou.app.ui.common.extension.vertical
import com.maksimowiczm.foodyou.app.ui.common.rememberInteractionAnimatedShape
import com.maksimowiczm.foodyou.app.ui.common.utility.EnergyFormatter.stringResource
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalEnergyUnit
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.app.ui.common.utility.QuantityFormatter.stringResource
import com.maksimowiczm.foodyou.app.ui.common.utility.WeightFormatter.stringResource
import com.maksimowiczm.foodyou.app.ui.common.utility.resolveBlob
import com.maksimowiczm.foodyou.app.ui.food.details.NutrientList
import com.maksimowiczm.foodyou.app.ui.food.details.NutrientsHeader
import com.maksimowiczm.foodyou.app.ui.userproduct.OutlinedTextField
import com.maksimowiczm.foodyou.app.ui.userproduct.PhotoPicker
import com.maksimowiczm.foodyou.app.ui.userproduct.requiredStringResource
import com.maksimowiczm.foodyou.common.domain.Energy
import com.maksimowiczm.foodyou.common.domain.Weight
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentImage
import com.maksimowiczm.foodyou.common.domain.food.PackageQuantity
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.ServingQuantity
import com.maksimowiczm.foodyou.common.domain.food.isIncomplete
import com.maksimowiczm.foodyou.common.domain.food.nutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.scale
import com.maksimowiczm.foodyou.common.domain.food.totalWeight
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.getOrNull
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RecipeForm(
    state: RecipeFormState,
    isLocked: Boolean,
    contentPadding: PaddingValues,
    onAddIngredient: () -> Unit,
    onIngredientClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecipeFormViewModel = koinViewModel(),
) {
    val horizontalPadding = contentPadding.horizontal()

    val uiState by viewModel.state.collectAsStateWithLifecycle()

    val totalWeight = remember(uiState.components) { uiState.components?.totalWeight ?: 0.grams }

    val servings = derivedStateOf {
        state.servings.textFieldState.text.toString().toDoubleOrNull()?.takeIf { it > 0 }
    }
        .value

    val servingWeight =
        remember(totalWeight, servings) {
            if (servings != null && totalWeight.grams > 0.0) totalWeight / servings else null
        }
    var expanded by rememberSaveable { mutableStateOf(true) }

    val quantities =
        remember(servingWeight, totalWeight) {
            if (totalWeight == 0.grams || (servingWeight != null && servingWeight == 0.grams))
                return@remember emptyList()

            buildList {
                add(AbsoluteQuantity.Weight(100.grams))
                add(PackageQuantity(1.0))
                if (servingWeight != null) add(ServingQuantity(1.0))
            }
        }
    var selectedQuantity by rememberSerializable { mutableStateOf<Quantity>(PackageQuantity(1.0)) }
    LaunchedEffect(quantities) {
        if (!quantities.contains(selectedQuantity)) {
            selectedQuantity = PackageQuantity(1.0)
        }
    }

    val nutritionFacts = remember(uiState.components) { uiState.components?.nutritionFacts }
    val scaled =
        remember(nutritionFacts, totalWeight, servingWeight, selectedQuantity) {
            nutritionFacts
                ?.scale(
                    AbsoluteQuantity.Weight(totalWeight),
                    servingWeight?.let(AbsoluteQuantity::Weight),
                    selectedQuantity,
                )
                ?.getOrNull()
        }
    val anyNutrientIsMissing =
        remember(nutritionFacts) {
            if (nutritionFacts == null) return@remember false
            nutritionFacts.asMap().any { it.value.isIncomplete() } ||
                nutritionFacts.energy.isIncomplete()
        }

    Column(modifier = modifier.padding(contentPadding.vertical())) {
        General(state = state, isLocked = isLocked, modifier = Modifier.padding(horizontalPadding))
        Spacer(Modifier.height(8.dp))
        Ingredients(
            ingredients = uiState.ingredients,
            onDeleteIngredient = viewModel::removeIngredient,
            isLocked = isLocked,
            onAdd = onAddIngredient,
            onIngredientClick = onIngredientClick,
            contentPadding = horizontalPadding,
        )
        if (scaled != null) {
            if (quantities.isNotEmpty()) {
                val stringedQuantities = quantities.mapNotNull { quantity ->
                    quantity
                        .stringResource(
                            AbsoluteQuantity.Weight(totalWeight),
                            servingWeight?.let(AbsoluteQuantity::Weight),
                        )
                        .getOrNull()
                        ?.let { quantity to it }
                }
                LazyRow(
                    contentPadding = horizontalPadding,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(stringedQuantities) { (quantity, text) ->
                        FilterChip(
                            selected = quantity == selectedQuantity,
                            onClick = { selectedQuantity = quantity },
                            label = { Text(text) },
                        )
                    }
                }
            }
            if (nutritionFacts?.hasMacronutrientsValues() == true) {
                NutrientsHeader(
                    proteins = nutritionFacts.proteins.value,
                    carbohydrates = nutritionFacts.carbohydrates.value,
                    fats = nutritionFacts.fats.value,
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    enabled = true,
                    modifier = Modifier.fillMaxWidth().padding(horizontalPadding),
                )
                Spacer(Modifier.height(8.dp))
            }
            NutrientList(
                facts = scaled,
                expanded = expanded,
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .clickable(
                            interactionSource = null,
                            indication = null,
                            onClick = { expanded = !expanded },
                        ),
            )
            if (anyNutrientIsMissing) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "* " + stringResource(Res.string.description_incomplete_nutrition_data),
                    modifier = Modifier.padding(horizontalPadding),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun General(state: RecipeFormState, isLocked: Boolean, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        state.name.OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLocked,
            label = { Text(requiredStringResource(stringResource(Res.string.product_name))) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )
        state.servings.OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLocked,
            label = { Text(stringResource(Res.string.recipe_servings)) },
            supportingText = { Text(stringResource(Res.string.description_recipe_servings)) },
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        PhotoPicker(
            imageUri = state.imageUri.value,
            setImageUri = { state.imageUri.value = it },
            isLocked = isLocked,
            modifier = Modifier.fillMaxWidth().then(if (isLocked) Modifier.shimmer() else Modifier),
        )
        state.note.OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLocked,
            label = { Text(stringResource(Res.string.headline_note)) },
            supportingText = { Text(stringResource(Res.string.description_add_note)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )
    }
}

@Composable
private fun Ingredients(
    ingredients: List<IngredientItemState>,
    onDeleteIngredient: (Int) -> Unit,
    isLocked: Boolean,
    onAdd: () -> Unit,
    onIngredientClick: (index: Int) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val shimmer = rememberShimmer(ShimmerBounds.View)

    Column(modifier) {
        AddIngredientButton(
            onClick = onAdd,
            contentPadding = contentPadding.horizontal().add(horizontal = 12.dp),
        )
        Column(Modifier.padding(contentPadding.horizontal()).clip(MaterialTheme.shapes.large)) {
            ingredients.forEachIndexed { index, item ->
                key(item.entry.entryId) {
                    if (item.resolved == null) FoodListItemSkeleton(shimmer)
                    else
                        IngredientListItem(
                            resolved = item.resolved,
                            isLocked = isLocked,
                            shimmer = shimmer,
                            onClick = { onIngredientClick(index) },
                            onDelete = { onDeleteIngredient(index) },
                        )
                }
                Spacer(Modifier.height(2.dp))
            }
        }
    }
}

@Composable
private fun IngredientListItem(
    resolved: ResolvedIngredient,
    isLocked: Boolean,
    shimmer: Shimmer,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteIngredientDialog(onDelete = onDelete, onDismiss = { showDeleteDialog = false })
    }

    val measurementFacts = resolved.component.measuredNutritionFacts
    val measurementString = resolved.component.quantity.stringResource()

    val nameSelector = LocalFoodNameSelector.current
    val headline =
        remember(resolved.component.name, nameSelector) {
            nameSelector.select(resolved.component.name)
        }

    val image: @Composable (() -> Unit)? = run {
        when (val image = resolved.component.image) {
            is FoodCompositionComponentImage.Blob -> {
                @Composable { resolveBlob(image.blob).Image(shimmer, Modifier.size(56.dp)) }
            }

            is FoodCompositionComponentImage.Uri -> {
                @Composable { image.uri.Image(shimmer, Modifier.size(56.dp)) }
            }

            null -> null
        }
    }

    RecipeIngredientListItem(
        headline = headline,
        isRecipe = resolved.component.identity is FoodCompositionComponentIdentity.Recipe,
        proteins = measurementFacts.proteins.value,
        carbohydrates = measurementFacts.carbohydrates.value,
        fats = measurementFacts.fats.value,
        energy = measurementFacts.energy.value,
        quantity = { Text(measurementString) },
        onDelete = { showDeleteDialog = true },
        onClick = onClick,
        isLocked = isLocked,
        modifier = modifier,
        image = image,
    )
}

@Composable
private fun DeleteIngredientDialog(onDelete: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDelete, shapes = ButtonDefaults.shapes()) {
                Text(stringResource(Res.string.action_delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, shapes = ButtonDefaults.shapes()) {
                Text(stringResource(Res.string.action_cancel))
            }
        },
        text = { Text(stringResource(Res.string.action_delete_ingredient)) },
    )
}

@Composable
internal fun RecipeIngredientListItem(
    headline: String,
    isRecipe: Boolean,
    proteins: Weight?,
    carbohydrates: Weight?,
    fats: Weight?,
    energy: Energy?,
    quantity: @Composable () -> Unit,
    image: @Composable (() -> Unit)?,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    isLocked: Boolean,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val shape =
        rememberInteractionAnimatedShape(
            InteractionShapes(
                shape = MaterialTheme.shapes.extraSmall,
                pressedShape = MaterialTheme.shapes.large,
            ),
            interactionSource,
        )

    Surface(
        onClick = onClick,
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = shape,
        interactionSource = interactionSource,
    ) {
        TwoRowFoodListItem(
            headline = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = headline)
                    if (isRecipe) {
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(Res.drawable.ic_skillet_filled),
                            contentDescription = null,
                        )
                    }
                }
            },
            image = image,
            proteins = { Text(proteins?.stringResource() ?: "?") },
            carbohydrates = { Text(carbohydrates?.stringResource() ?: "?") },
            fats = { Text(fats?.stringResource() ?: "?") },
            energy = { Text(energy?.inUnit(LocalEnergyUnit.current)?.stringResource() ?: "?") },
            quantity = quantity,
            modifier = Modifier,
            trailingContent = {
                IconButton(
                    onClick = onDelete,
                    shapes = IconButtonDefaults.shapes(),
                    enabled = !isLocked,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = stringResource(Res.string.action_delete_ingredient),
                    )
                }
            },
            onClick = null,
        )
    }
}

@Composable
private fun AddIngredientButton(
    onClick: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val motionScheme = MaterialTheme.motionScheme
    val interactionSource = remember { MutableInteractionSource() }

    val cornerRadiusPx = remember { Animatable(16f) }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press ->
                    cornerRadiusPx.animateTo(8f, motionScheme.fastSpatialSpec())

                is PressInteraction.Release,
                is PressInteraction.Cancel ->
                    cornerRadiusPx.animateTo(16f, motionScheme.fastSpatialSpec())
            }
        }
    }

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onClick, interactionSource = interactionSource)
                .padding(vertical = 16.dp)
                .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(56.dp)
                .graphicsLayer {
                    clip = true
                    shape = RoundedCornerShape(cornerRadiusPx.value.coerceAtLeast(0f).dp)
                }
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
                modifier = Modifier.align(Alignment.Center),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        Spacer(Modifier.width(16.dp))
        Text(
            text = stringResource(Res.string.action_add_ingredient),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}
