package com.maksimowiczm.foodyou.food.search.domain

import com.maksimowiczm.foodyou.foodsearch.domain.SearchQuery
import com.maksimowiczm.foodyou.foodsearch.domain.SearchQueryParser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SearchQueryParserTest {
    @Test
    fun parse_blankQuery_returnsBlank() {
        val parser = SearchQueryParser()
        val result = parser.parse("   ")
        val _ = assertIs<SearchQuery.Blank>(result)
    }

    @Test
    fun parse_barcodeQuery_returnsBarcode() {
        val parser = SearchQueryParser()
        val result = parser.parse("1234567890123")
        val _ = assertIs<SearchQuery.Barcode>(result)
        assertEquals("1234567890123", result.barcode)
    }

    @Test
    fun parse_openFoodFactsUrlQuery_returnsOpenFoodFactsUrl() {
        val parser = SearchQueryParser()

        val urls =
            listOf(
                "https://world.openfoodfacts.org/product/6111035000430/sidi-ali",
                "https://world.openfoodfacts.org/product/6111035000430",
            )

        for (url in urls) {
            val result = parser.parse(url)
            val _ = assertIs<SearchQuery.OpenFoodFactsUrl>(result, "Failed for URL: $url")
            assertEquals(url, result.url)
        }
    }

    @Test
    fun parse_foodDataCentralUrlQuery_returnsFoodDataCentralUrl() {
        val parser = SearchQueryParser()
        val url = "https://fdc.nal.usda.gov/food-details/2262074/nutrients"
        val result = parser.parse(url)
        val _ = assertIs<SearchQuery.FoodDataCentralUrl>(result)
        assertEquals(url, result.url)
    }

    @Test
    fun parse_textQuery_returnsText() {
        val parser = SearchQueryParser()
        val result = parser.parse("Sample Product Name")
        val _ = assertIs<SearchQuery.Text>(result)
        assertEquals("Sample Product Name", result.query)
    }
}
