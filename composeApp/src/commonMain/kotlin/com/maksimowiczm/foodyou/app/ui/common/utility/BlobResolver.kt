package com.maksimowiczm.foodyou.app.ui.common.utility

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.common.domain.BlobDigest
import com.maksimowiczm.foodyou.common.domain.BlobResolver
import com.maksimowiczm.foodyou.common.domain.FileUri

private val LocalBlobResolver =
    staticCompositionLocalOf<BlobResolver> { error("BlobResolver not provided") }

@Composable
fun BlobResolverProvider(blobResolver: BlobResolver, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalBlobResolver provides blobResolver) { content() }
}

@Composable
fun resolveBlob(blobDigest: BlobDigest): FileUri {
    val blobResolver = LocalBlobResolver.current
    return remember(blobDigest, blobResolver) { blobResolver.resolve(blobDigest) }
}
