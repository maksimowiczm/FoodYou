package com.maksimowiczm.foodyou.feature.recipe.ui.measure

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.model.Food
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.model.Recipe
import com.maksimowiczm.foodyou.core.model.Saver
import com.maksimowiczm.foodyou.core.model.stringResource
import com.maksimowiczm.foodyou.core.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.component.CaloriesProgressIndicator
import com.maksimowiczm.foodyou.core.ui.component.IncompleteFoodData
import com.maksimowiczm.foodyou.core.ui.component.IncompleteFoodsList
import com.maksimowiczm.foodyou.core.ui.component.NutritionFactsList
import com.maksimowiczm.foodyou.feature.measurement.ui.basic.MeasurementForm
import com.maksimowiczm.foodyou.feature.measurement.ui.basic.rememberMeasurementFormState
import com.maksimowiczm.foodyou.feature.measurement.ui.basic.toEnum
import com.maksimowiczm.foodyou.feature.measurement.ui.basic.value
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.in_x
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun MeasureIngredientScreen(
    foodId: FoodId,
    selected: Measurement?,
    onBack: () -> Unit,
    onMeasurement: (Measurement) -> Unit,
    onEditFood: (FoodId) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<MeasureIngredientViewModel>(
        parameters = { parametersOf(foodId) }
    )

    val food = viewModel.food.collectAsStateWithLifecycle().value
    val suggestions = viewModel.suggestions.collectAsStateWithLifecycle().value

    if (food == null || suggestions == null) {
        return
    }

    MeasureIngredientScreen(
        food = food,
        selected = selected,
        suggestions = suggestions,
        onBack = onBack,
        onMeasurement = onMeasurement,
        onEditFood = onEditFood,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MeasureIngredientScreen(
    food: Food,
    selected: Measurement?,
    suggestions: List<Measurement>,
    onBack: () -> Unit,
    onMeasurement: (Measurement) -> Unit,
    onEditFood: (FoodId) -> Unit,
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

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            MediumFlexibleTopAppBar(
                title = { Text(food.headline) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior
            )
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
                MeasurementForm(
                    state = fromState,
                    onMeasurement = onMeasurement,
                    contentPadding = PaddingValues(8.dp)
                )
            }

            item {
                HorizontalDivider()
            }

            item {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    WeightChips(
                        state = chipsState,
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    )

                    CaloriesProgressIndicator(
                        proteins = food.nutritionFacts.proteins.value,
                        carbohydrates = food.nutritionFacts.carbohydrates.value,
                        fats = food.nutritionFacts.fats.value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .padding(horizontal = 16.dp)
                    )

                    Text(
                        text = stringResource(
                            Res.string.in_x,
                            chipsState.selectedFilter.stringResource()
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.labelLarge
                    )

                    val facts = remember(chipsState.selectedFilter, food) {
                        val weight = chipsState.selectedFilter.weight(food) ?: 100f
                        food.nutritionFacts * weight / 100f
                    }

                    NutritionFactsList(
                        facts = facts,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    if (food is Recipe) {
                        val incompleteIngredients =
                            food.ingredients.filter { !it.food.nutritionFacts.isComplete }

                        if (incompleteIngredients.isNotEmpty()) {
                            IncompleteFoodsList(
                                foods = incompleteIngredients.map {
                                    IncompleteFoodData(
                                        foodId = it.food.id,
                                        name = it.food.headline
                                    )
                                },
                                onFoodClick = onEditFood,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
