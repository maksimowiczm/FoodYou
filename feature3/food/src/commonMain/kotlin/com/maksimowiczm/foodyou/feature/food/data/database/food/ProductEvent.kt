package com.maksimowiczm.foodyou.feature.food.data.database.food

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"]
        )
    ]
)
data class ProductEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val type: ProductEventType,
    val epochSeconds: Long,
    val extra: String? = null,
    val productId: Long
)
