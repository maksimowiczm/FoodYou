package com.maksimowiczm.foodyou.account.infrastructure.room

import androidx.room.Entity

@Entity(tableName = "Account", primaryKeys = ["id"])
internal data class AccountEntity(val id: String)
