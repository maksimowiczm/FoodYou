package com.maksimowiczm.foodyou.feature.importexport

import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.feature.importexport.ui.ImportExportScreen
import kotlinx.serialization.Serializable

@Serializable
data object ImportExport

fun NavGraphBuilder.importExportGraph(onBack: () -> Unit) {
    forwardBackwardComposable<ImportExport> {
        ImportExportScreen(
            onBack = onBack
        )
    }
}
