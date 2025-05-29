package com.maksimowiczm.foodyou.core.data.model.product

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.core.data.model.Minerals
import com.maksimowiczm.foodyou.core.data.model.Nutrients
import com.maksimowiczm.foodyou.core.data.model.Vitamins

/**
 * @see [ProductEntityField]
 */
@Entity
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val brand: String?,
    val barcode: String?,
    @Embedded
    val nutrients: Nutrients,
    @Embedded
    val vitamins: Vitamins,
    @Embedded
    val minerals: Minerals,
    val packageWeight: Float?,
    val servingWeight: Float?,
    val productSource: ProductSource
)
