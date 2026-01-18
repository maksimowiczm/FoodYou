package com.maksimowiczm.foodyou.app.ui.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.maksimowiczm.foodyou.account.domain.HomeCard
import com.maksimowiczm.foodyou.app.ui.home.calendar.CalendarHomeCard

val homeCardComposables = listOf(CalendarHomeCard)

val homeCardComposablesMap = homeCardComposables.associateBy { it.feature }

interface HomeCardComposable {
    val feature: HomeCard

    /** A composable that will be displayed on the home screen */
    @Composable
    fun HomeCard(homeState: HomeState, paddingValues: PaddingValues, modifier: Modifier = Modifier)

    @Composable
    fun HomeCardPersonalizationCard(
        paddingValues: PaddingValues,
        containerColor: Color,
        contentColor: Color,
        shadowElevation: Dp,
        modifier: Modifier = Modifier,
        dragHandle: @Composable (() -> Unit),
    )
}
