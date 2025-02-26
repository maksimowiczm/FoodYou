package com.maksimowiczm.foodyou.feature

/**
 * A manager for features that will be added to the application.
 */
object FeatureManager {
    private val _features = mutableListOf<Feature>()
    val features: List<Feature> = _features

    fun addHomeFeature(vararg feature: Feature.Home) {
        _features.addAll(feature)
    }

    fun addSettingsFeature(vararg feature: Feature.Settings) {
        _features.addAll(feature)
    }

    inline fun <reified T> get() = features.filterIsInstance<T>()
}
