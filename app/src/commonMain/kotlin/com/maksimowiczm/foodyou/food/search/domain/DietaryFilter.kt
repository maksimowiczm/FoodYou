package com.maksimowiczm.foodyou.food.search.domain

/**
 * Dietary classification filter backed by Open Food Facts `ingredients_analysis_tags`.
 *
 * Each entry corresponds to a tag value already present in the OFF database. When set, only
 * products whose `ingredients_analysis_tags` list includes the matching tag are returned.
 */
enum class DietaryFilter(val tag: String) {
    Vegan("en:vegan"),
    Vegetarian("en:vegetarian"),
}
