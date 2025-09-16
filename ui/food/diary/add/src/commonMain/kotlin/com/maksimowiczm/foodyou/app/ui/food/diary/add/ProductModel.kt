package com.maksimowiczm.foodyou.app.ui.food.diary.add

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.food.domain.entity.Product
import com.maksimowiczm.foodyou.shared.domain.food.FoodSource
import com.maksimowiczm.foodyou.shared.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.shared.domain.food.Weighted

@Immutable
internal data class ProductModel(
    override val foodId: FoodId.Product,
    override val name: String,
    override val nutritionFacts: NutritionFacts,
    override val isLiquid: Boolean,
    override val note: String?,
    val source: FoodSource,
    override val totalWeight: Double?,
    override val servingWeight: Double?,
) : FoodModel, Weighted {
    constructor(
        product: Product
    ) : this(
        foodId = product.id,
        name = product.headline,
        nutritionFacts = product.nutritionFacts,
        isLiquid = product.isLiquid,
        note = product.note,
        source = product.source,
        totalWeight = product.totalWeight,
        servingWeight = product.servingWeight,
    )
}
