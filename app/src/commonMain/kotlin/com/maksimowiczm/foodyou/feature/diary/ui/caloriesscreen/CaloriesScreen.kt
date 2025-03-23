package com.maksimowiczm.foodyou.feature.diary.ui.caloriesscreen

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.AnimationConstants.DefaultDurationMillis
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.feature.diary.data.model.DiaryDay
import com.maksimowiczm.foodyou.feature.diary.data.model.Nutrient
import com.maksimowiczm.foodyou.feature.diary.ui.component.CaloriesIndicator
import com.maksimowiczm.foodyou.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.ui.theme.LocalNutrientsPalette
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import kotlin.math.roundToInt
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CaloriesScreen(
    date: LocalDate,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    viewModel: CaloriesScreenViewModel = koinViewModel()
) {
    val diaryDay by viewModel.observeDiaryDay(date).collectAsStateWithLifecycle(null)

    if (diaryDay != null) {
        CaloriesScreen(
            diaryDay = diaryDay!!,
            formatDate = viewModel::formatDate,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = modifier
        )
    } else {
        Surface(modifier) {
            Spacer(Modifier.fillMaxSize())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CaloriesScreen(
    diaryDay: DiaryDay,
    formatDate: (LocalDate) -> String,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    val nutrientsPalette = LocalNutrientsPalette.current

    val topBar = @Composable {
        val insets = TopAppBarDefaults.windowInsets

        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerLow
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(insets.asPaddingValues())
                    .consumeWindowInsets(insets)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                with(animatedVisibilityScope) {
                    Text(
                        text = formatDate(diaryDay.date),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
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

                CaloriesIndicator(
                    calories = diaryDay.totalCalories.roundToInt(),
                    caloriesGoal = diaryDay.dailyGoals.calories,
                    proteins = diaryDay.totalProteins.roundToInt(),
                    carbohydrates = diaryDay.totalCarbohydrates.roundToInt(),
                    fats = diaryDay.totalFats.roundToInt()
                )
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
                    CaloriesScreenListItem(
                        label = {
                            Text(
                                text = stringResource(Res.string.nutriment_proteins),
                                color = nutrientsPalette.proteinsOnSurfaceContainer
                            )
                        },
                        value = {
                            val value = diaryDay.totalProteins.formatClipZeros()
                            val g = stringResource(Res.string.unit_gram_short)
                            Text(text = "$value $g")
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                item {
                    HorizontalDivider(Modifier.padding(horizontal = 48.dp))
                }

                item {
                    CaloriesScreenListItem(
                        label = {
                            Text(
                                text = stringResource(Res.string.nutriment_carbohydrates),
                                color = nutrientsPalette.carbohydratesOnSurfaceContainer
                            )
                        },
                        value = {
                            val value = diaryDay.totalCarbohydrates.formatClipZeros()
                            val g = stringResource(Res.string.unit_gram_short)
                            Text(text = "$value $g")
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                item {
                    HorizontalDivider(Modifier.padding(horizontal = 48.dp))
                }

                item {
                    CaloriesScreenListItem(
                        label = {
                            Text(
                                text = stringResource(Res.string.nutriment_sugars)
                            )
                        },
                        value = {
                            val summary = diaryDay.total(Nutrient.Sugars)
                            val g = stringResource(Res.string.unit_gram_short)
                            val prefix = summary.prefix()
                            val value = summary.value.formatClipZeros()
                            Text(
                                text = "$prefix $value $g",
                                color = summary.color()
                            )
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                item {
                    HorizontalDivider(Modifier.padding(horizontal = 48.dp))
                }

                item {
                    CaloriesScreenListItem(
                        label = {
                            Text(
                                text = stringResource(Res.string.nutriment_fats),
                                color = nutrientsPalette.fatsOnSurfaceContainer
                            )
                        },
                        value = {
                            val value = diaryDay.totalFats.formatClipZeros()
                            val g = stringResource(Res.string.unit_gram_short)
                            Text(text = "$value $g")
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                item {
                    HorizontalDivider(Modifier.padding(horizontal = 48.dp))
                }

                item {
                    CaloriesScreenListItem(
                        label = {
                            Text(
                                text = stringResource(Res.string.nutriment_saturated_fats)
                            )
                        },
                        value = {
                            val summary = diaryDay.total(Nutrient.SaturatedFats)
                            val g = stringResource(Res.string.unit_gram_short)
                            val prefix = summary.prefix()
                            val value = summary.value.formatClipZeros()
                            Text(
                                text = "$prefix $value $g",
                                color = summary.color(),
                                maxLines = 1
                            )
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                item {
                    HorizontalDivider(Modifier.padding(horizontal = 48.dp))
                }

                item {
                    CaloriesScreenListItem(
                        label = {
                            Text(
                                text = stringResource(Res.string.nutriment_salt)
                            )
                        },
                        value = {
                            val summary = diaryDay.total(Nutrient.Salt)
                            val g = stringResource(Res.string.unit_gram_short)
                            val prefix = summary.prefix()
                            val value = summary.value.formatClipZeros()
                            Text(
                                text = "$prefix $value $g",
                                color = summary.color()
                            )
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                item {
                    HorizontalDivider(Modifier.padding(horizontal = 48.dp))
                }

                item {
                    CaloriesScreenListItem(
                        label = {
                            Text(
                                text = stringResource(Res.string.nutriment_fiber)
                            )
                        },
                        value = {
                            val summary = diaryDay.total(Nutrient.Fiber)
                            val g = stringResource(Res.string.unit_gram_short)
                            val prefix = summary.prefix()
                            val value = summary.value.formatClipZeros()
                            Text(
                                text = "$prefix $value $g",
                                color = summary.color()
                            )
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                item {
                    HorizontalDivider(Modifier.padding(horizontal = 48.dp))
                }

                item {
                    CaloriesScreenListItem(
                        label = {
                            Text(
                                text = stringResource(Res.string.nutriment_sodium)
                            )
                        },
                        value = {
                            val summary = diaryDay.total(Nutrient.Sodium)
                            val g = stringResource(Res.string.unit_gram_short)
                            val prefix = summary.prefix()
                            val value = summary.value.formatClipZeros()
                            Text(
                                text = "$prefix $value $g",
                                color = summary.color()
                            )
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                item {
                    Text(
                        text =
                        PREFIX_INCOMPLETE_NUTRIENT_DATA + " " +
                            stringResource(Res.string.description_incomplete_nutrition_data),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CaloriesScreenListItem(
    label: @Composable () -> Unit,
    value: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            label()
        }
        value()
    }
}

@Composable
private fun DiaryDay.NutrientSummary.color(): Color = when (this) {
    is DiaryDay.NutrientSummary.Complete -> LocalTextStyle.current.color
    is DiaryDay.NutrientSummary.Incomplete -> MaterialTheme.colorScheme.outline
}

const val PREFIX_INCOMPLETE_NUTRIENT_DATA = "*"

private fun DiaryDay.NutrientSummary.prefix(): String = when (this) {
    is DiaryDay.NutrientSummary.Complete -> ""
    is DiaryDay.NutrientSummary.Incomplete -> PREFIX_INCOMPLETE_NUTRIENT_DATA
}
