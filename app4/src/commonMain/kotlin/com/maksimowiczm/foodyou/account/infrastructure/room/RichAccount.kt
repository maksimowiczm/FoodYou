package com.maksimowiczm.foodyou.account.infrastructure.room

import androidx.room.Embedded
import androidx.room.Relation

internal data class RichAccount(
    @Embedded("a_") val account: AccountEntity,
    @Relation(parentColumn = "a_id", entityColumn = "accountId") val profiles: List<ProfileEntity>,
    @Relation(parentColumn = "a_id", entityColumn = "accountId") val settings: SettingsEntity,
    @Relation(parentColumn = "a_id", entityColumn = "accountId")
    val favoriteFoods: List<ProfileFavoriteFoodEntity>,
)
