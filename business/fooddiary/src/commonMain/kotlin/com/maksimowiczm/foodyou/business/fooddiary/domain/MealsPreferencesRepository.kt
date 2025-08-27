package com.maksimowiczm.foodyou.business.fooddiary.domain

import kotlinx.coroutines.flow.Flow

interface MealsPreferencesRepository {
    fun observe(): Flow<MealsPreferences>

    suspend fun update(transform: MealsPreferences.() -> MealsPreferences)
}
