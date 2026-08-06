package com.maksimowiczm.foodyou.common.domain

/**
 * A content-addressable store for raw binary data.
 *
 * Combines write ([store]) and read ([resolve]) concerns for blob persistence. If only resolution
 * is needed, prefer the narrower [BlobResolver] interface.
 */
interface BlobStorage : BlobResolver {
    /**
     * Persists the given [bytes] and returns a [BlobDigest] identifying the stored blob.
     *
     * @param bytes The raw binary content to store.
     * @return A [BlobDigest] that uniquely identifies the stored content.
     */
    suspend fun store(bytes: ByteArray): BlobDigest
}
