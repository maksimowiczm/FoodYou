package com.maksimowiczm.foodyou.app.application

import com.maksimowiczm.foodyou.common.domain.EnergyUnit
import com.maksimowiczm.foodyou.preferences.domain.EnergyUnitPreference
import com.maksimowiczm.foodyou.preferences.domain.UserPreferencesRepository
import com.maksimowiczm.foodyou.preferences.domain.observe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

fun interface ObserveEnergyUnitUseCase {
    fun observe(): Flow<EnergyUnit>

    suspend fun get() = observe().first()
}

class ObserveEnergyUnitUseCaseImpl(private val preferencesRepository: UserPreferencesRepository) :
    ObserveEnergyUnitUseCase {
    override fun observe(): Flow<EnergyUnit> =
        preferencesRepository.observe<EnergyUnitPreference>().map { it.energyUnit }
}
