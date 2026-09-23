package com.maksimowiczm.foodyou.preferences.domain

import com.maksimowiczm.foodyou.common.domain.EnergyUnit
import kotlin.jvm.JvmInline

@JvmInline value class EnergyUnitPreference(val energyUnit: EnergyUnit) : UserPreferences
