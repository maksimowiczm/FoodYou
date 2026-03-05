package com.maksimowiczm.foodyou.common.csv

import io.ktor.utils.io.charsets.Charsets
import io.ktor.utils.io.core.toByteArray
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking

/**
 * RFC 4180-compliant tests for [CsvParser].
 *
 * Spec: https://www.rfc-editor.org/rfc/rfc4180
 *
 * RFC 4180 Summary:
 * - Records are separated by CRLF (\r\n). The last record MAY omit the line break.
 * - An optional header record may appear as the first line.
 * - Fields are separated by commas.
 * - Fields MAY be enclosed in double-quotes. Fields containing commas, double-quotes, or line
 *   breaks MUST be enclosed in double-quotes.
 * - A double-quote appearing inside a quoted field MUST be escaped by a preceding double-quote.
 * - Spaces are considered part of a field and must not be ignored.
 * - Empty fields are represented as null per this parser's contract.
 */
abstract class RfcCsvParserTest {

    // ---------------------------------------------------------------------------
    // Test infrastructure
    // ---------------------------------------------------------------------------

    protected abstract val parser: CsvParser

    private fun String.toByteFlow() = toByteArray(Charsets.UTF_8).toList().asFlow()

    private suspend fun CsvParser.parseString(csv: String) = parse(csv.toByteFlow()).toList()

    // ---------------------------------------------------------------------------
    // RFC 4180 §2.1 – Basic record / field structure
    // ---------------------------------------------------------------------------

    @Test
    fun basicStructure_singleFieldSingleRecord() = runBlocking {
        val result = parser.parseString("hello")
        assertEquals(listOf(listOf("hello")), result)
    }

    @Test
    fun basicStructure_multipleFieldsOneRecord() = runBlocking {
        val result = parser.parseString("one,two,three")
        assertEquals(listOf(listOf("one", "two", "three")), result)
    }

    @Test
    fun basicStructure_multipleRecordsSeparatedByCRLF() = runBlocking {
        val result = parser.parseString("a,b\r\nc,d")
        assertEquals(listOf(listOf("a", "b"), listOf("c", "d")), result)
    }

    @Test
    fun basicStructure_trailingCRLFProducesSameResultAsWithout() = runBlocking {
        val withoutTrailing = parser.parseString("a,b\r\nc,d")
        val withTrailing = parser.parseString("a,b\r\nc,d\r\n")
        assertEquals(withoutTrailing, withTrailing)
    }

    @Test
    fun basicStructure_allRecordsHaveSameFieldCount() = runBlocking {
        val result = parser.parseString("a,b,c\r\n1,2,3\r\n4,5,6")
        assertTrue(result.all { it.size == 3 })
    }

    // ---------------------------------------------------------------------------
    // RFC 4180 §2.2 – Header record
    // ---------------------------------------------------------------------------

    @Test
    fun header_returnedAsFirstRow() = runBlocking {
        val result = parser.parseString("name,age,city\r\nAlice,30,Warsaw")
        assertEquals("name", result[0][0])
        assertEquals("Alice", result[1][0])
    }

    // ---------------------------------------------------------------------------
    // RFC 4180 §2.4 – Spaces are significant
    // ---------------------------------------------------------------------------

    @Test
    fun spaces_preservedInUnquotedField() = runBlocking {
        val result = parser.parseString(" hello , world ")
        assertEquals(listOf(listOf(" hello ", " world ")), result)
    }

    @Test
    fun spaces_preservedInsideQuotedField() = runBlocking {
        val result = parser.parseString("\" hello \"")
        assertEquals(listOf(listOf(" hello ")), result)
    }

    // ---------------------------------------------------------------------------
    // RFC 4180 §2.5 – Quoted fields
    // ---------------------------------------------------------------------------

    @Test
    fun quoted_simpleQuotedField() = runBlocking {
        val result = parser.parseString("\"hello\"")
        assertEquals(listOf(listOf("hello")), result)
    }

    @Test
    fun quoted_fieldContainingComma() = runBlocking {
        val result = parser.parseString("\"hello, world\",next")
        assertEquals(listOf(listOf("hello, world", "next")), result)
    }

    @Test
    fun quoted_fieldContainingCRLF() = runBlocking {
        val result = parser.parseString("\"line1\r\nline2\",end")
        assertEquals(listOf(listOf("line1\r\nline2", "end")), result)
    }

