package com.maksimowiczm.foodyou.fooddatacentral.infrastructure

import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.network.model.DetailedFood
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.network.model.DetailedFoodNutrient
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.network.model.DetailedNutrient
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.network.model.Nutrient
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FoodDataCentralProductMapperTest {
    @Test
    fun `negative nutrient values are skipped`() {
        val product =
            FoodDataCentralProductMapper()
                .foodDataCentralProductEntity(
                    DetailedFood(
                        fdcId = 1,
                        description = "Test food",
                        dataType = "Foundation",
                        foodNutrients =
                            listOf(
                                DetailedFoodNutrient(
                                    nutrient =
                                        DetailedNutrient(
                                            number = Nutrient.PROTEIN.number,
                                            name = "Protein",
                                            unit = "g",
                                        ),
                                    amount = -0.1,
                                ),
                                DetailedFoodNutrient(
                                    nutrient =
                                        DetailedNutrient(
                                            number = Nutrient.ENERGY.number,
                                            name = "Energy",
                                            unit = "kcal",
                                        ),
                                    amount = -0.1,
                                ),
                            ),
                    )
                )

        assertNull(product.nutrients.proteins)
        assertNull(product.nutrients.energy)
    }

    @Test
    fun `tiny negative nutrient values are treated as zero`() {
        val product =
            FoodDataCentralProductMapper()
                .foodDataCentralProductEntity(
                    DetailedFood(
                        fdcId = 1,
                        description = "Test food",
                        dataType = "Foundation",
                        foodNutrients =
                            listOf(
                                DetailedFoodNutrient(
                                    nutrient =
                                        DetailedNutrient(
                                            number = Nutrient.PROTEIN.number,
                                            name = "Protein",
                                            unit = "g",
                                        ),
                                    amount = -1e-10,
                                ),
                                DetailedFoodNutrient(
                                    nutrient =
                                        DetailedNutrient(
                                            number = Nutrient.ENERGY.number,
                                            name = "Energy",
                                            unit = "kcal",
                                        ),
                                    amount = -1e-10,
                                ),
                            ),
                    )
                )

        assertEquals(0.0, product.nutrients.proteins)
        assertEquals(0.0, product.nutrients.energy)
    }
}
