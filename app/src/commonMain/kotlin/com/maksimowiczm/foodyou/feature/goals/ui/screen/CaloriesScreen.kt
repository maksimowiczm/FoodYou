package com.maksimowiczm.foodyou.feature.goals.ui.screen

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.AnimationConstants.DefaultDurationMillis
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.domain.model.sum
import com.maksimowiczm.foodyou.core.ui.LocalNavigationSharedTransitionScope
import com.maksimowiczm.foodyou.core.ui.component.IncompleteFoodData
import com.maksimowiczm.foodyou.core.ui.component.IncompleteFoodsList
import com.maksimowiczm.foodyou.core.ui.component.NutrientsList
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.core.ui.utils.LocalDateFormatter
import com.maksimowiczm.foodyou.feature.goals.model.DiaryDay
import com.maksimowiczm.foodyou.feature.goals.ui.CaloriesIndicatorTransitionKeys
import com.maksimowiczm.foodyou.feature.goals.ui.component.CaloriesIndicator
import com.maksimowiczm.foodyou.feature.goals.ui.component.MealsFilter
import com.maksimowiczm.foodyou.feature.goals.ui.component.rememberMealsFilterState
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.unit_gram_short
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun CaloriesScreen(
    date: LocalDate,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onFoodClick: (FoodId) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CaloriesScreenViewModel = koinViewModel()
) {
    val diaryDay by viewModel.observeDiaryDay(date).collectAsStateWithLifecycle(null)

    if (diaryDay != null) {
        CaloriesScreen(
            diaryDay = diaryDay!!,
            animatedVisibilityScope = animatedVisibilityScope,
            onFoodClick = onFoodClick,
            modifier = modifier
        )
    } else {
        Surface(modifier) {
            Spacer(Modifier.fillMaxSize())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun CaloriesScreen(
    diaryDay: DiaryDay,
    onFoodClick: (FoodId) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    val dateFormatter = LocalDateFormatter.current

    val filterState = rememberMealsFilterState(diaryDay.meals.toSet())
    val meals by remember(filterState.selectedMeals) {
        derivedStateOf {
            diaryDay.meals.filter { it.id in filterState.selectedMeals }
        }
    }

    val homeSTS = LocalNavigationSharedTransitionScope.current ?: error("No SharedTransitionScope")

    val topBar = @Composable {
        val insets = TopAppBarDefaults.windowInsets

        with(homeSTS) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerLow
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(insets.asPaddingValues())
                        .consumeWindowInsets(insets)
                        .padding(16.dp)
                ) {
                    with(animatedVisibilityScope) {
                        Text(
                            text = dateFormatter.formatDate(diaryDay.date),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .animateEnterExit(
                                    enter = fadeIn(
                                        tween(
                                            delayMillis = DefaultDurationMillis
                                        )
                                    ),
                                    exit = fadeOut(tween(50))
                                )
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    CaloriesIndicator(
                        calories = diaryDay.totalCalories,
                        caloriesGoal = diaryDay.dailyGoals.calories,
                        proteins = diaryDay.totalProteins,
                        carbohydrates = diaryDay.totalCarbohydrates,
                        fats = diaryDay.totalFats,
                        modifier = Modifier.sharedElement(
                            sharedContentState = rememberSharedContentState(
                                key = CaloriesIndicatorTransitionKeys.CaloriesIndicator(
                                    epochDay = diaryDay.date.toEpochDays()
                                )
                            ),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = topBar,
        modifier = modifier
    ) { paddingValues ->
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.titleMedium
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = paddingValues
            ) {
                item {
                    MealsFilter(
                        state = filterState,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                item {
                    NutrientsList(
                        nutrients = diaryDay.meals
                            .filter { it in meals }
                            .flatMap { diaryDay.foods[it] ?: emptyList() }
                            .mapNotNull { it.realNutrients }
                            .sum(),
                        incompleteValue = {
                            {
                                val g = stringResource(Res.string.unit_gram_short)
                                val value = it.value?.formatClipZeros() ?: "0"
                                Text(
                                    text = "* $value $g",
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                item {
                    val foods = diaryDay.meals
                        .filter { it in meals }
                        .flatMap { diaryDay.foods[it] ?: emptyList() }

                    val anyProductIncomplete = foods.any { !it.nutrients.isComplete }

                    // Display incomplete products
                    if (anyProductIncomplete) {
                        IncompleteFoodsList(
                            foods = foods
                                .distinctBy { it.foodId }
                                .filter { !it.nutrients.isComplete }
                                .map {
                                    IncompleteFoodData(
                                        foodId = it.foodId,
                                        name = it.name
                                    )
                                },
                            onFoodClick = onFoodClick,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
