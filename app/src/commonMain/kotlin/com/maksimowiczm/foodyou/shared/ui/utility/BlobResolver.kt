package com.maksimowiczm.foodyou.shared.ui.utility

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.common.domain.BlobDigest
import com.maksimowiczm.foodyou.common.domain.BlobResolver
import com.maksimowiczm.foodyou.common.domain.FileUri

val LocalBlobResolver =
    staticCompositionLocalOf<BlobResolver> { error("BlobResolver not provided") }

@Composable
fun resolveBlob(blobDigest: BlobDigest): FileUri {
    val blobResolver = LocalBlobResolver.current
    return remember(blobDigest, blobResolver) { blobResolver.resolve(blobDigest) }
}
