package com.maksimowiczm.foodyou.business.fooddiary.infrastructure.preferences

import com.maksimowiczm.foodyou.business.fooddiary.domain.MealsPreferences
import kotlinx.coroutines.flow.Flow

internal interface LocalMealsPreferencesDataSource {
    fun observe(): Flow<MealsPreferences>

    suspend fun update(preferences: MealsPreferences)
}
