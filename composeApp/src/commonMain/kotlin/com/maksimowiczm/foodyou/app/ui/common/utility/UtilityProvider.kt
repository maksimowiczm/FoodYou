package com.maksimowiczm.foodyou.app.ui.common.utility

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.app.infrastructure.FoodYouConfig
import com.maksimowiczm.foodyou.common.domain.BlobResolver

@Composable
fun UtilityProvider(
    clipboardManager: ClipboardManager,
    dateFormatter: DateFormatter,
    foodNameSelector: FoodNameSelector,
    appConfig: FoodYouConfig,
    blobResolver: BlobResolver,
    uiFeatureFlags: UIFeatureFlags,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalClipboardManager provides clipboardManager,
        LocalDateFormatter provides dateFormatter,
        LocalFoodNameSelector provides foodNameSelector,
        LocalBlobResolver provides blobResolver,
        LocalAppConfig provides appConfig,
        LocalUIFeatureFlags provides uiFeatureFlags,
        content = content,
    )
}
