package com.maksimowiczm.foodyou.business.shared.domain.infrastructure.csv

fun interface CsvParser {
    /**
     * Parses a single line of CSV input and returns a list of fields. Empty fields are represented
     * as null.
     */
    fun parseLine(line: String): List<String?>
}
