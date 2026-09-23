package com.maksimowiczm.foodyou.shared.ui.utility

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.common.domain.BlobDigest
import com.maksimowiczm.foodyou.common.domain.BlobStorage
import com.maksimowiczm.foodyou.common.domain.FileUri

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
    @Composable fun resolve(blobDigest: BlobDigest): FileUri
}

fun BlobStorage.asBlobResolver() = BlobResolver {
    remember(this, it) { resolve(it) }
}

val LocalBlobResolver =
    staticCompositionLocalOf<BlobResolver> { error("BlobResolver not provided") }