    @Test
    fun quoted_fieldContainingLF() = runBlocking {
        val result = parser.parseString("\"line1\nline2\",end")
        assertEquals(listOf(listOf("line1\nline2", "end")), result)
    }

    @Test
    fun quoted_fieldContainingCR() = runBlocking {
        val result = parser.parseString("\"line1\rline2\",end")
        assertEquals(listOf(listOf("line1\rline2", "end")), result)
    }

    @Test
    fun quoted_mixedQuotedAndUnquotedFieldsInSameRecord() = runBlocking {
        val result = parser.parseString("plain,\"quoted\",plain2")
        assertEquals(listOf(listOf("plain", "quoted", "plain2")), result)
    }

    // ---------------------------------------------------------------------------
    // RFC 4180 §2.7 – Escaped double-quotes inside quoted fields
    // ---------------------------------------------------------------------------

    @Test
    fun escapedQuotes_twoConsecutiveQuotesRepresentOneLiteral() = runBlocking {
        // CSV: "say ""hello"""  →  value: say "hello"
        val result = parser.parseString("\"say \"\"hello\"\"\"")
        assertEquals(listOf(listOf("say \"hello\"")), result)
    }

    @Test
    fun escapedQuotes_fieldContainingOnlyAnEscapedQuote() = runBlocking {
        // CSV: """"  →  value: "
        val result = parser.parseString("\"\"\"\"")
        assertEquals(listOf(listOf("\"")), result)
    }

    @Test
    fun escapedQuotes_multipleEscapedQuotesInOneField() = runBlocking {
        // CSV: "a""b""c"  →  value: a"b"c
        val result = parser.parseString("\"a\"\"b\"\"c\"")
        assertEquals(listOf(listOf("a\"b\"c")), result)
    }

    // ---------------------------------------------------------------------------
    // Empty / null field handling (parser contract)
    // ---------------------------------------------------------------------------

    @Test
    fun nullFields_emptyInputProducesNoRecords() = runBlocking {
        val result = parser.parseString("")
        assertTrue(result.isEmpty())
    }

    @Test
    fun nullFields_trailingCommaProducesNullLastField() = runBlocking {
        val result = parser.parseString("a,b,")
        assertEquals(listOf(listOf("a", "b", null)), result)
    }

    @Test
    fun nullFields_leadingCommaProducesNullFirstField() = runBlocking {
        val result = parser.parseString(",b,c")
        assertEquals(listOf(listOf(null, "b", "c")), result)
    }

    @Test
    fun nullFields_consecutiveCommasProduceMultipleNulls() = runBlocking {
        val result = parser.parseString("a,,,d")
        assertEquals(listOf(listOf("a", null, null, "d")), result)
    }

    @Test
    fun nullFields_emptyQuotedFieldIsEmptyStringNotNull() = runBlocking {
        // Only truly absent fields are null; an explicit "" is an empty string.
        val result = parser.parseString("\"\",b")
        assertEquals(listOf(listOf("", "b")), result)
    }

    @Test
    fun nullFields_recordWithAllEmptyFields() = runBlocking {
        val result = parser.parseString(",,")
        assertEquals(listOf(listOf(null, null, null)), result)
    }

    // ---------------------------------------------------------------------------
    // Line-ending variations (leniency beyond strict RFC 4180)
    // ---------------------------------------------------------------------------

    @Test
    fun lineEndings_lfOnly() = runBlocking {
        // Unix-style: LF (\n, U+000A)
        val result = parser.parseString("a,b\nc,d")
        assertEquals(listOf(listOf("a", "b"), listOf("c", "d")), result)
    }

    @Test
    fun lineEndings_crOnly() = runBlocking {
        // Classic Mac OS 9-style: CR (\r, U+000D)
        val result = parser.parseString("a,b\rc,d")
        assertEquals(listOf(listOf("a", "b"), listOf("c", "d")), result)
    }

    @Test
    fun lineEndings_mixedCRLFAndLF() = runBlocking {
        val result = parser.parseString("a,b\r\nc,d\ne,f")
        assertEquals(listOf(listOf("a", "b"), listOf("c", "d"), listOf("e", "f")), result)
    }

    @Test
    fun lineEndings_mixedCRLFAndCR() = runBlocking {
        val result = parser.parseString("a,b\r\nc,d\re,f")
        assertEquals(listOf(listOf("a", "b"), listOf("c", "d"), listOf("e", "f")), result)
    }

