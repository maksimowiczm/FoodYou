package com.maksimowiczm.foodyou.core.feature.addfood.ui.search.searchbar

import android.os.Build
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun AddFoodSearchBar(
    searchBarState: SearchBarState,
    onSearchSettings: () -> Unit,
    onSearch: (String) -> Unit,
    onClearSearch: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    if (Build.VERSION.SDK_INT < 28) {
        LaunchedEffect(interactionSource) {
            var wasPressInteraction = false

            interactionSource.interactions.collect {
                if (wasPressInteraction && it is PressInteraction.Release) {
                    searchBarState.requestExpandedState(true)
                    wasPressInteraction = false
                } else {
                    wasPressInteraction = it is PressInteraction.Press
                }
            }
        }
    }

    AddFoodSearchBar(
        searchBarState = searchBarState,
        onSearchSettings = onSearchSettings,
        onSearch = onSearch,
        onClearSearch = onClearSearch,
        onBack = onBack,
        modifier = modifier,
        interactionSource = interactionSource
    )
}
