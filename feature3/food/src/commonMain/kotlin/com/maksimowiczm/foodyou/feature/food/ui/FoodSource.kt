package com.maksimowiczm.foodyou.feature.food.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.feature.food.domain.FoodSource
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.headline_open_food_facts
import foodyou.app.generated.resources.headline_user
import foodyou.app.generated.resources.openfoodfacts_logo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun FoodSource.Type.Icon(modifier: Modifier = Modifier) {
    when (this) {
        FoodSource.Type.User -> Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = null,
            modifier = modifier.size(24.dp)
        )

        FoodSource.Type.OpenFoodFacts -> Image(
            painter = painterResource(Res.drawable.openfoodfacts_logo),
            contentDescription = null,
            modifier = Modifier.size(24.dp).then(modifier)
        )
    }
}

@Composable
fun FoodSource.Type.stringResource(): String = when (this) {
    FoodSource.Type.User -> stringResource(Res.string.headline_user)
    FoodSource.Type.OpenFoodFacts -> stringResource(Res.string.headline_open_food_facts)
}
