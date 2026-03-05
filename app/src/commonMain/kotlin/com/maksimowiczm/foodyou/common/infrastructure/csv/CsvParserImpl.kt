package com.maksimowiczm.foodyou.common.infrastructure.csv

import com.maksimowiczm.foodyou.common.csv.CsvParser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onCompletion

internal class CsvParserImpl : CsvParser {
    override fun parse(stream: Flow<Byte>): Flow<List<String?>> = stream.tokenize().records()
}

private fun Flow<Byte>.tokenize(): Flow<CsvToken> = map { byte ->
    when (byte) {
        COMMA -> CsvToken.Comma
        QUOTE -> CsvToken.Quote
        CR -> CsvToken.CarriageReturn
        LF -> CsvToken.LineFeed
        else -> CsvToken.RawByte(byte)
    }
}

private sealed interface CsvToken {
    data object Comma : CsvToken

    data object Quote : CsvToken

    data object CarriageReturn : CsvToken

    data object LineFeed : CsvToken

    data class RawByte(val value: Byte) : CsvToken
}

private fun Flow<CsvToken>.records(): Flow<List<String?>> {
    val parser = CsvRecordParser()
    return mapNotNull { token -> parser.feed(token) }
        .onCompletion { err -> if (err == null) parser.flush()?.let { emit(it) } }
}

/**
 * Stateful parser that consumes [CsvToken]s one at a time and assembles them into CSV records.
 *
 * Call [feed] for each token. Whenever a complete record is ready it is returned from [feed]. After
 * the token stream ends, call [flush] to drain any trailing record that was not terminated by a
 * newline.
 */
private class CsvRecordParser {
    private val field = mutableListOf<Byte>()
    private val record = mutableListOf<String?>()
    private var state: ParseState = ParseState.FieldStart

    /** Process one token. Returns the completed record if one was finished, otherwise `null`. */
    fun feed(token: CsvToken): List<String?>? {
        when (state) {
            ParseState.FieldStart ->
                when (token) {
                    CsvToken.Quote -> state = ParseState.InQuotedField
                    CsvToken.Comma -> {
                        record.add(null)
                        state = ParseState.FieldStart
                    }

                    CsvToken.CarriageReturn -> state = ParseState.CarriageReturnSeen
                    CsvToken.LineFeed -> return finishRecord()
                    is CsvToken.RawByte -> {
                        field.add(token.value)
                        state = ParseState.InField
                    }
                }

            ParseState.InField ->
                when (token) {
                    CsvToken.Comma -> {
                        flushField()
                        state = ParseState.FieldStart
                    }

                    CsvToken.CarriageReturn -> state = ParseState.CarriageReturnSeen
                    CsvToken.LineFeed -> return finishRecord()
                    CsvToken.Quote -> field.add(QUOTE) // unquoted field: treat quote as literal
                    is CsvToken.RawByte -> field.add(token.value)
                }

            ParseState.InQuotedField ->
                when (token) {
                    CsvToken.Quote -> state = ParseState.QuoteInQuotedField
                    CsvToken.CarriageReturn ->
                        field.add(CR) // RFC 4180: literal inside quoted field
                    CsvToken.LineFeed -> field.add(LF) // RFC 4180: literal inside quoted field
                    CsvToken.Comma -> field.add(COMMA) // RFC 4180: literal inside quoted field
                    is CsvToken.RawByte -> field.add(token.value)
                }

            ParseState.QuoteInQuotedField ->
                when (token) {
                    CsvToken.Quote -> {
                        field.add(QUOTE) // escaped ""
                        state = ParseState.InQuotedField
                    }

                    CsvToken.Comma -> {
                        flushField() // closing quote, end of field
                        state = ParseState.FieldStart
                    }

                    CsvToken.CarriageReturn ->
                        state = ParseState.CarriageReturnSeen // closing quote, end of record
                    CsvToken.LineFeed -> return finishRecord() // closing quote, end of record
                    is CsvToken.RawByte -> {
                        field.add(token.value) // malformed CSV, recover
                        state = ParseState.InField
                    }
                }

            ParseState.CarriageReturnSeen ->
                when (token) {
                    CsvToken.LineFeed -> return finishRecord() // proper \r\n
                    CsvToken.Comma -> {
                        val completed = finishRecord()
                        record.add(null)
                        state = ParseState.FieldStart
                        return completed
                    }

                    CsvToken.CarriageReturn -> {
                        val completed = finishRecord() // bare \r, another \r follows
                        state = ParseState.CarriageReturnSeen
                        return completed
                    }

                    CsvToken.Quote -> {
                        val completed = finishRecord()
                        state = ParseState.InQuotedField
                        return completed
                    }

                    is CsvToken.RawByte -> {
                        val completed = finishRecord()
                        field.add(token.value)
                        state = ParseState.InField
                        return completed
                    }
                }
        }

        return null
    }

    /**
     * Flushes any remaining partial record at end of stream. Returns the record if there was
     * anything buffered, otherwise `null`.
     */
    fun flush(): List<String?>? {
        if (record.isEmpty() && field.isEmpty()) return null
        return finishRecord()
    }

    private fun flushField() {
        record.add(
            if (field.isEmpty() && state == ParseState.FieldStart) null
            else field.toByteArray().decodeToString()
        )
        field.clear()
    }

    private fun finishRecord(): List<String?>? {
        flushField()
        if (record.isEmpty()) return null
        val completed = record.toList()
        record.clear()
        state = ParseState.FieldStart
        return completed
    }

    private sealed interface ParseState {
        data object FieldStart : ParseState

        data object InField : ParseState

        data object InQuotedField : ParseState

        /** Seen a quote inside a quoted field — might be `""` (escaped) or a closing quote. */
        data object QuoteInQuotedField : ParseState

        /** Seen `\r` — waiting for `\n` to form a proper CRLF line ending. */
        data object CarriageReturnSeen : ParseState
    }
}

private const val COMMA: Byte = ','.code.toByte()
private const val QUOTE: Byte = '"'.code.toByte()
private const val CR: Byte = '\r'.code.toByte()
private const val LF: Byte = '\n'.code.toByte()
