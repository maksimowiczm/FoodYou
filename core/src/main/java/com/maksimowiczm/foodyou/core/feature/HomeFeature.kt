package com.maksimowiczm.foodyou.core.feature

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import java.time.LocalDate

@Stable
interface HomeState {
    val selectedDate: LocalDate

    fun selectDate(date: LocalDate)
}

/**
 * A feature that can be added to the home screen.
 */
fun interface HomeFeature {
    /**
     * Card that will be added to the home card list.
     */
    @Composable
    fun Card(modifier: Modifier, state: HomeState)
}
