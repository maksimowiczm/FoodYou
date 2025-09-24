package com.maksimowiczm.foodyou.food.search.infrastructure.room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["epochSeconds"])])
data class SearchEntry(val epochSeconds: Long, @PrimaryKey val query: String)
