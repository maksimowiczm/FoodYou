package com.maksimowiczm.foodyou.feature.about

import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.feature.about.ui.AboutScreen
import kotlinx.serialization.Serializable

@Serializable
data object About

fun NavGraphBuilder.aboutGraph() {
    forwardBackwardComposable<About> {
        AboutScreen()
    }
}
