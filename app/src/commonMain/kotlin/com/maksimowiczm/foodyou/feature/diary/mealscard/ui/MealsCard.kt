package com.maksimowiczm.foodyou.feature.diary.mealscard.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.ui.LocalHomeSharedTransitionScope
import com.maksimowiczm.foodyou.core.ui.ext.toDp
import com.maksimowiczm.foodyou.core.ui.home.FoodYouHomeCard
import com.maksimowiczm.foodyou.core.ui.home.HomeState
import com.maksimowiczm.foodyou.core.ui.motion.crossfadeIn
import com.maksimowiczm.foodyou.feature.diary.mealscard.MealCardTransitionSpecs
import com.maksimowiczm.foodyou.feature.diary.mealscard.MealCardTransitionSpecs.overlayClipFromCardToScreen
import com.maksimowiczm.foodyou.feature.diary.mealscard.MealHeaderTransitionKeys
import com.maksimowiczm.foodyou.feature.diary.mealscard.domain.Meal
import com.maksimowiczm.foodyou.feature.diary.ui.MealHeader
import com.maksimowiczm.foodyou.feature.diary.ui.NutrientsLayout
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import foodyou.app.generated.resources.*
import kotlin.math.absoluteValue
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun MealsCard(
    animatedVisibilityScope: AnimatedVisibilityScope,
    homeState: HomeState,
    onMealClick: (epochDay: Int, mealId: Long) -> Unit,
    onAddClick: (epochDay: Int, mealId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MealsCardViewModel = koinViewModel()
) {
    val meals by viewModel.meals.collectAsStateWithLifecycle()

    MealsCard(
        meals = meals,
        formatTime = remember(viewModel) { viewModel::formatTime },
        onMealClick = { onMealClick(homeState.selectedDate.toEpochDays(), it) },
        onAddClick = { onAddClick(homeState.selectedDate.toEpochDays(), it) },
        animatedVisibilityScope = animatedVisibilityScope,
        epochDay = homeState.selectedDate.toEpochDays(),
        modifier = modifier,
        shimmer = homeState.shimmer
    )
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalAnimationApi::class)
@Composable
private fun MealsCard(
    meals: List<Meal>?,
    formatTime: (LocalTime) -> String,
    onMealClick: (mealId: Long) -> Unit,
    onAddClick: (mealId: Long) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    epochDay: Int,
    modifier: Modifier = Modifier,
    shimmer: Shimmer = rememberShimmer(
        shimmerBounds = ShimmerBounds.Window
    )
) {
    // Must be same as meals count or more but since we don't have meals count yet set it to some
    // extreme value. If it is less than actual meals count pager will scroll back to the
    // last item which is annoying for the user.
    // Let's assume that user won't use more than 20 meals
    val pagerState = rememberPagerState(
        pageCount = { meals?.size ?: 20 }
    )

    val sharedTransitionScope =
        LocalHomeSharedTransitionScope.current ?: error("SharedTransitionScope not found")

    val transition = updateTransition(meals)

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
            val meal = meals?.getOrNull(page)

            transition.Crossfade(
                contentKey = { it != null },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp)
                    .scale(
                        scaleX = 1f,
                        scaleY = lerp(0.9f, 1f, fraction)
                    )
            ) {
                if (it != null && meal != null) {
                    MealCard(
                        animatedVisibilityScope = animatedVisibilityScope,
                        epochDay = epochDay,
                        meal = meal,
                        isEmpty = meal.isEmpty,
                        totalCalories = meal.calories,
                        totalProteins = meal.proteins,
                        totalCarbohydrates = meal.carbohydrates,
                        totalFats = meal.fats,
                        formatTime = formatTime,
                        onMealClick = { onMealClick(meal.id) },
                        onAddClick = { onAddClick(meal.id) }
                    )
                } else {
                    MealCardSkeleton(
                        shimmer = shimmer
                    )
                }
            }
        }
    }
}

@Composable
private fun MealCardSkeleton(shimmer: Shimmer, modifier: Modifier = Modifier) {
    val headline = @Composable {
        Column {
            Box(
                modifier = Modifier
                    .shimmer(shimmer)
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
                .shimmer(shimmer)
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
                    .shimmer(shimmer)
                    .size(120.dp, MaterialTheme.typography.labelMedium.toDp() * 2)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )

            Spacer(Modifier.weight(1f))

            FilledIconButton(
                onClick = {},
                modifier = Modifier.shimmer(shimmer),
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
            enter = MealCardTransitionSpecs.containerEnterTransition,
            exit = MealCardTransitionSpecs.containerExitTransition,
            resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
            clipInOverlayDuringTransition = OverlayClip(
                animatedVisibilityScope.overlayClipFromCardToScreen()
            )
        )
    ) {
        val headline = @Composable {
            Text(
                text = meal.name,
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
                if (meal.isAllDay) {
                    Text(
                        text = stringResource(Res.string.headline_all_day),
                        color = MaterialTheme.colorScheme.outline
                    )
                } else {
                    val enDash = stringResource(Res.string.en_dash)

                    Text(
                        text = remember(enDash, meal, formatTime) {
                            buildString {
                                append(formatTime(meal.from))
                                append(enDash)
                                append(formatTime(meal.to))
                            }
                        },
                        color = MaterialTheme.colorScheme.outline
                    )
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
