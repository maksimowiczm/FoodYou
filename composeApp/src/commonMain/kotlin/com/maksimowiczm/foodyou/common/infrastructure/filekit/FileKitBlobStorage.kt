package com.maksimowiczm.foodyou.common.infrastructure.filekit

import com.maksimowiczm.foodyou.common.domain.FileUri
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.write
import okio.ByteString.Companion.toByteString

internal class FileKitBlobStorage {
    fun uri(digest: String): FileUri = FileUri((FileKit.filesDir / digest).absolutePath())

    suspend fun store(file: PlatformFile): String {
        val bytes = file.readBytes()
        val digest = bytes.toByteString().sha256().hex()
        val file = FileKit.filesDir / digest
        file.write(bytes)
        return digest
    }
}
