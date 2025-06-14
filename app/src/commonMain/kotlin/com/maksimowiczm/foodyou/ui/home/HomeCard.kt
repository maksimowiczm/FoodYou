package com.maksimowiczm.foodyou.ui.home

import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.ext.getBlocking
import com.maksimowiczm.foodyou.core.ext.observe
import com.maksimowiczm.foodyou.core.ext.set
import com.maksimowiczm.foodyou.data.AppPreferences
import kotlinx.coroutines.flow.map

enum class HomeCard {
    Calendar,
    Meals,
    Goals;

    companion object {
        val defaultOrder = listOf(
            Calendar,
            Goals,
            Meals
        )
    }
}

private fun String?.toHomeCards() = runCatching {
    this
        ?.split(",")
        ?.map { it.trim() }
        ?.map { HomeCard.entries[it.toInt()] }
        ?: HomeCard.defaultOrder
}.getOrElse { HomeCard.defaultOrder }

private fun List<HomeCard>.string() = joinToString(",") { it.ordinal.toString() }

@Composable
fun DataStore<Preferences>.collectHomeCardsAsState() = observe(AppPreferences.homeOrder)
    .map { it.toHomeCards() }
    .collectAsStateWithLifecycle(getBlocking(AppPreferences.homeOrder).toHomeCards())

suspend fun DataStore<Preferences>.updateHomeCards(homeCards: List<HomeCard>) =
    set(AppPreferences.homeOrder to homeCards.string())
