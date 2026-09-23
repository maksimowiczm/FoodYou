package com.maksimowiczm.foodyou.common.domain

/** A content-addressable store for raw binary data. */
interface BlobStorage {
    /**
     * Persists the given [bytes] and returns a [BlobDigest] identifying the stored blob.
     *
     * @param bytes The raw binary content to store.
     * @return A [BlobDigest] that uniquely identifies the stored content.
     */
    suspend fun store(bytes: ByteArray): BlobDigest

    /**
     * Resolves a [blobDigest] to a resource locator string (e.g. a file path or URL) that can be
     * used to access the stored blob.
     *
     * @param blobDigest The digest identifying the blob to resolve.
     * @return A [FileUri] locator pointing to the blob's storage location.
     */
    fun resolve(blobDigest: BlobDigest): FileUri
}
