package com.maksimowiczm.foodyou.feature.diary.ui.mealscard

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.feature.HomeFeature
import com.maksimowiczm.foodyou.feature.HomeState
import com.maksimowiczm.foodyou.feature.diary.data.model.Meal
import com.maksimowiczm.foodyou.feature.diary.ui.MealHeader
import com.maksimowiczm.foodyou.feature.diary.ui.MealHeaderTransitionKeys
import com.maksimowiczm.foodyou.feature.diary.ui.MealHeaderTransitionSpecs
import com.maksimowiczm.foodyou.feature.diary.ui.MealHeaderTransitionSpecs.overlayClipFromCardToScreen
import com.maksimowiczm.foodyou.feature.diary.ui.NutrientsLayout
import com.maksimowiczm.foodyou.ui.LocalHomeSharedTransitionScope
import com.maksimowiczm.foodyou.ui.ext.toDp
import com.maksimowiczm.foodyou.ui.home.FoodYouHomeCard
import com.maksimowiczm.foodyou.ui.motion.crossfadeIn
import com.maksimowiczm.foodyou.ui.preview.DiaryDayPreviewParameterProvider
import com.maksimowiczm.foodyou.ui.preview.SharedTransitionPreview
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import foodyou.app.generated.resources.*
import kotlin.math.absoluteValue
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

fun buildMealsCard(
    onMealClick: (epochDay: Int, meal: Meal) -> Unit,
    onAddClick: (epochDay: Int, meal: Meal) -> Unit
) = HomeFeature(
    applyPadding = false
) { animatedVisibilityScope, modifier, homeState ->
    MealsCard(
        animatedVisibilityScope = animatedVisibilityScope,
        homeState = homeState,
        onMealClick = onMealClick,
        onAddClick = onAddClick,
        modifier = modifier
    )
}

