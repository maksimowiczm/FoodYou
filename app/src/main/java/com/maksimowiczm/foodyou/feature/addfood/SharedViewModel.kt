package com.maksimowiczm.foodyou.feature.addfood

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel

// https://stackoverflow.com/a/76957580

/**
 * Share a ViewModel between destinations in the same navigation graph.
 *
 * @see [com.maksimowiczm.foodyou.feature.addfood.AddFoodFeature.graph]
 */
@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavController
): T {
    val navGraphRoute = destination.parent?.route ?: return koinViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return koinViewModel(
        viewModelStoreOwner = parentEntry
    )
}
