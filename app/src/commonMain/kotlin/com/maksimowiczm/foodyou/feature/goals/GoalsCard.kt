package com.maksimowiczm.foodyou.feature.goals

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.core.ui.home.HomeState

@Composable
fun GoalsCard(homeState: HomeState, modifier: Modifier = Modifier) {
    com.maksimowiczm.foodyou.feature.goals.ui.cardredesign.GoalsCard(
        homeState = homeState,
        modifier = modifier
    )
}
