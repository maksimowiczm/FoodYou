package com.maksimowiczm.foodyou.app.infrastructure.room.fooddiary

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.app.infrastructure.room.shared.Minerals
import com.maksimowiczm.foodyou.app.infrastructure.room.shared.Nutrients
import com.maksimowiczm.foodyou.app.infrastructure.room.shared.Vitamins

@Entity(
    tableName = "ManualDiaryEntry",
    foreignKeys =
        [
            ForeignKey(
                entity = MealEntity::class,
                parentColumns = ["id"],
                childColumns = ["mealId"],
                onDelete = ForeignKey.CASCADE,
            )
        ],
    indices = [Index(value = ["mealId"]), Index(value = ["dateEpochDay"])],
)
data class ManualDiaryEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mealId: Long,
    val dateEpochDay: Long,
    val name: String,
    @Embedded val nutrients: Nutrients,
    @Embedded val vitamins: Vitamins,
    @Embedded val minerals: Minerals,
    val createdEpochSeconds: Long,
    val updatedEpochSeconds: Long,
)
