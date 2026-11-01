package com.maksimowiczm.foodyou.openfoodfacts.infrastructure

import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.Nutriments
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.OpenFoodFactsProductNetwork
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class OpenFoodFactsProductMapperTest {
    @Test
    fun `negative nutrient values are skipped`() {
        val product =
            OpenFoodFactsProductMapper()
                .toModel(
                    OpenFoodFactsProductNetwork(
                        code = "123",
                        nutriments =
                            Nutriments(
                                proteins100g = -0.1,
                                energyKcal100g = -0.1,
                            ),
                    )
                )

        assertNull(product.nutritionFacts.proteins.value)
        assertNull(product.nutritionFacts.energy.value)
    }

    @Test
    fun `tiny negative nutrient values are treated as zero`() {
        val product =
            OpenFoodFactsProductMapper()
                .toModel(
                    OpenFoodFactsProductNetwork(
                        code = "123",
                        nutriments =
                            Nutriments(
                                proteins100g = -1e-10,
                                energyKcal100g = -1e-10,
                            ),
                    )
                )

        assertEquals(0.0, product.nutritionFacts.proteins.value?.grams)
        assertEquals(0.0, product.nutritionFacts.energy.value?.kilocalories)
    }
}
