package com.maksimowiczm.foodyou.business.shared.infrastructure.room.fooddiary

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.business.shared.infrastructure.room.shared.FoodSourceType
import com.maksimowiczm.foodyou.business.shared.infrastructure.room.shared.Minerals
import com.maksimowiczm.foodyou.business.shared.infrastructure.room.shared.Nutrients
import com.maksimowiczm.foodyou.business.shared.infrastructure.room.shared.Vitamins

@Entity(tableName = "DiaryProduct")
data class DiaryProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    @Embedded val nutrients: Nutrients,
    @Embedded val vitamins: Vitamins,
    @Embedded val minerals: Minerals,
    val packageWeight: Double?,
    val servingWeight: Double?,
    val isLiquid: Boolean,
    val sourceType: FoodSourceType,
    val sourceUrl: String?,
    val note: String?,
)
