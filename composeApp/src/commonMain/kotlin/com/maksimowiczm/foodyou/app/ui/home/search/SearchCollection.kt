package com.maksimowiczm.foodyou.app.ui.home.search

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.headline_favorite
import foodyou.app.generated.resources.headline_fooddata_central
import foodyou.app.generated.resources.headline_open_food_facts
import foodyou.app.generated.resources.headline_your_food
import foodyou.app.generated.resources.openfoodfacts_logo
import foodyou.app.generated.resources.usda_logo
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource

@Immutable
@Serializable
internal sealed interface SearchCollection {
    @Composable fun Icon(selected: Boolean, modifier: Modifier = Modifier)

    @Composable fun stringResource(): String

    @Immutable
    @Serializable
    class Favorite : SearchCollection {
        @Composable
        override fun Icon(selected: Boolean, modifier: Modifier) {
            androidx.compose.material3.Icon(
                imageVector =
                    if (selected) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = null,
                modifier = modifier,
            )
        }

        @Composable
        override fun stringResource(): String =
            org.jetbrains.compose.resources.stringResource(Res.string.headline_favorite)
    }

    @Immutable
    @Serializable
    class UserFood : SearchCollection {
        @Composable
        override fun Icon(selected: Boolean, modifier: Modifier) {
            androidx.compose.material3.Icon(
                imageVector = if (selected) Icons.Filled.Person else Icons.Outlined.Person,
                contentDescription = null,
                modifier = modifier,
            )
        }

        @Composable
        override fun stringResource(): String =
            org.jetbrains.compose.resources.stringResource(Res.string.headline_your_food)
    }

    @Immutable
    @Serializable
    class OpenFoodFacts : SearchCollection {
        @Composable
        override fun Icon(selected: Boolean, modifier: Modifier) {
            Image(
                painter = painterResource(Res.drawable.openfoodfacts_logo),
                contentDescription = null,
                modifier = modifier,
            )
        }

        @Composable
        override fun stringResource(): String =
            org.jetbrains.compose.resources.stringResource(Res.string.headline_open_food_facts)
    }

    @Immutable
    @Serializable
    class FoodDataCentral : SearchCollection {
        @Composable
        override fun Icon(selected: Boolean, modifier: Modifier) {
            Image(
                painter = painterResource(Res.drawable.usda_logo),
                contentDescription = null,
                modifier = modifier,
            )
        }

        @Composable
        override fun stringResource(): String =
            org.jetbrains.compose.resources.stringResource(Res.string.headline_fooddata_central)
    }
}
