package com.maksimowiczm.foodyou.feature.food.ui.search

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.feature.food.domain.FoodSource
import com.maksimowiczm.foodyou.feature.food.ui.Icon
import com.maksimowiczm.foodyou.feature.food.ui.stringResource
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Immutable
internal data class FoodFilter(val source: Source = DefaultFilter) {

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
        USDA,
        SwissFoodCompositionDatabase;

        @Composable
        fun Icon(modifier: Modifier = Modifier) = when (this) {
            Recent -> Icon(
                imageVector = Icons.Filled.History,
                contentDescription = null,
                modifier = modifier
            )

            YourFood -> Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                modifier = modifier
            )

            OpenFoodFacts -> FoodSource.Type.OpenFoodFacts.Icon(modifier)
            USDA -> FoodSource.Type.USDA.Icon(modifier)
            SwissFoodCompositionDatabase -> FoodSource.Type.SwissFoodCompositionDatabase.Icon()
        }

        @Composable
        fun stringResource(): String = when (this) {
            Recent -> stringResource(Res.string.headline_recent)
            YourFood -> stringResource(Res.string.headline_your_food)
            OpenFoodFacts -> FoodSource.Type.OpenFoodFacts.stringResource()
            USDA -> FoodSource.Type.USDA.stringResource()
            SwissFoodCompositionDatabase ->
                stringResource(Res.string.headline_swiss_food_composition_database)
        }
    }
}
