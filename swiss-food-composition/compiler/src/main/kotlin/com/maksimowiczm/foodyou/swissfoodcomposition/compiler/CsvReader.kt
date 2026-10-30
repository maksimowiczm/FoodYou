package com.maksimowiczm.foodyou.swissfoodcomposition.compiler

import arrow.core.Either
import arrow.core.nonEmptyListOf
import arrow.core.raise.either
import arrow.core.raise.ensure
import java.io.File
import java.io.PushbackReader
import java.io.Reader

typealias CsvContent = List<Map<String, String?>>

class CsvReader {
    fun readCsv(file: File): Either<Errors, CsvContent> = either {
        ensure(file.exists()) {
            nonEmptyListOf("CSV file does not exist: '${file.path}'")
        }
        ensure(file.isFile) {
            nonEmptyListOf("CSV path is not a file: '${file.path}'")
        }

        Either.catch {
                file.bufferedReader().use { reader ->
                    parseCsv(reader)
                }
            }
            .mapLeft { throwable ->
                nonEmptyListOf(
                    "Failed to read CSV file '${file.path}': ${throwable.message ?: throwable.toString()}"
                )
            }
            .bind()
    }

    private fun parseCsv(rawReader: Reader): CsvContent {
        val reader = PushbackReader(rawReader)
        val rows = mutableListOf<List<String?>>()
        val currentField = StringBuilder()
        val currentRow = mutableListOf<String?>()
        var inQuotes = false

        while (true) {
            val rawChar = reader.read()
            if (rawChar == -1) break
            val ch = rawChar.toChar()

            if (inQuotes) {
                if (ch == '"') {
                    val nextChar = reader.read()
                    if (nextChar == '"'.code) {
                        currentField.append('"')
                    } else {
                        if (nextChar != -1) {
                            reader.unread(nextChar)
                        }
                        inQuotes = false
                    }
                } else {
                    currentField.append(ch)
                }
            } else {
                when (ch) {
                    '"' -> {
                        inQuotes = true
                    }

                    ',' -> {
                        val text = currentField.toString()
                        currentRow.add(text.ifEmpty { null })
                        currentField.clear()
                    }

                    '\r' -> {
                        // Ignore \r
                    }

                    '\n' -> {
                        val text = currentField.toString()
                        currentRow.add(text.ifEmpty { null })
                        currentField.clear()

                        if ((currentRow.size > 1) || (currentRow.firstOrNull() != null)) {
                            rows.add(currentRow.toList())
                        }
                        currentRow.clear()
                    }

                    else -> {
                        currentField.append(ch)
                    }
                }
            }
        }

        if (currentField.isNotEmpty() || currentRow.isNotEmpty()) {
            val text = currentField.toString()
            currentRow.add(text.ifEmpty { null })
            if ((currentRow.size > 1) || (currentRow.firstOrNull() != null)) {
                rows.add(currentRow.toList())
            }
        }

        if (rows.isEmpty()) return emptyList()

        val headers = rows.first()
        var currentNutrient = ""
        val keys = headers.mapIndexed { index, h ->
            val header = h ?: "COLUMN_$index"
            val metadataFields =
                setOf(
                    "ID",
                    "ID V 4.0",
                    "ID SwissFIR",
                    "Name",
                    "Synonyms",
                    "Category",
                    "Density",
                    "Matrix unit",
                    "Record has changed",
                )
            if (
                header !in metadataFields && header != "Derivation of value" && header != "Source"
            ) {
                currentNutrient = header
                header
            } else if (header == "Derivation of value" || header == "Source") {
                if (currentNutrient.isNotEmpty()) {
                    "$currentNutrient - $header"
                } else {
                    header
                }
            } else {
                header
            }
        }

        return rows
            .asSequence()
            .drop(1)
            .map { row ->
                keys
                    .mapIndexed { index, key ->
                        key to row.getOrNull(index)
                    }
                    .toMap()
            }
            .toList()
    }
}
