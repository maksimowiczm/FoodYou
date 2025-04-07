package com.maksimowiczm.foodyou.feature.diary.openfoodfacts

import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import kotlinx.serialization.Serializable

@Serializable
data object OpenFoodFactsSettings

fun NavGraphBuilder.openFoodFactsSettingsGraph(onOpenFoodFactsSettingsBack: () -> Unit) {
    forwardBackwardComposable<OpenFoodFactsSettings> {
        OpenFoodFactsSettingsScreen(
            onBack = onOpenFoodFactsSettingsBack
        )
    }
}
