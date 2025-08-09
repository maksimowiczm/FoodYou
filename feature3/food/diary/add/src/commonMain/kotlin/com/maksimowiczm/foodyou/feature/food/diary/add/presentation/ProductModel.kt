package com.maksimowiczm.foodyou.feature.food.diary.add.presentation

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.business.food.domain.FoodSource
import com.maksimowiczm.foodyou.business.food.domain.Product
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement

@Immutable
internal data class ProductModel(
    override val foodId: FoodId.Product,
    override val name: String,
    override val nutritionFacts: NutritionFacts,
    override val isLiquid: Boolean,
    override val note: String?,
    val source: FoodSource,
    private val packageWeight: Double?,
    private val servingWeight: Double?,
) : FoodModel {
    constructor(
        product: Product
    ) : this(
        foodId = product.id,
        name = product.headline,
        nutritionFacts = product.nutritionFacts,
        isLiquid = product.isLiquid,
        note = product.note,
        source = product.source,
        packageWeight = product.totalWeight,
        servingWeight = product.servingWeight,
    )

    override fun weight(measurement: Measurement): Double? =
        when (measurement) {
            is Measurement.Gram -> measurement.value
            is Measurement.Milliliter -> measurement.value
            is Measurement.Package -> packageWeight?.let { it * measurement.quantity }
            is Measurement.Serving -> servingWeight?.let { it * measurement.quantity }
        }
}
