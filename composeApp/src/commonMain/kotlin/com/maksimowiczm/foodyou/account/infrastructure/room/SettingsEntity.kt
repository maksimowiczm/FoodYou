package com.maksimowiczm.foodyou.account.infrastructure.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.common.domain.EnergyUnit

@Entity(tableName = "AccountSettings")
internal data class SettingsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val onboardingFinished: Boolean,
    val energyUnit: EnergyUnit,
    val nutrientsOrder: String,
)
