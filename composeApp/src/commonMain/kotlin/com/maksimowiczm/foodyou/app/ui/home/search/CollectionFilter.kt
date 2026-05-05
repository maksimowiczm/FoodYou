package com.maksimowiczm.foodyou.app.ui.home.search

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import foodyou.app.generated.resources.*
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource

@Serializable
internal sealed interface CollectionFilter {
    @Composable fun Icon(modifier: Modifier = Modifier.Companion)

    @Composable fun stringResource(): String
}

@Immutable
@Serializable
internal data object FavoriteCollectionFilter : CollectionFilter {
    @Composable
    override fun Icon(modifier: Modifier) {
        Icon(
            imageVector = Icons.Outlined.FavoriteBorder,
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
internal data object YourFoodCollectionFilter : CollectionFilter {
    @Composable
    override fun Icon(modifier: Modifier) {
        Icon(imageVector = Icons.Outlined.Person, contentDescription = null, modifier = modifier)
    }

    @Composable
    override fun stringResource(): String =
        org.jetbrains.compose.resources.stringResource(Res.string.headline_your_food)
}

@Serializable
internal data object OpenFoodFactsCollectionFilter : CollectionFilter {
    @Composable
    override fun Icon(modifier: Modifier) {
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

@Serializable
internal data object FoodDataCentralCollectionFilter : CollectionFilter {
    @Composable
    override fun Icon(modifier: Modifier) {
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
