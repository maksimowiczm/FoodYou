package com.maksimowiczm.foodyou.business.shared.infrastructure.file

import com.maksimowiczm.foodyou.business.shared.domain.file.FileReader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.io.Buffer
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray

internal class KotlinxReader : FileReader {
    override fun read(path: String): Flow<ByteArray> = flow {
        Path(path).let(SystemFileSystem::source).use {
            val buffer = Buffer()
            while (true) {
                val read = it.readAtMostTo(buffer, BUFFER_SIZE)
                if (read <= 0) {
                    break
                }
                emit(buffer.readByteArray())
            }
        }
    }

    private companion object {
        const val BUFFER_SIZE = 1024L * 16
    }
}
