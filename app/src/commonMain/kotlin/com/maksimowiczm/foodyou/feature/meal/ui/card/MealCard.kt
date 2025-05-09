package com.maksimowiczm.foodyou.feature.meal.ui.card

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ui.home.FoodYouHomeCard
import com.maksimowiczm.foodyou.core.ui.motion.crossfadeIn
import com.maksimowiczm.foodyou.core.ui.utils.LocalDateFormatter
import com.maksimowiczm.foodyou.feature.meal.domain.MealWithSummary
import com.maksimowiczm.foodyou.feature.meal.ui.card.MealCardTransitionSpecs.overlayClipFromCardToScreen
import com.maksimowiczm.foodyou.feature.meal.ui.component.MealHeader
import com.maksimowiczm.foodyou.feature.meal.ui.component.NutrientsLayout
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.action_add
import foodyou.app.generated.resources.em_dash
import foodyou.app.generated.resources.en_dash
import foodyou.app.generated.resources.headline_all_day
import foodyou.app.generated.resources.unit_gram_short
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun SharedTransitionScope.MealCard(
    animatedVisibilityScope: AnimatedVisibilityScope,
    epochDay: Int,
    meal: MealWithSummary,
    isEmpty: Boolean,
    totalCalories: Int,
    totalProteins: Int,
    totalCarbohydrates: Int,
    totalFats: Int,
    onMealClick: () -> Unit,
    onAddClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier.Companion
) {
    val dateFormatter = LocalDateFormatter.current

    FoodYouHomeCard(
        onClick = onMealClick,
        modifier = modifier.sharedBounds(
            sharedContentState = rememberSharedContentState(
                key = MealCardTransitionKeys.MealContainer(
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
        ),
        onLongClick = onLongClick
    ) {
        val headline = @Composable {
            Text(
                text = meal.name,
                modifier = Modifier.Companion.sharedElement(
                    sharedContentState = rememberSharedContentState(
                        key = MealCardTransitionKeys.MealTitle(
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
                modifier = Modifier.Companion.sharedElement(
                    sharedContentState = rememberSharedContentState(
                        key = MealCardTransitionKeys.MealTime(
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
                        text = remember(enDash, meal, dateFormatter) {
                            buildString {
                                append(dateFormatter.formatTime(meal.from))
                                append(" ")
                                append(enDash)
                                append(" ")
                                append(dateFormatter.formatTime(meal.to))
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
                    modifier = Modifier.Companion
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

        MealHeader(
            headline = headline,
            time = time,
            modifier = Modifier.Companion.padding(16.dp),
            nutrientsLayout = {
                Row(
                    modifier = Modifier.Companion.fillMaxWidth(),
                    verticalAlignment = Alignment.Companion.CenterVertically
                ) {
                    NutrientsLayout(
                        caloriesLabel = caloriesLabel,
                        proteinsLabel = proteinsLabel,
                        carbohydratesLabel = carbohydratesLabel,
                        fatsLabel = fatsLabel,
                        modifier = Modifier.Companion.sharedElement(
                            sharedContentState = rememberSharedContentState(
                                key = MealCardTransitionKeys.MealNutrients(
                                    mealId = meal.id,
                                    epochDay = epochDay
                                )
                            ),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    )

                    Spacer(Modifier.Companion.weight(1f))

                    actionButton()
                }
            }
        )
    }
}
