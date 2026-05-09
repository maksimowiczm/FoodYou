package com.maksimowiczm.foodyou.common.domain.search

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SearchQueryParserTest {

    @Test
    fun parse_withNullQuery_returnsBlank() {
        val parser = SearchQueryParser()
        val result = parser.parse(null)
        assertIs<SearchQuery.Blank>(result)
    }

    @Test
    fun parse_withBlankQuery_returnsBlank() {
        val parser = SearchQueryParser()
        val result = parser.parse("   ")
        assertIs<SearchQuery.Blank>(result)
    }

    @Test
    fun parse_whenRecognizerMatches_returnsRecognizedQuery() {
        val expected = SearchQuery.Barcode("123")
        val recognizer = SearchQueryRecognizer { expected }
        val parser = SearchQueryParser(recognizer)

        val result = parser.parse("some input")

        assertEquals(expected, result)
    }

    @Test
    fun parse_withBarcodeRecognizer_returnsBarcode() {
        val parser = SearchQueryParser(SearchQuery.Barcode.recognizer)
        val result = parser.parse("12345")

        assertIs<SearchQuery.Barcode>(result)
        assertEquals("12345", result.barcode)
    }

    @Test
    fun parse_whenMultipleRecognizers_returnsFirstMatch() {
        val firstMatch = SearchQuery.Barcode("123")
        val secondMatch = SearchQuery.Text("text")
        val recognizer1 = SearchQueryRecognizer { null }
        val recognizer2 = SearchQueryRecognizer { firstMatch }
        val recognizer3 = SearchQueryRecognizer { secondMatch }
        val parser = SearchQueryParser(recognizer1, recognizer2, recognizer3)

        val result = parser.parse("some input")

        assertEquals(firstMatch, result)
    }

    @Test
    fun parse_whenNoRecognizerMatchesAndNumeric_returnsText() {
        val parser = SearchQueryParser()
        val result = parser.parse("123456")

        assertIs<SearchQuery.Text>(result)
        assertEquals("123456", result.query)
    }

    @Test
    fun parse_whenNoRecognizerMatchesAndNonNumeric_returnsText() {
        val parser = SearchQueryParser()
        val result = parser.parse("Apple")

        assertIs<SearchQuery.Text>(result)
        assertEquals("Apple", result.query)
    }

    @Test
    fun parse_trimsInputBeforeRecognition() {
        var captured: String? = null
        val recognizer = SearchQueryRecognizer {
            captured = it
            null
        }
        val parser = SearchQueryParser(recognizer)

        parser.parse("  trimmed  ")

        assertEquals("trimmed", captured)
    }

    @Test
    fun parse_trimsInputBeforeFallback() {
        val parser = SearchQueryParser()

        val result = parser.parse("  12345  ")

        assertIs<SearchQuery.Text>(result)
        assertEquals("12345", result.query)
    }
}
