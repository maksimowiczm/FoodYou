package com.maksimowiczm.foodyou.fooddatacentral.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FoodDataCentralUrlSearchQueryTest {

    @Test
    fun recognize_valid_url_with_nutrients() {
        val url = "https://fdc.nal.usda.gov/food-details/123456/nutrients"
        val query =
            FoodDataCentralUrlSearchQuery.recognizer.recognize(url)
                as? FoodDataCentralUrlSearchQuery

        assertEquals(url, query?.url)
        assertEquals(123456, query?.fdcId)
    }

    @Test
    fun recognize_valid_url_without_nutrients() {
        val url = "https://fdc.nal.usda.gov/food-details/654321"
        val query =
            FoodDataCentralUrlSearchQuery.recognizer.recognize(url)
                as? FoodDataCentralUrlSearchQuery

        assertEquals(url, query?.url)
        assertEquals(654321, query?.fdcId)
    }

    @Test
    fun ignore_invalid_urls() {
        val urls =
            listOf(
                "https://fdc.nal.usda.gov/food-details/",
                "https://fdc.nal.usda.gov/food-details/abc",
                "https://example.com/food-details/123456",
                "fdc.nal.usda.gov/food-details/123456",
            )

        for (url in urls) {
            val query = FoodDataCentralUrlSearchQuery.recognizer.recognize(url)
            assertNull(query, "URL should not be recognized: $url")
        }
    }
}
