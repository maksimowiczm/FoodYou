package com.maksimowiczm.foodyou.feature.food.shared.ui.res

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.business.food.domain.FoodSource
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun FoodSource.Type.Icon(modifier: Modifier = Modifier) {
    when (this) {
        FoodSource.Type.User ->
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                modifier = modifier.size(24.dp),
            )

        FoodSource.Type.OpenFoodFacts ->
            Image(
                painter = painterResource(Res.drawable.openfoodfacts_logo),
                contentDescription = null,
                modifier = modifier.size(24.dp),
            )

        FoodSource.Type.USDA ->
            Image(
                painter = painterResource(Res.drawable.usda_logo),
                contentDescription = null,
                modifier = modifier.size(24.dp),
            )

        FoodSource.Type.SwissFoodCompositionDatabase -> Text("CH", modifier)
    }
}

@Composable
fun FoodSource.Type.stringResource(): String =
    when (this) {
        FoodSource.Type.User -> stringResource(Res.string.headline_user)
        FoodSource.Type.OpenFoodFacts -> stringResource(Res.string.headline_open_food_facts)
        FoodSource.Type.USDA -> stringResource(Res.string.headline_food_data_central_usda)
        FoodSource.Type.SwissFoodCompositionDatabase ->
            stringResource(Res.string.headline_swiss_food_composition_database)
    }