    @Test
    fun lineEndings_mixedLFAndCR() = runBlocking {
        val result = parser.parseString("a,b\nc,d\re,f")
        assertEquals(listOf(listOf("a", "b"), listOf("c", "d"), listOf("e", "f")), result)
    }

    @Test
    fun lineEndings_allThreeStylesMixed() = runBlocking {
        // CRLF, LF, and CR all in the same document
        val result = parser.parseString("a,b\r\nc,d\ne,f\rg,h")
        assertEquals(
            listOf(listOf("a", "b"), listOf("c", "d"), listOf("e", "f"), listOf("g", "h")),
            result,
        )
    }

    @Test
    fun lineEndings_trailingLFAccepted() = runBlocking {
        val withoutTrailing = parser.parseString("a,b\nc,d")
        val withTrailing = parser.parseString("a,b\nc,d\n")
        assertEquals(withoutTrailing, withTrailing)
    }

    @Test
    fun lineEndings_trailingCRAccepted() = runBlocking {
        val withoutTrailing = parser.parseString("a,b\rc,d")
        val withTrailing = parser.parseString("a,b\rc,d\r")
        assertEquals(withoutTrailing, withTrailing)
    }

    @Test
    fun lineEndings_crNotFollowedByLFIsTreatedAsSingleSeparator() = runBlocking {
        // A bare CR must split records exactly once, not produce an empty record.
        val result = parser.parseString("a\rb")
        assertEquals(2, result.size)
        assertEquals(listOf("a"), result[0])
        assertEquals(listOf("b"), result[1])
    }

    @Test
    fun lineEndings_crlfCountsAsSingleSeparatorNotTwo() = runBlocking {
        // \r\n is ONE record separator, not CR + LF producing an extra empty record.
        val result = parser.parseString("a,b\r\nc,d")
        assertEquals(2, result.size)
    }

    // ---------------------------------------------------------------------------
    // Large / streaming inputs
    // ---------------------------------------------------------------------------

    @Test
    fun streaming_thousandRecordsParsedCorrectly() = runBlocking {
        val csv = (1..1_000).joinToString("\r\n") { i -> "$i,value$i" }
        val result = parser.parseString(csv)
        assertEquals(1_000, result.size)
        result.forEachIndexed { idx, row ->
            assertEquals("${idx + 1}", row[0])
            assertEquals("value${idx + 1}", row[1])
        }
    }

    @Test
    fun streaming_veryLongFieldReturnedIntact() = runBlocking {
        val longValue = "x".repeat(10_000)
        val result = parser.parseString(longValue)
        assertEquals(longValue, result[0][0])
    }

    // ---------------------------------------------------------------------------
    // Unicode
    // ---------------------------------------------------------------------------

    @Test
    fun unicode_multiBytCharactersPreservedInUnquotedField() = runBlocking {
        val result = parser.parseString("こんにちは,Привет,مرحبا")
        assertEquals(listOf(listOf("こんにちは", "Привет", "مرحبا")), result)
    }

    @Test
    fun unicode_multiBytCharactersPreservedInQuotedField() = runBlocking {
        val result = parser.parseString("\"emoji: 🎉\",plain")
        assertEquals(listOf(listOf("emoji: 🎉", "plain")), result)
    }

    // ---------------------------------------------------------------------------
    // Real-world-shaped data
    // ---------------------------------------------------------------------------

    @Test
    fun realWorld_headerPlusDataRowsWithQuotedAddressField() = runBlocking {
        val csv =
            "name,address,age\r\nAlice,\"123 Main St, Springfield\",30\r\nBob,\"456 Elm St\",25"
        val result = parser.parseString(csv)
        assertEquals(3, result.size)
        assertEquals(listOf("name", "address", "age"), result[0])
        assertEquals("123 Main St, Springfield", result[1][1])
    }

    @Test
    fun realWorld_fieldContainingEscapedDoubleQuote() = runBlocking {
        // Value: He said "hi"
        val csv = "name,quote\r\nAlice,\"He said \"\"hi\"\"\""
        val result = parser.parseString(csv)
        assertEquals("He said \"hi\"", result[1][1])
    }

    @Test
    fun realWorld_multilineNoteFieldEmbeddedInRecord() = runBlocking {
        val csv = "id,note,status\r\n1,\"Line one\r\nLine two\",active"
        val result = parser.parseString(csv)
        assertEquals("Line one\r\nLine two", result[1][1])
        assertEquals("active", result[1][2])
    }
}
