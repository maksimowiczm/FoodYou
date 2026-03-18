package com.maksimowiczm.foodyou.account.infrastructure.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AccountProfile")
internal data class ProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val avatar: String,
    val homeFeaturesOrder: String,
)
