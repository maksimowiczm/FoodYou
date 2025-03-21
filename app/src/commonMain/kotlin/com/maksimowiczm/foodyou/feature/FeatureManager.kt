package com.maksimowiczm.foodyou.feature

/**
 * A manager for features that will be added to the application.
 */
class FeatureManager {
    private val _features = mutableListOf<Feature>()
    val features: List<Feature> = _features

    fun addFeature(vararg feature: Feature) {
        _features.addAll(feature)
    }

    inline fun <reified T> get() = features.filterIsInstance<T>()
}