@Composable
fun MealsCard(
    animatedVisibilityScope: AnimatedVisibilityScope,
    homeState: HomeState,
    onMealClick: (epochDay: Int, meal: Meal) -> Unit,
    onAddClick: (epochDay: Int, meal: Meal) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MealsCardViewModel = koinViewModel()
) {
    val diaryDay by viewModel
        .observeDiaryDay(homeState.selectedDate)
        .collectAsStateWithLifecycle(null)

    val time by viewModel.time.collectAsStateWithLifecycle()

    val useTimeBasedSorting by viewModel.useTimeBasedSorting.collectAsStateWithLifecycle()

    val includeAllDayMeals by viewModel.includeAllDayMeals.collectAsStateWithLifecycle()

    MealsCard(
        animatedVisibilityScope = animatedVisibilityScope,
        state = rememberMealsCardState(
            timeBasedSorting = useTimeBasedSorting,
            includeAllDayMeals = includeAllDayMeals,
            diaryDay = diaryDay,
            time = time,
            shimmer = homeState.shimmer
        ),
        formatTime = viewModel::formatTime,
        onMealClick = { onMealClick(homeState.selectedDate.toEpochDays(), it) },
        onAddClick = { onAddClick(homeState.selectedDate.toEpochDays(), it) },
        modifier = modifier
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun MealsCard(
    animatedVisibilityScope: AnimatedVisibilityScope,
    state: MealsCardState,
    formatTime: (LocalTime) -> String,
    onMealClick: (Meal) -> Unit,
    onAddClick: (Meal) -> Unit,
    modifier: Modifier = Modifier
) {
    // Must be same as meals count or more but since we don't have meals count yet set it to some
    // extreme value. If it is less than actual meals count pager will scroll back to the
    // last item which is annoying for the user.
    // Let's assume that user won't use more than 20 meals
    val pagerState = rememberPagerState(
        pageCount = { state.diaryDay?.meals?.size ?: 20 }
    )

    val sharedTransitionScope =
        LocalHomeSharedTransitionScope.current ?: error("SharedTransitionScope not found")

    with(sharedTransitionScope) {
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

            Crossfade(
                targetState = state.diaryDay != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp)
                    .scale(
                        scaleX = 1f,
                        scaleY = lerp(0.9f, 1f, fraction)
                    )
            ) {
                if (it && meal != null && state.diaryDay != null) {
                    MealCard(
                        animatedVisibilityScope = animatedVisibilityScope,
                        epochDay = state.diaryDay.date.toEpochDays(),
                        meal = meal,
                        isEmpty = state.diaryDay.mealProductMap[meal]?.isEmpty() == true,
                        totalCalories = state.diaryDay.totalCalories(meal),
                        totalProteins = state.diaryDay.totalProteins(meal),
                        totalCarbohydrates = state.diaryDay.totalCarbohydrates(meal),
                        totalFats = state.diaryDay.totalFats(meal),
                        formatTime = formatTime,
                        onMealClick = { onMealClick(meal) },
                        onAddClick = { onAddClick(meal) }
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
    val headline = @Composable {
        Column {
            Box(
                modifier = Modifier
                    .shimmer(shimmerInstance)
                    .size(140.dp, MaterialTheme.typography.headlineMedium.toDp() - 4.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )
            Spacer(Modifier.height(4.dp))
        }
    }
    val time = @Composable {
        Box(
            modifier = Modifier
                .shimmer(shimmerInstance)
                .size(60.dp, MaterialTheme.typography.labelLarge.toDp())
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        )
    }
    val nutrientsLayout = @Composable {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .shimmer(shimmerInstance)
                    .size(120.dp, MaterialTheme.typography.labelMedium.toDp() * 2)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
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

    FoodYouHomeCard(
        modifier = modifier
    ) {
        MealHeader(
            modifier = Modifier.padding(16.dp),
            headline = headline,
            time = time,
            nutrientsLayout = nutrientsLayout
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.MealCard(
    animatedVisibilityScope: AnimatedVisibilityScope,
    epochDay: Int,
    meal: Meal,
    isEmpty: Boolean,
    totalCalories: Int,
    totalProteins: Int,
    totalCarbohydrates: Int,
    totalFats: Int,
    formatTime: (LocalTime) -> String,
    onMealClick: () -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FoodYouHomeCard(
        onClick = onMealClick,
        modifier = modifier.sharedBounds(
            sharedContentState = rememberSharedContentState(
                key = MealHeaderTransitionKeys.MealContainer(
                    mealId = meal.id,
                    epochDay = epochDay
                )
            ),
            animatedVisibilityScope = animatedVisibilityScope,
            enter = MealHeaderTransitionSpecs.containerEnterTransition,
            exit = MealHeaderTransitionSpecs.containerExitTransition,
            resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
            clipInOverlayDuringTransition = OverlayClip(
                animatedVisibilityScope.overlayClipFromCardToScreen()
            )
        )
    ) {
        val headline = @Composable {
            Text(
                text = meal.name,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState(
                        key = MealHeaderTransitionKeys.MealTitle(
                            mealId = meal.id,
                            epochDay = epochDay
                        )
                    ),
                    animatedVisibilityScope = animatedVisibilityScope
                )
            )
        }
        val time = @Composable {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState(
                        key = MealHeaderTransitionKeys.MealTime(
                            mealId = meal.id,
                            epochDay = epochDay
                        )
                    ),
                    animatedVisibilityScope = animatedVisibilityScope
                )
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.outline,
                    LocalTextStyle provides MaterialTheme.typography.labelLarge
                ) {
                    if (meal.isAllDay) {
                        Text(
                            text = stringResource(Res.string.headline_all_day)
                        )
                    } else {
                        Text(
                            text = formatTime(meal.from)
                        )
                        Text(
                            text = stringResource(Res.string.en_dash)
                        )
                        Text(
                            text = formatTime(meal.to)
                        )
                    }
                }
            }
        }

        val caloriesLabel = @Composable {
            Text(
                text = if (isEmpty) {
                    stringResource(Res.string.em_dash)
                } else {
                    totalCalories.toString()
                }
            )
        }
        val proteinsLabel = @Composable {
            Text(
                text = if (isEmpty) {
                    stringResource(Res.string.em_dash)
                } else {
                    "$totalProteins " + stringResource(Res.string.unit_gram_short)
                }
            )
        }
        val carbohydratesLabel = @Composable {
            Text(
                text = if (isEmpty) {
                    stringResource(Res.string.em_dash)
                } else {
                    "$totalCarbohydrates " + stringResource(Res.string.unit_gram_short)
                }
            )
        }
        val fatsLabel = @Composable {
            Text(
                text = if (isEmpty) {
                    stringResource(Res.string.em_dash)
                } else {
                    "$totalFats " + stringResource(Res.string.unit_gram_short)
                }
            )
        }

        val actionButton = @Composable {
            with(animatedVisibilityScope) {
                FilledIconButton(
                    onClick = onAddClick,
                    modifier = Modifier
                        .renderInSharedTransitionScopeOverlay(zIndexInOverlay = 1f)
                        .animateEnterExit(
                            enter = crossfadeIn(),
                            exit = fadeOut(tween(50))
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(Res.string.action_add)
                    )
                }
            }
        }

        val sharedTransitionScope =
            LocalHomeSharedTransitionScope.current ?: error("SharedTransitionScope not found")

        with(sharedTransitionScope) {
            MealHeader(
                headline = headline,
                time = time,
                modifier = Modifier.padding(16.dp),
                nutrientsLayout = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NutrientsLayout(
                            caloriesLabel = caloriesLabel,
                            proteinsLabel = proteinsLabel,
                            carbohydratesLabel = carbohydratesLabel,
                            fatsLabel = fatsLabel,
                            modifier = Modifier.sharedBounds(
                                sharedContentState = rememberSharedContentState(
                                    key = MealHeaderTransitionKeys.MealNutrients(
                                        mealId = meal.id,
                                        epochDay = epochDay
                                    )
                                ),
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                        )

                        Spacer(Modifier.weight(1f))

                        actionButton()
                    }
                }
            )
        }
    }
}

@Preview
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

    FoodYouTheme {
        SharedTransitionPreview {
            MealCard(
                animatedVisibilityScope = it,
                epochDay = 0,
                meal = meal,
                isEmpty = false,
                totalCalories = diaryDay.totalCalories(meal),
                totalProteins = diaryDay.totalProteins(meal),
                totalCarbohydrates = diaryDay.totalCarbohydrates(meal),
                totalFats = diaryDay.totalFats(meal),
                formatTime = { it.toString() },
                onMealClick = {},
                onAddClick = {}
            )
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

    FoodYouTheme {
        SharedTransitionPreview {
            MealCard(
                animatedVisibilityScope = it,
                epochDay = 0,
                meal = diaryDay.meals.first(),
                isEmpty = true,
                totalCalories = 0,
                totalProteins = 0,
                totalCarbohydrates = 0,
                totalFats = 0,
                formatTime = { it.toString() },
                onMealClick = {},
                onAddClick = {}
            )
        }
    }
}
