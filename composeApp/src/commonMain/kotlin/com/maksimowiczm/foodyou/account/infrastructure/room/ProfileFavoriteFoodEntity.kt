package com.maksimowiczm.foodyou.account.infrastructure.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ProfileFavoriteFood",
    foreignKeys =
        [
            ForeignKey(
                entity = ProfileEntity::class,
                parentColumns = ["id"],
                childColumns = ["profileId"],
                onDelete = ForeignKey.CASCADE,
            )
        ],
    indices = [Index("profileId")],
)
internal data class ProfileFavoriteFoodEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val profileId: String,
    val identityType: FoodIdentityType,
    val extra: String,
)
