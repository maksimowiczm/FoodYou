package com.maksimowiczm.foodyou.feature.openfoodfacts

import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import kotlinx.serialization.Serializable

@Serializable
data object OpenFoodFactsSettings

fun NavGraphBuilder.openFoodFactsGraph(onOpenFoodFactsSettingsBack: () -> Unit) {
    forwardBackwardComposable<OpenFoodFactsSettings> {
        OpenFoodFactsSettingsScreen(
            onBack = onOpenFoodFactsSettingsBack
        )
    }
}
