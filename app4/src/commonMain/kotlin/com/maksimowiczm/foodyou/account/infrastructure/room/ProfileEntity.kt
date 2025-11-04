package com.maksimowiczm.foodyou.account.infrastructure.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "AccountProfile",
    primaryKeys = ["id", "accountId"],
    foreignKeys =
        [
            ForeignKey(
                entity = AccountEntity::class,
                parentColumns = ["id"],
                childColumns = ["accountId"],
                onDelete = ForeignKey.CASCADE,
            )
        ],
    indices = [Index("accountId", unique = true)],
)
data class ProfileEntity(
    val id: String,
    val accountId: String,
    val name: String,
    val avatar: String,
    val homeFeaturesOrder: String,
)
