package com.maksimowiczm.foodyou.capabilities.foodbrowsing

import androidx.compose.runtime.*
import kotlinx.serialization.Serializable

@Immutable
@Serializable
sealed interface CollectionFilterState {
    val count: Int?

    @Immutable
    @Serializable
    data object Loading : CollectionFilterState {
        override val count = null
    }

    @Immutable @Serializable data class Loaded(override val count: Int) : CollectionFilterState

    @Immutable @Serializable data class Error(override val count: Int?) : CollectionFilterState
}
