package com.maksimowiczm.foodyou.business.shared.domain.file

import kotlinx.coroutines.flow.Flow

fun interface FileWriter {
    /**
     * Writes the content to the specified file path.
     *
     * @param path The path to the file where the content will be written.
     * @param content A flow of byte arrays representing the content to be written.
     */
    suspend fun write(path: String, content: Flow<ByteArray>)
}
