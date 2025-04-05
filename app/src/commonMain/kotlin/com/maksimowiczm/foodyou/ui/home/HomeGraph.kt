package com.maksimowiczm.foodyou.ui.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object Home

fun NavGraphBuilder.homeGraph(onSettings: () -> Unit) {
    composable<Home> {
        HomeScreen(
            onSettings = onSettings
        )
    }
}
