package com.maksimowiczm.foodyou.account.infrastructure.room

import androidx.room.Entity

@Entity(tableName = "AccountProfile", primaryKeys = ["id", "localAccountId"])
data class ProfileEntity(val id: String, val localAccountId: String, val name: String)
