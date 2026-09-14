package com.maksimowiczm.foodyou.shared.ui.utility

import androidx.compose.runtime.*
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.maksimowiczm.foodyou.app.infrastructure.FoodYouConfig
import com.maksimowiczm.foodyou.common.domain.BlobResolver

@Composable
fun UtilityProvider(
    dateFormatter: DateFormatter,
    foodNameSelector: FoodNameSelector,
    appConfig: FoodYouConfig,
    blobResolver: BlobResolver,
    uiFeatureFlags: UIFeatureFlags,
    dataStore: DataStore<Preferences>,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalDateFormatter provides dateFormatter,
        LocalFoodNameSelector provides foodNameSelector,
        LocalBlobResolver provides blobResolver,
        LocalAppConfig provides appConfig,
        LocalUIFeatureFlags provides uiFeatureFlags,
        LocalDataStore provides dataStore,
        content = content,
    )
}
