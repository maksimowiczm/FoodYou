package com.maksimowiczm.foodyou.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.business.settings.application.query.ObserveSettingsQuery
import com.maksimowiczm.foodyou.business.settings.domain.NutrientsOrder
import com.maksimowiczm.foodyou.business.settings.domain.Settings
import com.maksimowiczm.foodyou.feature.about.master.ui.AppUpdateChangelogModalBottomSheet
import com.maksimowiczm.foodyou.feature.about.master.ui.PreviewReleaseDialog
import com.maksimowiczm.foodyou.feature.settings.language.ui.TranslationWarningStartupDialog
import com.maksimowiczm.foodyou.feature.shared.ui.NutrientsOrderProvider
import com.maksimowiczm.foodyou.navigation.FoodYouNavHost
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryBus
import com.maksimowiczm.foodyou.shared.ui.theme.FoodYouTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.compose.koinInject

@Composable
fun FoodYouApp() {
    val queryBus: QueryBus = koinInject()
    val nutrientsOrder by
        queryBus.observeNutrientsOrder().collectAsStateWithLifecycle(NutrientsOrder.defaultOrder)

    NutrientsOrderProvider(nutrientsOrder) {
        FoodYouTheme {
            PreviewReleaseDialog()
            TranslationWarningStartupDialog()

            Surface {
                FoodYouNavHost()
                AppUpdateChangelogModalBottomSheet()
            }
        }
    }
}

private fun QueryBus.observeNutrientsOrder(): Flow<List<NutrientsOrder>> =
    dispatch<Settings>(ObserveSettingsQuery).map { it.nutrientsOrder }
