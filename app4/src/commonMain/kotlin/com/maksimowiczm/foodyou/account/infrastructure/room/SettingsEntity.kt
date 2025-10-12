package com.maksimowiczm.foodyou.account.infrastructure.room

import androidx.room.Entity
import com.maksimowiczm.foodyou.account.domain.EnergyFormat

@Entity(tableName = "AccountSettings", primaryKeys = ["accountId"])
data class SettingsEntity(
    val accountId: String,
    val onboardingFinished: Boolean,
    val energyFormat: EnergyFormat,
    val nutrientsOrder: String,
)
