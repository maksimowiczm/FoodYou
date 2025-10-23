package com.maksimowiczm.foodyou.app.ui.food.details

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import com.maksimowiczm.foodyou.app.ui.common.extension.toDp
import com.maksimowiczm.foodyou.app.ui.common.saveable.rememberBlockingDataStore
import com.maksimowiczm.foodyou.app.ui.food.EnergyProgressIndicator
import com.maksimowiczm.foodyou.app.ui.food.Image
import com.maksimowiczm.foodyou.app.ui.food.LocalFoodNameSelector
import com.maksimowiczm.foodyou.food.domain.FoodNote
import com.maksimowiczm.foodyou.food.domain.FoodProductIdentity
import com.maksimowiczm.foodyou.food.domain.FoodSource
import com.maksimowiczm.foodyou.food.domain.NutritionFacts
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun FoodDetailsScreen(
    identity: FoodProductIdentity,
    onBack: () -> Unit,
    onEdit: (FoodProductIdentity.Local) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<FoodDetailsViewModel> { parametersOf(identity) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FoodDetailsScreen(
        onBack = onBack,
        onRefresh = viewModel::refresh,
        onEdit = onEdit,
        uiState = uiState,
        modifier = modifier,
    )
}

@Composable
private fun FoodDetailsScreen(
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onEdit: (FoodProductIdentity.Local) -> Unit,
    uiState: FoodDetailsUiState,
    modifier: Modifier = Modifier,
) {
    val nameSelector = LocalFoodNameSelector.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    val headline = (uiState as? FoodDetailsUiState.WithData)?.headline(nameSelector)

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
                    when (val identity = uiState.identity) {
                        is FoodProductIdentity.FoodDataCentral -> RefreshMenu(onRefresh = onRefresh)
                        is FoodProductIdentity.Local -> LocalMenu(onEdit = { onEdit(identity) })
                        is FoodProductIdentity.OpenFoodFacts -> RefreshMenu(onRefresh = onRefresh)
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        when (uiState) {

            // TODO
            //  These aren't really possible if we are accessing it from food list, leave it for now
            is FoodDetailsUiState.Error -> TODO()
            is FoodDetailsUiState.NotFound -> TODO()

            is FoodDetailsUiState.WithData ->
                FoodDetailsContent(
                    isLoading = uiState.isLoading,
                    image = uiState.image,
                    nutritionFacts = uiState.nutritionFacts,
                    note = uiState.note,
                    source = uiState.source,
                    contentPadding = paddingValues,
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                )
        }
    }
}

@Composable
private fun FoodDetailsContent(
    isLoading: Boolean,
    image: FoodImageUiState,
    nutritionFacts: NutritionFacts?,
    note: FoodNote?,
    source: FoodSource?,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    var expanded by
        rememberBlockingDataStore(key = booleanPreferencesKey("FoodDetailsScreenExpanded")) {
            mutableStateOf(false)
        }

    Box(modifier) {
        Column(Modifier.padding(top = contentPadding.calculateTopPadding()).zIndex(100f)) {
            if (isLoading) {
                Spacer(Modifier.height(8.dp))
                LinearWavyProgressIndicator(Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
            } else {
                Spacer(Modifier.height(26.dp))
            }
        }

        LazyColumn(contentPadding = contentPadding.add(top = 26.dp, bottom = 8.dp)) {
            item {
                when (image) {
                    FoodImageUiState.Loading ->
                        Spacer(
                            Modifier.shimmer()
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .padding(horizontal = 32.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                        )

                    FoodImageUiState.NoImage -> Unit
                    is FoodImageUiState.WithImage ->
                        image.image.Image(
                            shimmer = rememberShimmer(ShimmerBounds.View),
                            modifier =
                                Modifier.fillMaxWidth()
                                    .aspectRatio(16f / 9f)
                                    .padding(horizontal = 32.dp),
                        )
                }
            }
            item { Spacer(Modifier.height(8.dp)) }
            if (nutritionFacts != null) {
                item {
                    NutrientsHeader(
                        proteins = nutritionFacts.proteins.value?.toFloat(),
                        carbohydrates = nutritionFacts.carbohydrates.value?.toFloat(),
                        fats = nutritionFacts.fats.value?.toFloat(),
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    )
                    Spacer(Modifier.height(8.dp))
                    NutrientList(
                        facts = nutritionFacts,
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
            if (note != null) {
                item {
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(Modifier.fillMaxWidth().padding(horizontal = 16.dp))
                    Spacer(Modifier.height(16.dp))
                    Note(
                        note = note.value,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    )
                }
            }
            when (source) {
                is FoodSource.OpenFoodFacts ->
                    item {
                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(Modifier.fillMaxWidth().padding(horizontal = 16.dp))
                        Spacer(Modifier.height(16.dp))
                        OpenFoodFactsSource(
                            url = source.url,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        )
                    }

                is FoodSource.FoodDataCentral ->
                    item {
                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(Modifier.fillMaxWidth().padding(horizontal = 16.dp))
                        Spacer(Modifier.height(16.dp))
                        FoodDataCentralSource(
                            url = source.url,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        )
                    }

                is FoodSource.UserAdded ->
                    item {
                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(Modifier.fillMaxWidth().padding(horizontal = 16.dp))
                        Spacer(Modifier.height(16.dp))
                        UserAddedSource(
                            value = source.value,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        )
                    }

                null -> Unit
            }
        }
    }
}

@Composable
private fun NutrientsHeader(
    proteins: Float?,
    carbohydrates: Float?,
    fats: Float?,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rotationState =
        animateFloatAsState(
            targetValue = if (expanded) 180f else 0f,
            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
        )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
            Icon(imageVector = Icons.AutoMirrored.Outlined.ViewList, contentDescription = null)
        }
        if (proteins != null && carbohydrates != null && fats != null) {
            EnergyProgressIndicator(
                proteins = proteins,
                carbohydrates = carbohydrates,
                fats = fats,
                modifier = Modifier.weight(1f),
            )
        } else {
            EnergyProgressIndicator(
                energy = 1f,
                proteins = 0f,
                carbohydrates = 0f,
                fats = 0f,
                goal = 1f,
                modifier = Modifier.weight(1f),
            )
        }

        IconButton(
            onClick = { onExpandedChange(!expanded) },
            shapes = IconButtonDefaults.shapes(),
        ) {
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowUp,
                contentDescription = null,
                modifier = Modifier.graphicsLayer { rotationZ = rotationState.value },
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

@Composable
private fun LocalMenu(onEdit: () -> Unit, modifier: Modifier = Modifier) {
    var expanded by rememberSaveable { mutableStateOf(false) }

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
        }
    }
}

@Composable
private fun RefreshMenu(onRefresh: () -> Unit, modifier: Modifier = Modifier) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(modifier) {
        IconButton(onClick = { expanded = true }, shapes = IconButtonDefaults.shapes()) {
            Icon(imageVector = Icons.Outlined.MoreVert, contentDescription = null)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.action_refresh)) },
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Refresh, contentDescription = null)
                },
                onClick = {
                    expanded = false
                    onRefresh()
                },
            )
        }
    }
}
