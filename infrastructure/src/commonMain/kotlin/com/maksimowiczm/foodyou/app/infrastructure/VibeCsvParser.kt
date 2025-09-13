package com.maksimowiczm.foodyou.app.infrastructure

import com.maksimowiczm.foodyou.app.business.shared.domain.csv.CsvParser

// This is 100% vibe coded, seems to be working well
internal class VibeCsvParser : CsvParser {

    /**
     * Parses a single line of CSV input and returns a list of fields. Empty fields are represented
     * as null.
     */
    override fun parseLine(line: String): List<String?> {
        val result = mutableListOf<String?>()
        val current = StringBuilder()
        var inQuotes = false
        var fieldStarted = false
        var wasQuoted = false
        var i = 0

        while (i < line.length) {
            val char = line[i]

            when {
                char == '"' -> {
                    if (!fieldStarted && !inQuotes) {
                        // Quote at the beginning of a field - start quoted field
                        inQuotes = true
                        fieldStarted = true
                        wasQuoted = true
                    } else if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
                        // Escaped quote within quoted field - add both quotes to result
                        current.append("\"\"")
                        i++ // Skip next quote
                    } else if (inQuotes) {
                        // End of quoted field
                        inQuotes = false
                    } else {
                        // Quote within unquoted field - treat as literal
                        current.append(char)
                    }
                }

                char == ',' && !inQuotes -> {
                    // Field separator - add current field to result
                    result.add(current.toStringOrNull(wasQuoted))
                    current.clear()
                    fieldStarted = false
                    wasQuoted = false
                }

                else -> {
                    current.append(char)
                    fieldStarted = true
                }
            }
            i++
        }

        // Add the last field
        result.add(current.toStringOrNull(wasQuoted))
        return result
    }

    private fun StringBuilder.toStringOrNull(wasQuoted: Boolean): String? =
        if (this.isEmpty()) {
            if (wasQuoted) "" else null
        } else {
            this.toString()
        }
}
