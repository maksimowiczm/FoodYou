package com.maksimowiczm.foodyou.account.infrastructure.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.account.domain.EnergyFormat

@Entity(
    tableName = "AccountSettings",
    foreignKeys =
        [
            ForeignKey(
                AccountEntity::class,
                parentColumns = ["id"],
                childColumns = ["accountId"],
                onDelete = ForeignKey.CASCADE,
            )
        ],
    indices = [Index("accountId")],
)
internal data class SettingsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accountId: String,
    val onboardingFinished: Boolean,
    val energyFormat: EnergyFormat,
    val nutrientsOrder: String,
)
