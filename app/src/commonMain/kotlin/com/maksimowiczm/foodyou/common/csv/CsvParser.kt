package com.maksimowiczm.foodyou.common.csv

import kotlinx.coroutines.flow.Flow

fun interface CsvParser {
    /**
     * Parses a CSV input stream and returns a flow of lists of fields. Empty fields are represented
     * as null.
     */
    fun parse(stream: Flow<Byte>): Flow<List<String?>>
}
