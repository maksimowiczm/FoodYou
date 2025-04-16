package com.maksimowiczm.foodyou.core.input

sealed interface ValidationStrategy {
    data object FailFast : ValidationStrategy
    data object LazyEval : ValidationStrategy
}
