package com.maksimowiczm.foodyou.app.ui.food.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.app.ui.common.component.IncompleteFoodsList
import com.maksimowiczm.foodyou.app.ui.common.utility.stringResourceWithWeight
import com.maksimowiczm.foodyou.app.ui.food.component.EnergyProgressIndicator
import com.maksimowiczm.foodyou.app.ui.food.component.MeasurementPicker
import com.maksimowiczm.foodyou.app.ui.food.component.rememberMeasurementPickerState
import com.maksimowiczm.foodyou.app.ui.food.shared.component.NutrientList
import com.maksimowiczm.foodyou.common.compose.extension.add
import com.maksimowiczm.foodyou.common.domain.food.isComplete
import com.maksimowiczm.foodyou.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.food.domain.entity.Product
import com.maksimowiczm.foodyou.food.domain.entity.Recipe
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun MeasureIngredientScreen(
    onBack: () -> Unit,
    measurement: Measurement,
    viewModel: MeasureIngredientViewModel,
    onSave: (Measurement) -> Unit,
    modifier: Modifier = Modifier,
) {
    val possibleMeasurements = viewModel.possibleMeasurements.collectAsStateWithLifecycle().value
    val suggestions = viewModel.suggestions.collectAsStateWithLifecycle().value
    val food = viewModel.food.collectAsStateWithLifecycle().value

    if (possibleMeasurements == null || suggestions == null || food == null) {
        // TODO loading state
        return
    }

    // This is stupid that it is here but it's going to be deleted in 4.0.0
    val selectedMeasurement =
        remember(measurement) {
            if (food.weight(measurement) != null) {
                measurement
            } else {
                if (food.isLiquid) Measurement.Milliliter(100.0) else Measurement.Gram(100.0)
            }
        }

    val measurementPickerState =
        rememberMeasurementPickerState(
            suggestions = suggestions,
            possibleTypes = possibleMeasurements,
            selectedMeasurement = selectedMeasurement,
        )

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            MediumTopAppBar(
                title = { Text(food.headline) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            LargeExtendedFloatingActionButton(
                onClick = { onSave(measurementPickerState.measurement) },
                icon = { Icon(imageVector = Icons.Filled.Edit, contentDescription = null) },
                text = { Text(stringResource(Res.string.action_save)) },
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier.fillMaxSize()
                    .imePadding()
                    .padding(horizontal = 8.dp)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues.add(vertical = 8.dp).add(bottom = 80.dp + 24.dp),
        ) {
            item { HorizontalDivider() }

            item {
                MeasurementPicker(
                    state = measurementPickerState,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }

            item { HorizontalDivider() }

            item {
                val measurement = measurementPickerState.measurement
                val facts =
                    remember(food, measurement) {
                        val weight =
                            food.weight(measurement)
                                ?: error(
                                    "Invalid measurement: $measurement for food: ${food.headline}"
                                )
                        food.nutritionFacts * (weight / 100)
                    }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ViewList,
                            contentDescription = null,
                        )
                    }

                    val proteins = facts.proteins.value
                    val carbohydrates = facts.carbohydrates.value
                    val fats = facts.fats.value

                    if (proteins != null && carbohydrates != null && fats != null) {
                        EnergyProgressIndicator(
                            proteins = proteins.toFloat(),
                            carbohydrates = carbohydrates.toFloat(),
                            fats = fats.toFloat(),
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                val measurementString =
                    measurement.stringResourceWithWeight(
                        totalWeight = food.totalWeight,
                        servingWeight = food.servingWeight,
                        isLiquid = food.isLiquid,
                    ) ?: error("Invalid measurement: $measurement for food ${food.id}")

                Text(
                    text = measurementString,
                    modifier = Modifier.padding(horizontal = 8.dp).padding(bottom = 8.dp),
                    style = MaterialTheme.typography.labelLarge,
                )

                NutrientList(facts)
            }

            if (food is Recipe) {
                item {
                    val incompleteIngredients =
                        food
                            .flatIngredients()
                            .filter { it is Product }
                            .filterNot { it.nutritionFacts.isComplete }

                    IncompleteFoodsList(
                        foods = incompleteIngredients.map { it.headline }.distinct(),
                        modifier = Modifier.padding(8.dp),
                    )
                }
            }
        }
    }
}
