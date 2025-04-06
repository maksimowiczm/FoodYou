package com.maksimowiczm.foodyou.ui.home

import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.core.navigation.crossfadeComposable
import kotlinx.serialization.Serializable

@Serializable
data object Home

fun NavGraphBuilder.homeGraph(onSettings: () -> Unit) {
    crossfadeComposable<Home> {
        HomeScreen(
            onSettings = onSettings
        )
    }
}
