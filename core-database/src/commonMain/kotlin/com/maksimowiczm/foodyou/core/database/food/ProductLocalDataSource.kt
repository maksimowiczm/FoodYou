package com.maksimowiczm.foodyou.core.database.food

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductLocalDataSource {

    @Query("SELECT * FROM ProductEntity WHERE id = :id")
    fun observeProductById(id: Long): Flow<ProductEntity?>

    @Insert
    suspend fun insertProduct(product: ProductEntity): Long

    @Update
    suspend fun updateProduct(product: ProductEntity)
}
