package com.maksimowiczm.foodyou.feature.goals

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.core.ui.home.HomeState
import com.maksimowiczm.foodyou.feature.goals.ui.card.GoalsCard

@Composable
fun GoalsCard(
    homeState: HomeState,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GoalsCard(
        homeState = homeState,
        onClick = onClick,
        onLongClick = onLongClick,
        modifier = modifier
    )
}
