package com.maksimowiczm.foodyou.common.domain

/**
 * A functional interface for resolving a [BlobDigest] to a resource locator.
 *
 * Implementations perform a synchronous, local lookup — typically constructing a file path or URL
 * from the digest without any I/O.
 */
fun interface BlobResolver {

    /**
     * Resolves a [blobDigest] to a resource locator string (e.g. a file path or URL) that can be
     * used to access the stored blob.
     *
     * @param blobDigest The digest identifying the blob to resolve.
     * @return A [FileUri] locator pointing to the blob's storage location.
     */
    fun resolve(blobDigest: BlobDigest): FileUri
}
