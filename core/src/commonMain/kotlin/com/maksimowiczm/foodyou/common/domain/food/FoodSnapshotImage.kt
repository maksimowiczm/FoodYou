package com.maksimowiczm.foodyou.common.domain.food

import com.maksimowiczm.foodyou.common.domain.BlobDigest
import com.maksimowiczm.foodyou.common.domain.FileUri
import kotlinx.serialization.Serializable

@Serializable
sealed interface FoodSnapshotImage {
    @Serializable data class Uri(val uri: FileUri) : FoodSnapshotImage

    @Serializable data class Blob(val blob: BlobDigest) : FoodSnapshotImage
}
