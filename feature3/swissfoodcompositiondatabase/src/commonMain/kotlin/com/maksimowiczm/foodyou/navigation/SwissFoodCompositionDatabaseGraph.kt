package com.maksimowiczm.foodyou.navigation

import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.feature.swissfoodcompositiondatabase.ui.SwissFoodCompositionDatabaseScreen
import kotlinx.serialization.Serializable

@Serializable
data object SwissFoodCompositionDatabase

fun NavGraphBuilder.swissFoodCompositionDatabaseGraph(
    swissFoodCompositionDatabaseOnBack: () -> Unit
) {
    forwardBackwardComposable<SwissFoodCompositionDatabase> {
        SwissFoodCompositionDatabaseScreen(
            onBack = swissFoodCompositionDatabaseOnBack
        )
    }
}
