package com.maksimowiczm.foodyou.feature.diary.ui.mealscard

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
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
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.feature.diary.ui.SharedTransitionKeys
import com.maksimowiczm.foodyou.feature.diary.ui.previewparameter.DiaryDayPreviewParameterProvider
import com.maksimowiczm.foodyou.feature.diary.ui.theme.LocalNutrientsPalette
import com.maksimowiczm.foodyou.ui.LocalSharedTransitionScope
import com.maksimowiczm.foodyou.ui.motion.crossfadeIn
import com.maksimowiczm.foodyou.ui.motion.crossfadeOut
import com.maksimowiczm.foodyou.ui.preview.SharedTransitionPreview
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import com.maksimowiczm.foodyou.ui.toDp
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import kotlin.math.absoluteValue
import kotlinx.datetime.LocalTime

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MealsCard(
    animatedVisibilityScope: AnimatedVisibilityScope,
    state: MealsCardState,
    formatTime: (LocalTime) -> String,
    onAdd: (Meal) -> Unit,
    onEdit: (Meal) -> Unit,
    modifier: Modifier = Modifier
) {
    val sharedTransitionScope =
        LocalSharedTransitionScope.current ?: error("No shared transition scope found")

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
        val meal = state.meals?.getOrNull(page)

        with(sharedTransitionScope) {
            Crossfade(
                targetState = state.diaryDay != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp)
                    .scale(
                        scaleX = 1f,
                        scaleY = lerp(0.9f, 1f, fraction)
                    )
                    .then(
                        if (state.diaryDay != null && meal != null) {
                            Modifier.sharedBounds(
                                sharedContentState = rememberSharedContentState(
                                    key = SharedTransitionKeys.Meal(
                                        id = meal.id,
                                        epochDay = state.diaryDay.date.toEpochDays()
                                    )
                                ),
                                animatedVisibilityScope = animatedVisibilityScope,
                                enter = crossfadeIn(),
                                exit = crossfadeOut()
                            )
                        } else {
                            Modifier
                        }
                    )

            ) {
                if (it && meal != null && state.diaryDay != null) {
                    MealCard(
                        animatedVisibilityScope = animatedVisibilityScope,
                        meal = meal,
                        epochDay = state.diaryDay.date.toEpochDays(),
                        isEmpty = state.diaryDay.mealProductMap[meal]?.isEmpty() == true,
                        totalCalories = state.diaryDay.totalCalories(meal),
                        totalProteins = state.diaryDay.totalProteins(meal),
                        totalCarbohydrates = state.diaryDay.totalCarbohydrates(meal),
                        totalFats = state.diaryDay.totalFats(meal),
                        formatTime = formatTime,
                        onAddClick = { onAdd(meal) },
                        onEditClick = { onEdit(meal) },
                        modifier = Modifier
                    )
                } else {
                    MealCardSkeleton(
                        shimmerInstance = state.shimmer
                    )
                }
            }
        }
    }
}

@Composable
fun MealCardSkeleton(shimmerInstance: Shimmer, modifier: Modifier = Modifier) {
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.MealCard(
    animatedVisibilityScope: AnimatedVisibilityScope,
    epochDay: Int,
    meal: Meal,
    isEmpty: Boolean,
    totalCalories: Int,
    totalProteins: Int,
    totalCarbohydrates: Int,
    totalFats: Int,
    formatTime: (LocalTime) -> String,
    onAddClick: () -> Unit,
    onEditClick: () -> Unit,
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
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState(
                        key = SharedTransitionKeys.Meal.Title(
                            id = meal.id,
                            epochDay = epochDay
                        )
                    ),
                    animatedVisibilityScope = animatedVisibilityScope
                )
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
                    onClick = {
                        if (!isEmpty) {
                            onEditClick()
                        } else {
                            onAddClick()
                        }
                    }
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
private fun MacroLayoutSkeleton(modifier: Modifier = Modifier) {
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
    val nutrientsPalette = LocalNutrientsPalette.current

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
                    LocalContentColor provides nutrientsPalette.proteinsOnSurfaceContainer
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
                    LocalContentColor provides nutrientsPalette.carbohydratesOnSurfaceContainer
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
                    LocalContentColor provides nutrientsPalette.fatsOnSurfaceContainer
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun MealsCardPreview() {
    val diaryDay = DiaryDayPreviewParameterProvider().values.first()
    val meal = diaryDay.meals.first()

    SharedTransitionPreview { stc, animatedVisibilityScope ->
        FoodYouTheme {
            with(stc) {
                MealCard(
                    animatedVisibilityScope = animatedVisibilityScope,
                    epochDay = 0,
                    meal = meal,
                    isEmpty = false,
                    totalCalories = diaryDay.totalCalories(meal),
                    totalProteins = diaryDay.totalProteins(meal),
                    totalCarbohydrates = diaryDay.totalCarbohydrates(meal),
                    totalFats = diaryDay.totalFats(meal),
                    onAddClick = {},
                    onEditClick = {},
                    formatTime = { it.toString() }
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun EmptyMealsCardPreview() {
    val init = DiaryDayPreviewParameterProvider().values.first()
    val diaryDay = init.copy(
        mealProductMap = init.mealProductMap.mapValues { emptyList() }
    )

    SharedTransitionPreview { stc, animatedVisibilityScope ->
        FoodYouTheme {
            with(stc) {
                MealCard(
                    animatedVisibilityScope = animatedVisibilityScope,
                    meal = diaryDay.meals.first(),
                    epochDay = 0,
                    isEmpty = true,
                    totalCalories = 0,
                    totalProteins = 0,
                    totalCarbohydrates = 0,
                    totalFats = 0,
                    onAddClick = {},
                    onEditClick = {},
                    formatTime = { it.toString() }
                )
            }
        }
    }
}
