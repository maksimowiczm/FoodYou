package com.maksimowiczm.foodyou.app.ui.food.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Immutable
internal data class FoodFilter(val source: Source) {

    companion object {
        val DefaultFilter = Source.Recent
    }

    val filterCount: Int
        get() {
            var count = 0

            if (source != DefaultFilter) {
                count++
            }

            return count
        }

    enum class Source {
        Recent,
        YourFood,
        OpenFoodFacts,
        USDA;

        @Composable
        fun Icon(modifier: Modifier = Modifier.Companion) =
            when (this) {
                Recent ->
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Filled.History,
                        contentDescription = null,
                        modifier = modifier,
                    )

                YourFood ->
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        modifier = modifier,
                    )

                OpenFoodFacts ->
                    Image(
                        painter = painterResource(Res.drawable.openfoodfacts_logo),
                        contentDescription = null,
                        modifier = modifier.size(24.dp),
                    )

                USDA ->
                    Image(
                        painter = painterResource(Res.drawable.usda_logo),
                        contentDescription = null,
                        modifier = modifier.size(24.dp),
                    )
            }

        @Composable
        fun stringResource(): String =
            when (this) {
                Recent -> stringResource(Res.string.headline_recent)
                YourFood -> stringResource(Res.string.headline_your_food)
                OpenFoodFacts -> stringResource(Res.string.headline_open_food_facts)
                USDA -> stringResource(Res.string.headline_food_data_central_usda)
            }
    }
}
