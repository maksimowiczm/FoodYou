package com.maksimowiczm.foodyou.core.feature

/**
 * A manager for features that will be added to the application.
 */
object FeatureManager {
    private val _features = mutableListOf<Feature>()
    val features: List<Feature> = _features

    fun add(vararg feature: Feature) {
        _features.addAll(feature)
    }

    inline fun <reified T> get() = features.filterIsInstance<T>()
}
