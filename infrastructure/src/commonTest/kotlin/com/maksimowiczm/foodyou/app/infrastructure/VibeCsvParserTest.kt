package com.maksimowiczm.foodyou.app.infrastructure

import kotlin.test.Test
import kotlin.test.assertEquals

class VibeCsvParserTest {

    private val parser = VibeCsvParser()

    @Test
    fun testEmptyString() {
        assertEquals(listOf(null), parser.parseLine(""))
    }

    @Test
    fun testSimpleLine() {
        assertEquals(listOf("a", "b", "c"), parser.parseLine("a,b,c"))
    }

    @Test
    fun testLineWithEmptyField() {
        assertEquals(listOf("a", null, "c"), parser.parseLine("a,,c"))
    }

    @Test
    fun testLineWithQuotedFields() {
        assertEquals(listOf("a", "b", "c"), parser.parseLine("\"a\",\"b\",\"c\""))
    }

    @Test
    fun testLineWithQuotedFieldContainingComma() {
        assertEquals(listOf("a", "b,c", "d"), parser.parseLine("\"a\",\"b,c\",\"d\""))
    }

    @Test
    fun testLineWithQuotedFieldContainingEscapedQuote() {
        assertEquals(listOf("a", "b\"\"c", "d"), parser.parseLine("\"a\",\"b\"\"c\",\"d\""))
    }

    @Test
    fun testLineWithMixOfQuotedAndUnquotedFields() {
        assertEquals(listOf("a", "b,c", "d", "e"), parser.parseLine("a,\"b,c\",d,e"))
    }

    @Test
    fun testLineWithEmptyQuotedField() {
        assertEquals(listOf("a", "", "c"), parser.parseLine("a,\"\",c"))
    }

    @Test
    fun testLineWithEmptyUnquotedFieldAtEnd() {
        assertEquals(listOf("a", "b", null), parser.parseLine("a,b,"))
    }

    @Test
    fun testLineWithQuotedFieldAtEnd() {
        assertEquals(listOf("a", "b", "c"), parser.parseLine("a,b,\"c\""))
    }

    @Test
    fun testLineWithUnquotedFieldAtEnd() {
        assertEquals(listOf("a", "b", "c"), parser.parseLine("a,b,c"))
    }

    @Test
    fun testLineWithMultipleEmptyFields() {
        assertEquals(listOf(null, null, null), parser.parseLine(",,"))
    }

    @Test
    fun testLineWithFieldContainingOnlyWhitespace() {
        assertEquals(listOf("a", " ", "c"), parser.parseLine("a,\" \",c"))
    }

    @Test
    fun testLineStartingWithEmptyField() {
        assertEquals(listOf(null, "a", "b"), parser.parseLine(",a,b"))
    }

    @Test
    fun testLineWithQuotesInsideUnquotedField() {
        assertEquals(listOf("a\"b", "c"), parser.parseLine("a\"b,c"))
    }

    @Test
    fun testSingleField() {
        assertEquals(listOf("a"), parser.parseLine("a"))
    }

    @Test
    fun testSingleQuotedField() {
        assertEquals(listOf("a"), parser.parseLine("\"a\""))
    }

    @Test
    fun testSingleEmptyQuotedField() {
        assertEquals(listOf(""), parser.parseLine("\"\""))
    }

    @Test
    fun testOnlyEscapedQuotes() {
        val str = "\"".repeat(1000)
        assertEquals(listOf("foo", str, "bar$str"), parser.parseLine("foo,\"$str\",bar$str"))
    }
}
