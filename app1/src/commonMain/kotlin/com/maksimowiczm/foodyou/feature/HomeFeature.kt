package com.maksimowiczm.foodyou.feature

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import com.valentinilk.shimmer.Shimmer
import kotlinx.datetime.LocalDate

@Stable
interface HomeState {
    val selectedDate: LocalDate

    fun selectDate(date: LocalDate)

    /**
     * Shimmer instance that can be used coordinate shimmer animations across different cards.
     */
    val shimmer: Shimmer
}

/**
 * A feature that can be added to the home screen.
 */
data class HomeFeature(
    /**
     * Whether to apply padding to the card. Useful for cards that uses the whole screen width.
     */
    val applyPadding: Boolean = true,
    val card: HomeCard
)

/**
 * Card that will be added to the home card list.
 */
fun interface HomeCard {
    @Composable
    operator fun invoke(
        animatedVisibilityScope: AnimatedVisibilityScope,
        modifier: Modifier,
        homeState: HomeState
    )
}
