package com.maksimowiczm.foodyou.core.feature

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import kotlinx.datetime.LocalDate

@Stable
interface HomeState {
    val selectedDate: LocalDate

    fun selectDate(date: LocalDate)
}

/**
 * A feature that can be added to the home screen.
 */
@Immutable
data class HomeFeature(
    /**
     * Whether to apply padding to the card. Useful for cards that uses the whole screen width.
     * @see [com.maksimowiczm.foodyou.core.feature.diary.ui.nutrimentscard.buildNutrimentsCard]
     */
    val applyPadding: Boolean = true,
    val card: HomeCard
)

/**
 * Card that will be added to the home card list.
 */
fun interface HomeCard {
    @Composable
    operator fun invoke(modifier: Modifier, homeState: HomeState)
}
