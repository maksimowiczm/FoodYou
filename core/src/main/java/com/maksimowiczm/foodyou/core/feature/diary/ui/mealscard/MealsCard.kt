package com.maksimowiczm.foodyou.core.feature.diary.ui.mealscard

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.maksimowiczm.foodyou.core.R
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.core.feature.diary.ui.previewparameter.DiaryDayPreviewParameterProvider
import com.maksimowiczm.foodyou.core.feature.diary.ui.theme.LocalNutrimentsPalette
import com.maksimowiczm.foodyou.core.ui.theme.FoodYouTheme
import com.maksimowiczm.foodyou.core.ui.toDp
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import kotlinx.datetime.LocalTime
import kotlin.math.absoluteValue

@Composable
fun MealsCard(
    state: MealsCardState,
    formatTime: (LocalTime) -> String,
    onAddProduct: (Meal) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(
        pageCount = { state.diaryDay?.meals?.size ?: 4 }
    )

    HorizontalPager(
        state = pagerState,
        modifier = modifier.animateContentSize(),
        contentPadding = PaddingValues(
            start = 8.dp,
            end = 24.dp
        )
    ) { page ->
        val pageOffset = pagerState.currentPage - page + pagerState.currentPageOffsetFraction
        val fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)

        AnimatedContent(
            targetState = state.diaryDay != null,
            transitionSpec = { fadeIn(tween()) togetherWith fadeOut(tween()) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp)
                .scale(
                    scaleX = 1f,
                    scaleY = lerp(0.9f, 1f, fraction)
                )
        ) {
            val meal = state.meals?.getOrNull(page)

            if (it && meal != null && state.diaryDay != null) {
                MealCard(
                    meal = meal,
                    isEmpty = state.diaryDay.mealProductMap[meal]?.isEmpty() == true,
                    totalCalories = state.diaryDay.totalCalories(meal),
                    totalProteins = state.diaryDay.totalProteins(meal),
                    totalCarbohydrates = state.diaryDay.totalCarbohydrates(meal),
                    totalFats = state.diaryDay.totalFats(meal),
                    formatTime = formatTime,
                    onAddClick = { onAddProduct(meal) }
                )
            } else {
                MealCardSkeleton(
                    shimmerInstance = state.shimmer
                )
            }
        }
    }
}

@Composable
fun MealCardSkeleton(
    shimmerInstance: Shimmer,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .shimmer(shimmerInstance)
                    .size(140.dp, MaterialTheme.typography.headlineMedium.toDp() - 4.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )

            Spacer(Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .shimmer(shimmerInstance)
                    .size(60.dp, MaterialTheme.typography.labelLarge.toDp())
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MacroLayoutSkeleton(
                    modifier = Modifier.shimmer(shimmerInstance)
                )

                Spacer(Modifier.weight(1f))

                FilledIconButton(
                    onClick = {},
                    modifier = Modifier.shimmer(shimmerInstance),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    ),
                    enabled = false,
                    content = {}
                )
            }
        }
    }
}

@Composable
private fun MealCard(
    meal: Meal,
    isEmpty: Boolean,
    totalCalories: Int,
    totalProteins: Int,
    totalCarbohydrates: Int,
    totalFats: Int,
    formatTime: (LocalTime) -> String,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = meal.name,
                style = MaterialTheme.typography.headlineMedium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.outline,
                    LocalTextStyle provides MaterialTheme.typography.labelLarge
                ) {
                    Text(
                        text = formatTime(meal.from)
                    )
                    Text(
                        text = stringResource(R.string.en_dash)
                    )
                    Text(
                        text = formatTime(meal.to)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MacroLayout(
                    caloriesLabel = {
                        Text(
                            text = if (isEmpty) {
                                stringResource(R.string.em_dash)
                            } else {
                                totalCalories.toString()
                            }
                        )
                    },
                    proteinsLabel = {
                        Text(
                            text = if (isEmpty) {
                                stringResource(R.string.em_dash)
                            } else {
                                "$totalProteins " + stringResource(R.string.unit_gram_short)
                            }
                        )
                    },
                    carbohydratesLabel = {
                        Text(
                            text = if (isEmpty) {
                                stringResource(R.string.em_dash)
                            } else {
                                "$totalCarbohydrates " + stringResource(R.string.unit_gram_short)
                            }
                        )
                    },
                    fatsLabel = {
                        Text(
                            text = if (isEmpty) {
                                stringResource(R.string.em_dash)
                            } else {
                                "$totalFats " + stringResource(R.string.unit_gram_short)
                            }
                        )
                    },
                    modifier = Modifier.weight(1f)
                )

                FilledIconButton(
                    onClick = onAddClick
                ) {
                    if (!isEmpty) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.action_log_products)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.action_log_products)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MacroLayoutSkeleton(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(120.dp, MaterialTheme.typography.labelMedium.toDp() * 2)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
    )
}

@Composable
private fun MacroLayout(
    caloriesLabel: @Composable () -> Unit,
    proteinsLabel: @Composable () -> Unit,
    carbohydratesLabel: @Composable () -> Unit,
    fatsLabel: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val nutrimentsPalette = LocalNutrimentsPalette.current

    CompositionLocalProvider(
        LocalTextStyle provides MaterialTheme.typography.labelMedium
    ) {
        FlowRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.unit_kcal)
                )
                caloriesLabel()
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides nutrimentsPalette.proteinsOnSurfaceContainer
                ) {
                    Text(
                        text = stringResource(R.string.nutriment_proteins_short)
                    )
                    proteinsLabel()
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides nutrimentsPalette.carbohydratesOnSurfaceContainer
                ) {
                    Text(
                        text = stringResource(R.string.nutriment_carbohydrates_short)
                    )
                    carbohydratesLabel()
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides nutrimentsPalette.fatsOnSurfaceContainer
                ) {
                    Text(
                        text = stringResource(R.string.nutriment_fats_short)
                    )
                    fatsLabel()
                }
            }
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun MealsCardSkeletonPreview() {
    FoodYouTheme {
        MealCardSkeleton(
            shimmerInstance = rememberShimmer(shimmerBounds = ShimmerBounds.View)
        )
    }
}

@Preview
@Composable
private fun MealsCardPreview() {
    val diaryDay = DiaryDayPreviewParameterProvider().values.first()
    val meal = diaryDay.meals.first()

    FoodYouTheme {
        MealCard(
            meal = meal,
            isEmpty = false,
            totalCalories = diaryDay.totalCalories(meal),
            totalProteins = diaryDay.totalProteins(meal),
            totalCarbohydrates = diaryDay.totalCarbohydrates(meal),
            totalFats = diaryDay.totalFats(meal),
            onAddClick = {},
            formatTime = { it.toString() }
        )
    }
}

@Preview
@Composable
private fun EmptyMealsCardPreview() {
    val init = DiaryDayPreviewParameterProvider().values.first()
    val diaryDay = init.copy(
        mealProductMap = init.mealProductMap.mapValues { emptyList() }
    )

    FoodYouTheme {
        MealCard(
            meal = diaryDay.meals.first(),
            isEmpty = true,
            totalCalories = 0,
            totalProteins = 0,
            totalCarbohydrates = 0,
            totalFats = 0,
            onAddClick = {},
            formatTime = { it.toString() }
        )
    }
}
