package com.maksimowiczm.foodyou.app.ui.food.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Immutable
internal data class FoodFilter(val source: Source = DefaultFilter) {

    companion object {
        val DefaultFilter = Source.Favorite
    }

    enum class Source {
        Favorite,
        YourFood,
        OpenFoodFacts,
        USDA;

        @Composable
        fun Icon(modifier: Modifier = Modifier) =
            when (this) {
                Favorite ->
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Filled.Favorite,
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
                Favorite -> stringResource(Res.string.headline_favorite)
                YourFood -> stringResource(Res.string.headline_your_food)
                OpenFoodFacts -> stringResource(Res.string.headline_open_food_facts)
                USDA -> stringResource(Res.string.headline_food_data_central_usda)
            }
    }
}
