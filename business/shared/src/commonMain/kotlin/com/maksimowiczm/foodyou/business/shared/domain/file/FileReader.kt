package com.maksimowiczm.foodyou.business.shared.domain.file

import kotlinx.coroutines.flow.Flow

fun interface FileReader {
    /**
     * Reads the content of a file at the specified path as a flow of byte arrays.
     *
     * @param path The path to the file to be read.
     * @return A flow of byte arrays representing the content of the file.
     */
    fun read(path: String): Flow<ByteArray>
}
