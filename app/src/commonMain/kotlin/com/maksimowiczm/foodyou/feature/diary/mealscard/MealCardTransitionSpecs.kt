package com.maksimowiczm.foodyou.feature.diary.mealscard

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.animateDp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ui.home.FoodYouHomeCardDefaults

object MealHeaderTransitionKeys {
    data class MealContainer(val mealId: Long, val epochDay: Int)

    data class MealTitle(val mealId: Long, val epochDay: Int)

    data class MealTime(val mealId: Long, val epochDay: Int)

    data class MealNutrients(val mealId: Long, val epochDay: Int)
}

object MealCardTransitionSpecs {
    val containerColor: Color
        @Composable get() = FoodYouHomeCardDefaults.colors().containerColor

    val containerEnterTransition: EnterTransition = EnterTransition.None
    val containerExitTransition: ExitTransition = ExitTransition.None

    private val cardCornerRadius: Dp
        get() = 12.dp

    @Composable
    fun AnimatedVisibilityScope.overlayClipFromScreenToCard(): Shape {
        val roundedCornerAnimation by transition.animateDp {
            when (it) {
                EnterExitState.PreEnter -> 0.dp
                EnterExitState.Visible -> 0.dp
                EnterExitState.PostExit -> cardCornerRadius
            }
        }

        return RoundedCornerShape(roundedCornerAnimation)
    }

    @Composable
    fun AnimatedVisibilityScope.overlayClipFromCardToScreen(): Shape {
        val roundedCornerAnimation by transition.animateDp {
            when (it) {
                EnterExitState.PreEnter -> 0.dp
                EnterExitState.Visible -> cardCornerRadius
                EnterExitState.PostExit -> 0.dp
            }
        }

        return RoundedCornerShape(roundedCornerAnimation)
    }
}
