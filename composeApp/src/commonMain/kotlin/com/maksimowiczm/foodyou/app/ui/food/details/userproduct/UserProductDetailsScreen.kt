package com.maksimowiczm.foodyou.app.ui.food.details.userproduct

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.app.ui.common.component.Image
import com.maksimowiczm.foodyou.app.ui.common.component.StatusBarProtection
import com.maksimowiczm.foodyou.app.ui.common.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import com.maksimowiczm.foodyou.app.ui.common.extension.toDp
import com.maksimowiczm.foodyou.app.ui.food.LocalFoodNameSelector
import com.maksimowiczm.foodyou.app.ui.food.details.FavoriteIconButton
import com.maksimowiczm.foodyou.app.ui.food.details.NutrientList
import com.maksimowiczm.foodyou.app.ui.food.details.NutrientsHeader
import com.maksimowiczm.foodyou.app.ui.food.details.rememberNutrientExpanded
import com.maksimowiczm.foodyou.common.domain.food.Nutrient
import com.maksimowiczm.foodyou.userfood.domain.product.UserProduct
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductIdentity
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
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

    val headline =
        remember(userFood, nameSelector) {
            userFood?.let { food ->
                buildString {
                    append(nameSelector.select(food.name))
                    append(food.brand?.value?.let { " ($it)" } ?: "")
                }
            }
        }

    val lazyListState = rememberLazyListState()
    val animatedIsScrolled =
        animateFloatAsState(
            targetValue = if (lazyListState.canScrollBackward) 1f else 0f,
            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
        )
    val animatedIconButtonColor =
        animateColorAsState(
            targetValue =
                if (lazyListState.canScrollBackward) MaterialTheme.colorScheme.surfaceContainerHigh
                else MaterialTheme.colorScheme.surface
        )

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    ArrowBackIconButton(
                        onClick = onBack,
                        colors =
                            IconButtonDefaults.iconButtonColors(
                                containerColor = animatedIconButtonColor.value
                            ),
                    )
                },
                actions = {
                    FavoriteIconButton(
                        favorite = isFavorite ?: false,
                        onChange = onSetFavorite,
                        colors =
                            IconButtonDefaults.iconButtonColors(
                                containerColor = animatedIconButtonColor.value
                            ),
                    )
                    LocalMenu(
                        onEdit = { onEdit() },
                        onDelete = { onDelete() },
                        colors =
                            IconButtonDefaults.iconButtonColors(
                                containerColor = animatedIconButtonColor.value
                            ),
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
    ) { contentPadding ->
        LazyColumn(state = lazyListState, contentPadding = contentPadding.add(bottom = 8.dp)) {
            item {
                Box(Modifier.fillMaxWidth().padding(16.dp)) {
                    if (headline != null) {
                        Text(text = headline, style = MaterialTheme.typography.displaySmall)
                    } else {
                        Spacer(
                            Modifier.shimmer()
                                .fillMaxWidth(.75f)
                                .height(MaterialTheme.typography.displaySmall.toDp())
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                        )
                    }
                }
            }
            item {
                when {
                    userFood == null ->
                        Spacer(
                            Modifier.shimmer()
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .padding(horizontal = 32.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                        )

                    userFood.image == null -> Unit

                    else ->
                        userFood.image.Image(
                            shimmer = rememberShimmer(ShimmerBounds.View),
                            modifier =
                                Modifier.fillMaxWidth()
                                    .aspectRatio(16f / 9f)
                                    .padding(horizontal = 32.dp),
                        )
                }
            }
            item { Spacer(Modifier.height(8.dp)) }
            if (userFood?.nutritionFacts != null) {
                item {
                    NutrientsHeader(
                        proteins = userFood.nutritionFacts.proteins.value?.toFloat(),
                        carbohydrates = userFood.nutritionFacts.carbohydrates.value?.toFloat(),
                        fats = userFood.nutritionFacts.fats.value?.toFloat(),
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        enabled = expandingEnabled,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    )
                    Spacer(Modifier.height(8.dp))
                    NutrientList(
                        facts = userFood.nutritionFacts,
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
                }
            }
            if (userFood?.note != null) {
                item {
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(Modifier.fillMaxWidth().padding(horizontal = 16.dp))
                    Spacer(Modifier.height(16.dp))
                    Note(
                        note = userFood.note.value,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    )
                }
            }
        }
    }
    StatusBarProtection(MaterialTheme.colorScheme.surfaceContainerHigh) { animatedIsScrolled.value }
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
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(Res.string.headline_note),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(text = note, style = MaterialTheme.typography.bodyMedium)
    }
}
