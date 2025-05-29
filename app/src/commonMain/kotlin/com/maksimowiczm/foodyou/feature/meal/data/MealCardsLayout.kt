package com.maksimowiczm.foodyou.feature.meal.data

import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.ext.getBlocking
import com.maksimowiczm.foodyou.core.ext.observe
import com.maksimowiczm.foodyou.core.ext.set
import kotlinx.coroutines.flow.map

internal enum class MealCardsLayout {
    Horizontal,
    Vertical
}

private fun Boolean?.toMealCardsLayout(): MealCardsLayout = if (this == false) {
    MealCardsLayout.Horizontal
} else {
    MealCardsLayout.Vertical
}

internal suspend fun DataStore<Preferences>.setMealCardsLayout(layout: MealCardsLayout) =
    set(MealPreferences.useHorizontalLayout to (layout == MealCardsLayout.Vertical))

internal fun DataStore<Preferences>.observeMealCardsLayout() =
    observe(MealPreferences.useHorizontalLayout).map { it.toMealCardsLayout() }

@Composable
internal fun DataStore<Preferences>.collectMealCardsLayout() = observeMealCardsLayout()
    .collectAsStateWithLifecycle(
        getBlocking(MealPreferences.useHorizontalLayout).toMealCardsLayout()
    )
