package com.maksimowiczm.foodyou.feature.food.ui.search2

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.feature.food.domain.FoodSource
import com.maksimowiczm.foodyou.feature.food.ui.Icon
import com.maksimowiczm.foodyou.feature.food.ui.stringResource
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.headline_your_food

@Immutable
internal data class FoodFilter(val source: Source = Source.YourFood) {

    val filterCount: Int
        get() {
            var count = 0

            if (source != Source.YourFood) {
                count++
            }

            return count
        }

    enum class Source {
        YourFood,
        OpenFoodFacts,
        USDA;

        @Composable
        fun Icon(modifier: Modifier = Modifier.Companion) = when (this) {
            YourFood -> androidx.compose.material3.Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                modifier = Modifier.Companion.size(24.dp).then(modifier)
            )

            OpenFoodFacts -> FoodSource.Type.OpenFoodFacts.Icon(modifier)
            USDA -> FoodSource.Type.USDA.Icon(modifier)
        }

        @Composable
        fun stringResource(): String = when (this) {
            YourFood -> org.jetbrains.compose.resources.stringResource(
                Res.string.headline_your_food
            )
            OpenFoodFacts -> FoodSource.Type.OpenFoodFacts.stringResource()
            USDA -> FoodSource.Type.USDA.stringResource()
        }
    }
}
