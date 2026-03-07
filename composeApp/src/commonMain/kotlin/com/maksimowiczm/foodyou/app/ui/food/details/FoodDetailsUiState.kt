package com.maksimowiczm.foodyou.app.ui.food.details

import androidx.compose.runtime.*
import kotlin.jvm.JvmInline

@Immutable
internal sealed interface FoodDetailsUiState<out F> {
    @Immutable
    data class Details<out F>(val food: F?, val isLoading: Boolean, val isFavorite: Boolean) :
        FoodDetailsUiState<F>

    @Immutable data object NotFound : FoodDetailsUiState<Nothing>

    @Immutable @JvmInline value class Error(val message: String?) : FoodDetailsUiState<Nothing>

    companion object {
        fun loading() = Details(null, isLoading = true, isFavorite = false)

        fun <F> partial(food: F?, isFavorite: Boolean) =
            Details(food, isLoading = true, isFavorite = isFavorite)
    }
}
