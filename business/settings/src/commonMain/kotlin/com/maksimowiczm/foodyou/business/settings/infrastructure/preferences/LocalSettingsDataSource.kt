package com.maksimowiczm.foodyou.business.settings.infrastructure.preferences

import com.maksimowiczm.foodyou.business.settings.domain.HomeCard
import com.maksimowiczm.foodyou.business.settings.domain.NutrientsOrder
import com.maksimowiczm.foodyou.business.settings.domain.Settings
import kotlinx.coroutines.flow.Flow

internal interface LocalSettingsDataSource {
    fun observe(): Flow<Settings>

    suspend fun updateLastRememberedVersion(version: String)

    suspend fun updateShowTranslationWarning(show: Boolean)

    suspend fun updateSecureScreen(secureScreen: Boolean)

    suspend fun updateNutrientsOrder(order: List<NutrientsOrder>)

    suspend fun updateHomeCardOrder(order: List<HomeCard>)
}
