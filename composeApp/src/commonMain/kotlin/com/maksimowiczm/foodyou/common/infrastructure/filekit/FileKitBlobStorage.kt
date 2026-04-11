package com.maksimowiczm.foodyou.common.infrastructure.filekit

import com.maksimowiczm.foodyou.common.domain.blob.BlobDigest
import com.maksimowiczm.foodyou.common.domain.blob.BlobStorage
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absoluteFile
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.write
import okio.ByteString.Companion.toByteString

internal class FileKitBlobStorage : BlobStorage {
    override suspend fun store(bytes: ByteArray): BlobDigest {
        val digest = BlobDigest(bytes.toByteString().sha256().hex())
        val file = FileKit.filesDir / digest.value
        file.write(bytes)
        return digest
    }

    override suspend fun read(digest: BlobDigest): ByteArray? {
        val file = FileKit.filesDir / digest.value
        return if (!file.exists()) null else file.readBytes()
    }

    override suspend fun path(digest: BlobDigest): String {
        val file = FileKit.filesDir / digest.value
        return file.absoluteFile().path
    }
}

internal suspend fun BlobStorage.path(file: PlatformFile): String = path(store(file.readBytes()))
