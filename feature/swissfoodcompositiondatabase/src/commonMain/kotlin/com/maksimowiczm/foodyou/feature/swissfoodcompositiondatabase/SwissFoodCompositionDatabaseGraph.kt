package com.maksimowiczm.foodyou.feature.swissfoodcompositiondatabase

import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.feature.swissfoodcompositiondatabase.ui.SwissFoodCompositionDatabaseScreen
import kotlinx.serialization.Serializable

@Serializable
data object ImportSwissFoodCompositionDatabase

fun NavGraphBuilder.swissFoodCompositionDatabaseGraph(onBack: () -> Unit) {
    forwardBackwardComposable<ImportSwissFoodCompositionDatabase> {
        SwissFoodCompositionDatabaseScreen(
            onBack = onBack
        )
    }
}
