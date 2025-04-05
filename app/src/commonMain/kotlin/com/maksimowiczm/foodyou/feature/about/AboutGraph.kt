package com.maksimowiczm.foodyou.feature.about

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.maksimowiczm.foodyou.feature.about.ui.AboutScreen
import kotlinx.serialization.Serializable

@Serializable
data object About

fun NavGraphBuilder.aboutGraph() {
    composable<About> {
        AboutScreen()
    }
}
