package com.maksimowiczm.foodyou.app.ui.common.component

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.common.domain.FileUri
import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

@Immutable
@Serializable
sealed interface UiProfileAvatar {

    @Immutable @Serializable @JvmInline value class Uri(val uri: FileUri) : UiProfileAvatar

    @Immutable
    @Serializable
    @JvmInline
    value class Predefined(val variant: Profile.Avatar.Predefined.Variant) : UiProfileAvatar {
        companion object {
            val variants: List<Predefined>
                get() = Profile.Avatar.Predefined.Variant.entries.map(::Predefined)
        }
    }
}
