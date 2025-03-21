package com.maksimowiczm.foodyou.ui.preview

import com.maksimowiczm.foodyou.feature.diary.data.model.Nutrients
import com.maksimowiczm.foodyou.feature.diary.data.model.Product
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductSource
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightUnit
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

class ProductPreviewParameterProvider : PreviewParameterProvider<Product> {
    override val values: Sequence<Product> = sequenceOf(
        Product(
            id = 1,
            name = "Egg",
            brand = "Chicken land",
            barcode = "1234567890123",
            nutrients = Nutrients(
                calories = 155f,
                proteins = 13f,
                carbohydrates = 1.1f,
                sugars = 1.1f,
                fats = 11f,
                saturatedFats = 3.3f,
                salt = 0.5f,
                sodium = 0.2f,
                fiber = 0f
            ),
            packageWeight = 60f,
            weightUnit = WeightUnit.Gram,
            productSource = ProductSource.User
        ),
        Product(
            id = 2,
            name = "Chicken",
            brand = "Chicken land",
            nutrients = Nutrients(
                calories = 239f,
                proteins = 27f,
                carbohydrates = 0f,
                sugars = 0f,
                fats = 14f,
                saturatedFats = 4.1f,
                salt = 0.1f,
                sodium = 0.04f,
                fiber = 0f
            ),
            weightUnit = WeightUnit.Gram,
            productSource = ProductSource.User
        ),
        Product(
            id = 3,
            name = "Tomato sauce",
            brand = "Spaghetti italiano",
            nutrients = Nutrients(
                calories = 82f,
                proteins = 1.2f,
                carbohydrates = 16f,
                sugars = 12f,
                fats = 1.1f,
                saturatedFats = 0.2f,
                salt = 0.1f,
                sodium = 0.04f,
                fiber = 2.5f
            ),
            packageWeight = 500f,
            servingWeight = 100f,
            weightUnit = WeightUnit.Gram,
            productSource = ProductSource.User
        ),
        Product(
            id = 4,
            name = "Cheese",
            brand = "Mouse opposition",
            nutrients = Nutrients(
                calories = 402f,
                proteins = 25f,
                carbohydrates = 0.1f,
                sugars = 0.1f,
                fats = 33f,
                saturatedFats = 21f,
                salt = 1.6f,
                sodium = 0.64f,
                fiber = null
            ),
            packageWeight = 400f,
            servingWeight = 30f,
            weightUnit = WeightUnit.Gram,
            productSource = ProductSource.User
        ),
        Product(
            id = 5,
            name = "Water with sugar",
            brand = "Sweet water",
            nutrients = Nutrients(
                calories = 43f,
                proteins = 0f,
                carbohydrates = 10f,
                sugars = 10f,
                fats = 0f,
                saturatedFats = 0f,
                salt = 0f,
                sodium = 0f,
                fiber = null
            ),
            packageWeight = 1000f,
            servingWeight = 250f,
            weightUnit = WeightUnit.Milliliter,
            productSource = ProductSource.User
        )
    )
}
