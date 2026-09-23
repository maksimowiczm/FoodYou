package com.maksimowiczm.foodyou.common.domain.search

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BarcodeSearchQueryTest {

    @Test
    fun recognize_numeric_string_returns_barcode() {
        val query = "1234567890123"
        val result = SearchQuery.Barcode.recognizer.recognize(query) as? SearchQuery.Barcode

        assertEquals(query, result?.barcode)
    }

    @Test
    fun recognize_non_numeric_string_returns_null() {
        val query = "abc123"
        val result = SearchQuery.Barcode.recognizer.recognize(query)

        assertNull(result)
    }

    @Test
    fun recognize_empty_string_returns_null() {
        val query = ""
        val result = SearchQuery.Barcode.recognizer.recognize(query)

        assertNull(result)
    }
}
