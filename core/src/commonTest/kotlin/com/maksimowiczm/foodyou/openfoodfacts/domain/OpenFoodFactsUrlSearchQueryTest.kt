package com.maksimowiczm.foodyou.openfoodfacts.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class OpenFoodFactsUrlSearchQueryTest {

    @Test
    fun recognize_valid_url_with_slug() {
        val url = "https://world.openfoodfacts.org/product/5449000000996/coca-cola"
        val query =
            OpenFoodFactsUrlSearchQuery.recognizer.recognize(url) as? OpenFoodFactsUrlSearchQuery

        assertEquals(url, query?.url)
        assertEquals("5449000000996", query?.barcode)
    }

    @Test
    fun recognize_valid_url_without_slug() {
        val url = "https://world.openfoodfacts.org/product/5449000000996"
        val query =
            OpenFoodFactsUrlSearchQuery.recognizer.recognize(url) as? OpenFoodFactsUrlSearchQuery

        assertEquals(url, query?.url)
        assertEquals("5449000000996", query?.barcode)
    }

    @Test
    fun recognize_valid_url_with_trailing_slash() {
        val url = "https://world.openfoodfacts.org/product/5449000000996/"
        val query =
            OpenFoodFactsUrlSearchQuery.recognizer.recognize(url) as? OpenFoodFactsUrlSearchQuery

        assertEquals(url, query?.url)
        assertEquals("5449000000996", query?.barcode)
    }

    @Test
    fun recognize_different_subdomains() {
        val urls =
            listOf(
                "https://fr.openfoodfacts.org/product/123456789",
                "https://pl.openfoodfacts.org/product/123456789",
                "https://en.openfoodfacts.org/product/123456789",
            )

        for (url in urls) {
            val query =
                OpenFoodFactsUrlSearchQuery.recognizer.recognize(url)
                    as? OpenFoodFactsUrlSearchQuery
            assertEquals(url, query?.url)
            assertEquals("123456789", query?.barcode)
        }
    }

    @Test
    fun ignore_invalid_urls() {
        val urls =
            listOf(
                "https://world.openfoodfacts.org/product/",
                "https://world.openfoodfacts.org/product/abc",
                "https://example.com/product/123456789",
                "world.openfoodfacts.org/product/123456789",
            )

        for (url in urls) {
            val query = OpenFoodFactsUrlSearchQuery.recognizer.recognize(url)
            assertNull(query, "URL should not be recognized: $url")
        }
    }
}
