package com.maksimowiczm.foodyou.feature.meal.data

import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.ext.getBlocking
import com.maksimowiczm.foodyou.core.ext.observe
import com.maksimowiczm.foodyou.core.ext.set
import kotlinx.coroutines.flow.map

enum class MealCardsLayout {
    Horizontal,
    Vertical
}

private fun Boolean?.toMealCardsLayout(): MealCardsLayout = if (this == true) {
    MealCardsLayout.Vertical
} else {
    MealCardsLayout.Horizontal
}

suspend fun DataStore<Preferences>.setMealCardsLayout(layout: MealCardsLayout) {
    set(MealPreferences.useVerticalLayout to (layout == MealCardsLayout.Vertical))
}

@Composable
fun DataStore<Preferences>.collectMealCardsLayout() = observe(MealPreferences.useVerticalLayout)
    .map { it.toMealCardsLayout() }
    .collectAsStateWithLifecycle(getBlocking(MealPreferences.useVerticalLayout).toMealCardsLayout())
