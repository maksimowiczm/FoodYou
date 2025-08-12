package com.maksimowiczm.foodyou.business.shared.infrastructure.file

import com.maksimowiczm.foodyou.business.shared.domain.file.FileWriter
import kotlinx.coroutines.flow.Flow
import kotlinx.io.Buffer
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

internal class KotlinxWriter : FileWriter {
    override suspend fun write(path: String, content: Flow<ByteArray>) {
        Path(path).let(SystemFileSystem::sink).use { sink ->
            val buffer = Buffer()
            content.collect { byteArray ->
                var offset = 0
                while (offset < byteArray.size) {
                    val bytesToWrite = (byteArray.size - offset).coerceAtMost(BUFFER_SIZE.toInt())

                    buffer.write(byteArray, startIndex = offset, endIndex = offset + bytesToWrite)
                    sink.write(buffer, bytesToWrite.toLong())

                    offset += bytesToWrite
                }
            }
        }
    }

    private companion object {
        const val BUFFER_SIZE = 1024L * 16
    }
}
