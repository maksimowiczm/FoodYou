package com.maksimowiczm.foodyou.feature.fooddiary.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.core.ui.HomeState

@Composable
fun GoalsCard(
    homeState: HomeState,
    onClick: (epochDay: Long) -> Unit,
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
