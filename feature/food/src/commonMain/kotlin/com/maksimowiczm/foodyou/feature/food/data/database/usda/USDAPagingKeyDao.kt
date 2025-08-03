package com.maksimowiczm.foodyou.feature.food.data.database.usda

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface USDAPagingKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPagingKey(pagingKey: USDAPagingKey)

    @Query("SELECT * FROM USDAPagingKey WHERE queryString = :query")
    suspend fun getPagingKey(query: String): USDAPagingKey?
}
