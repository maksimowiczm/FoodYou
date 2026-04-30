package com.maksimowiczm.foodyou.common.infrastructure.filekit

import com.maksimowiczm.foodyou.common.domain.BlobDigest
import com.maksimowiczm.foodyou.common.domain.BlobStorage
import com.maksimowiczm.foodyou.common.domain.FileUri
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.write
import okio.ByteString.Companion.toByteString

internal class FileKitBlobStorage : BlobStorage {

    override suspend fun store(bytes: ByteArray): BlobDigest {
        val digest = "sha256:" + bytes.toByteString().sha256().hex()
        val file = FileKit.filesDir / digest
        file.write(bytes)
        return BlobDigest(digest)
    }

    override fun resolve(blobDigest: BlobDigest): FileUri =
        FileUri((FileKit.filesDir / blobDigest.digest).absolutePath())
}
