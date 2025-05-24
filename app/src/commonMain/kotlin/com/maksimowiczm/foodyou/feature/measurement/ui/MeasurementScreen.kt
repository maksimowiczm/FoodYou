package com.maksimowiczm.foodyou.feature.measurement.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.domain.model.Food
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.domain.model.Measurement
import com.maksimowiczm.foodyou.core.ext.now
import com.maksimowiczm.foodyou.core.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.feature.measurement.ui.advanced.AdvancedMeasurementForm
import com.maksimowiczm.foodyou.feature.measurement.ui.advanced.AdvancedMeasurementFormState
import com.maksimowiczm.foodyou.feature.measurement.ui.advanced.AdvancedMeasurementSummary
import com.maksimowiczm.foodyou.feature.measurement.ui.advanced.rememberAdvancedMeasurementFormState
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun MeasurementScreen(
    foodId: FoodId,
    mealId: Long?,
    date: LocalDate?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MeasurementViewModel = koinViewModel(
        parameters = { parametersOf(foodId) }
    )
) {
    val food = viewModel.food.collectAsStateWithLifecycle().value
    val meals = viewModel.meals.collectAsStateWithLifecycle().value
    val suggestions = viewModel.suggestions.collectAsStateWithLifecycle().value

    if (food == null || meals == null || suggestions == null) {
        // TODO
        return
    }

    val formState = rememberAdvancedMeasurementFormState(
        food = food,
        initialDate = date ?: LocalDate.now(TimeZone.currentSystemDefault()),
        meals = meals,
        measurements = suggestions,
        initialMeal = mealId?.let { meals.indexOfFirst { meal -> meal.id == it } },
        initialMeasurement = 0
    )

    MeasurementScreen(
        state = formState,
        food = food,
        onBack = onBack,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun MeasurementScreen(
    state: AdvancedMeasurementFormState,
    food: Food,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
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
        }
    }
}
