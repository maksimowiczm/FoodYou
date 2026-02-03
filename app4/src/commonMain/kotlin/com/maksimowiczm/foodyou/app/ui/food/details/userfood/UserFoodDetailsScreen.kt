package com.maksimowiczm.foodyou.app.ui.food.details.userfood

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
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.app.ui.common.component.Image
import com.maksimowiczm.foodyou.app.ui.common.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import com.maksimowiczm.foodyou.app.ui.common.extension.toDp
import com.maksimowiczm.foodyou.app.ui.food.LocalFoodNameSelector
import com.maksimowiczm.foodyou.app.ui.food.details.FavoriteIcon
import com.maksimowiczm.foodyou.app.ui.food.details.NutrientList
import com.maksimowiczm.foodyou.app.ui.food.details.NutrientsHeader
import com.maksimowiczm.foodyou.app.ui.food.details.rememberNutrientExpanded
import com.maksimowiczm.foodyou.userfood.domain.UserFoodProduct
import com.maksimowiczm.foodyou.userfood.domain.UserFoodProductIdentity
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun UserFoodDetailsScreen(
    identity: UserFoodProductIdentity,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: UserFoodDetailsViewModel = koinViewModel(parameters = { parametersOf(identity) })

    LaunchedCollectWithLifecycle(viewModel.uiEvents) {
        when (it) {
            UserFoodDetailsUiEvent.Deleted -> onDelete()
        }
    }

    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    val userFood by viewModel.userFood.collectAsStateWithLifecycle()

    UserFoodDetailsScreen(
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
private fun UserFoodDetailsScreen(
    isFavorite: Boolean?,
    userFood: UserFoodProduct?,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSetFavorite: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val nameSelector = LocalFoodNameSelector.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var expanded by rememberNutrientExpanded()

    val headline =
        remember(userFood, nameSelector) {
            userFood?.let { food ->
                buildString {
                    append(nameSelector.select(food.name))
                    append(food.brand?.let { " ($it)" } ?: "")
                }
            }
        }

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    if (headline != null) {
                        Text(headline)
                    } else {
                        Spacer(
                            Modifier.shimmer()
                                .fillMaxWidth(.75f)
                                .height(LocalTextStyle.current.toDp())
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                        )
                    }
                },
                navigationIcon = { ArrowBackIconButton(onBack) },
                actions = {
                    FavoriteIcon(favorite = isFavorite ?: false, onChange = onSetFavorite)

                    LocalMenu(onEdit = { onEdit() }, onDelete = { onDelete() })
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                scrollBehavior = scrollBehavior,
            )
        },
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = contentPadding.add(bottom = 8.dp),
        ) {
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

            if (userFood?.source != null) {
                item {
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(Modifier.fillMaxWidth().padding(horizontal = 16.dp))
                    Spacer(Modifier.height(16.dp))
                    UserAddedSource(
                        value = userFood.source.value,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun LocalMenu(onEdit: () -> Unit, onDelete: () -> Unit, modifier: Modifier = Modifier) {
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
        IconButton(onClick = { expanded = true }, shapes = IconButtonDefaults.shapes()) {
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
