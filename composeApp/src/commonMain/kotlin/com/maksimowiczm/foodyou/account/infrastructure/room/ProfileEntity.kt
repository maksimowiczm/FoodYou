package com.maksimowiczm.foodyou.account.infrastructure.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.uuid.Uuid

@Entity(tableName = "AccountProfile")
internal data class ProfileEntity(
    @PrimaryKey val id: Uuid,
    val name: String,
    val avatar: String,
    val homeFeaturesOrder: String,
)
