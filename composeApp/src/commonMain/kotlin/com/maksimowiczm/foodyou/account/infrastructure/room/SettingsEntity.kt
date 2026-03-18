package com.maksimowiczm.foodyou.account.infrastructure.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.account.domain.EnergyFormat

@Entity(tableName = "AccountSettings")
internal data class SettingsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val onboardingFinished: Boolean,
    val energyFormat: EnergyFormat,
    val nutrientsOrder: String,
)
