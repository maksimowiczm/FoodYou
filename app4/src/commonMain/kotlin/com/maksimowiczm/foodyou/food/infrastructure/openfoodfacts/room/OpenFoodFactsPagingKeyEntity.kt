package com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "OpenFoodFactsPagingKey",
    indices = [Index(value = ["queryString"]), Index(value = ["productBarcode"])],
    foreignKeys =
        [
            ForeignKey(
                entity = OpenFoodFactsProductEntity::class,
                parentColumns = ["barcode"],
                childColumns = ["productBarcode"],
                onDelete = ForeignKey.CASCADE,
            )
        ],
)
data class OpenFoodFactsPagingKeyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val queryString: String,
    val productBarcode: String,
)
