package com.maksimowiczm.foodyou.account.infrastructure.room

import androidx.room.Entity

@Entity(tableName = "Account", primaryKeys = ["id"]) data class AccountEntity(val id: String)
