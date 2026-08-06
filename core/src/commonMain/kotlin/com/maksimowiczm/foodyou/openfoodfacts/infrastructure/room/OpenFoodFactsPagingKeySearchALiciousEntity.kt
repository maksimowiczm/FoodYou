package com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "OpenFoodFactsPagingKeySearchALicious",
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
internal data class OpenFoodFactsPagingKeySearchALiciousEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val queryString: String,
    val productBarcode: String,
)
