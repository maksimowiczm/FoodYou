package com.maksimowiczm.foodyou.common.domain.food

import com.maksimowiczm.foodyou.common.domain.BlobDigest
import com.maksimowiczm.foodyou.common.domain.FileUri
import kotlinx.serialization.Serializable

@Serializable
sealed interface FoodCompositionComponentImage {
    @Serializable data class Uri(val uri: FileUri) : FoodCompositionComponentImage

    @Serializable data class Blob(val blob: BlobDigest) : FoodCompositionComponentImage
}
