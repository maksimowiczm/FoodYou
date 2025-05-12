package com.maksimowiczm.foodyou.feature.recipe.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.domain.model.Food
import com.maksimowiczm.foodyou.core.domain.model.Measurement
import com.maksimowiczm.foodyou.core.ui.component.CaloriesProgressIndicator
import com.maksimowiczm.foodyou.core.ui.component.NutritionFactsList
import com.maksimowiczm.foodyou.core.ui.ext.firstVisibleItemAlpha
import com.maksimowiczm.foodyou.core.ui.res.Saver
import com.maksimowiczm.foodyou.feature.addfood.ui.measurement.WeightChips
import com.maksimowiczm.foodyou.feature.addfood.ui.measurement.rememberWeightChipsState
import com.maksimowiczm.foodyou.feature.measurement.ui.basic.MeasurementForm
import com.maksimowiczm.foodyou.feature.measurement.ui.basic.rememberMeasurementFormState
import com.maksimowiczm.foodyou.feature.measurement.ui.basic.toEnum
import com.maksimowiczm.foodyou.feature.measurement.ui.basic.value
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MeasurementScreen(
    food: Food,
    suggestions: List<Measurement>,
    selected: Measurement?,
    onBack: () -> Unit,
    onMeasurement: (Measurement) -> Unit,
    onEditFood: () -> Unit,
    onDeleteFood: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedEnum by rememberSaveable { mutableStateOf(selected?.toEnum()) }
    LaunchedEffect(selected) {
        selectedEnum = selected?.toEnum()
    }

    val fromState = rememberMeasurementFormState(
        food = food,
        suggestions = remember(suggestions) {
            suggestions.associateBy {
                it.toEnum()
            }.map {
                it.key to it.value.value
            }.toMap()
        },
        selected = selectedEnum
    )

    var extraFilter by rememberSaveable(
        stateSaver = Measurement.Saver
    ) { mutableStateOf<Measurement?>(null) }
    val chipsState = rememberWeightChipsState(food, extraFilter)
    LaunchedEffect(fromState) {
        merge(
            snapshotFlow { fromState.packageInput?.value }
                .filterNotNull()
                .distinctUntilChanged()
                .drop(1)
                .map { Measurement.Package(it) },
            snapshotFlow { fromState.servingInput?.value }
                .filterNotNull()
                .distinctUntilChanged()
                .drop(1)
                .map { Measurement.Serving(it) },
            snapshotFlow { fromState.gramInput.value }
                .filterNotNull()
                .distinctUntilChanged()
                .drop(1)
                .map { Measurement.Gram(it) }
        ).collectLatest {
            extraFilter = it
        }
    }

    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteDialog(
            onDismissRequest = { showDeleteDialog = false },
            onDelete = onDeleteFood
        )
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val lazyListState = rememberLazyListState()
    var headlineHeight by remember { mutableIntStateOf(0) }
    var headlineAlpha = lazyListState.firstVisibleItemAlpha(headlineHeight)

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = food.name,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.graphicsLayer { alpha = headlineAlpha }
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.action_go_back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .imePadding()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            item {
                Text(
                    text = food.name,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .onSizeChanged { headlineHeight = it.height }
                        .graphicsLayer { alpha = 1 - headlineAlpha },
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            item {
                HorizontalDivider()
                MeasurementForm(
                    state = fromState,
                    onMeasurement = {
                        selectedEnum = it.toEnum()
                        onMeasurement(it)
                    },
                    contentPadding = PaddingValues(8.dp)
                )
                HorizontalDivider()
            }

            item {
                WeightChips(
                    state = chipsState,
                    contentPadding = PaddingValues(horizontal = 16.dp)
                )
            }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CaloriesProgressIndicator(
                        proteins = food.nutritionFacts.proteins.value,
                        carbohydrates = food.nutritionFacts.carbohydrates.value,
                        fats = food.nutritionFacts.fats.value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .padding(horizontal = 16.dp)
                    )

                    val nutritionFacts = remember(chipsState.selectedFilter, food) {
                        val weight = chipsState.selectedFilter.weight(food) ?: 100f
                        food.nutritionFacts * weight / 100f
                    }

                    NutritionFactsList(
                        facts = nutritionFacts,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            item {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterHorizontally
                    )
                ) {
                    Button(
                        onClick = onEditFood
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(Res.string.action_edit))
                    }

                    OutlinedButton(
                        onClick = { showDeleteDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(Res.string.action_delete))
                    }
                }
            }
        }
    }
}

@Composable
private fun DeleteDialog(onDismissRequest: () -> Unit, onDelete: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onDelete
            ) {
                Text(stringResource(Res.string.action_delete))
            }
        },
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
