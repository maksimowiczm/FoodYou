package com.maksimowiczm.foodyou.account.infrastructure.room

import androidx.room.Entity

@Entity(tableName = "AccountSettings", primaryKeys = ["accountId"])
data class SettingsEntity(val accountId: String, val onboardingFinished: Boolean)
