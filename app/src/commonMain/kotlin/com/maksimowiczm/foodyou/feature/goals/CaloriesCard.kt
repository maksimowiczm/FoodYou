package com.maksimowiczm.foodyou.feature.goals

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.core.ui.home.HomeState
import com.maksimowiczm.foodyou.feature.goals.ui.card.CaloriesCard

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CaloriesCard(
    animatedVisibilityScope: AnimatedVisibilityScope,
    homeState: HomeState,
    onClick: (epochDay: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    CaloriesCard(
        animatedVisibilityScope = animatedVisibilityScope,
        homeState = homeState,
        onClick = onClick,
        modifier = modifier
    )
}
