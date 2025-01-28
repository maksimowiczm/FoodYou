package com.maksimowiczm.foodyou.feature.addfood.ui.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Composable
fun rememberSearchBottomBarState(
    totalCalories: Int = 0
): SearchBottomBarState {
    return rememberSaveable(
        saver = Saver(
            save = { state ->
                state.totalCalories
            },
            restore = {
                SearchBottomBarState(
                    totalCalories = it
                )
            }
        )
    ) {
        SearchBottomBarState(
            totalCalories = totalCalories
        )
    }
}

@Stable
class SearchBottomBarState(
    totalCalories: Int
) {
    var totalCalories: Int by mutableIntStateOf(totalCalories)
        private set
}
