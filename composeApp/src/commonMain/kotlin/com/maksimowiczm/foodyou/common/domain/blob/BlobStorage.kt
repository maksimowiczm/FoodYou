package com.maksimowiczm.foodyou.common.domain.blob

/** Content-addressed storage for binary blobs. */
interface BlobStorage {
    suspend fun store(bytes: ByteArray): BlobDigest

    suspend fun read(digest: BlobDigest): ByteArray?

    suspend fun path(digest: BlobDigest): String
}
