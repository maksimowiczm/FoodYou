package com.maksimowiczm.foodyou.feature.swissfoodcompositiondatabase

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import kotlinx.serialization.Serializable

@Serializable
data object ImportSwissFoodCompositionDatabase

fun NavGraphBuilder.swissFoodCompositionDatabaseGraph(onBack: () -> Unit) {
    forwardBackwardComposable<ImportSwissFoodCompositionDatabase> {
        Surface {
            Spacer(Modifier.fillMaxSize())
        }
    }
}
