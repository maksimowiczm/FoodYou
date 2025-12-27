package com.maksimowiczm.foodyou.app.ui.food.search

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.app.ui.food.component.Icon
import com.maksimowiczm.foodyou.app.ui.food.component.stringResource
import com.maksimowiczm.foodyou.common.domain.food.FoodSource
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
<<<<<<< Updated upstream
        OpenFoodFacts,
        USDA,
        SwissFoodCompositionDatabase;
=======
        TBCA;
>>>>>>> Stashed changes

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

<<<<<<< Updated upstream
                OpenFoodFacts -> FoodSource.Type.OpenFoodFacts.Icon(modifier)
                USDA -> FoodSource.Type.USDA.Icon(modifier)
                SwissFoodCompositionDatabase -> FoodSource.Type.SwissFoodCompositionDatabase.Icon()
=======
                TBCA -> FoodSource.Type.TBCA.Icon(modifier)
>>>>>>> Stashed changes
            }

        @Composable
        fun stringResource(): String =
            when (this) {
                Recent -> stringResource(Res.string.headline_recent)
                YourFood -> stringResource(Res.string.headline_your_food)
<<<<<<< Updated upstream
                OpenFoodFacts -> FoodSource.Type.OpenFoodFacts.stringResource()
                USDA -> FoodSource.Type.USDA.stringResource()
                SwissFoodCompositionDatabase ->
                    stringResource(Res.string.headline_swiss_food_composition_database)
=======
                TBCA -> stringResource(Res.string.headline_tbca)
>>>>>>> Stashed changes
            }
    }
}
