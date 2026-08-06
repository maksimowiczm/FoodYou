package com.maksimowiczm.foodyou.fooddatacentral.infrastructure.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSearchParameters.DataType

@Entity(
    tableName = "FoodDataCentralPagingKey",
    indices = [Index(value = ["queryString", "dataTypes"]), Index(value = ["fdcId"])],
    foreignKeys =
        [
            ForeignKey(
                entity = FoodDataCentralProductEntity::class,
                parentColumns = ["fdcId"],
                childColumns = ["fdcId"],
                onDelete = ForeignKey.CASCADE,
            )
        ],
)
internal data class FoodDataCentralPagingKeyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val queryString: String,
    val dataTypes: Set<DataType>?,
    val fdcId: Int,
)
