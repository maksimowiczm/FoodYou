package com.maksimowiczm.foodyou.core.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.model.Food
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.Product
import com.maksimowiczm.foodyou.core.model.Recipe
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource

data class IncompleteFoodData(val foodId: FoodId, val name: String) {
    companion object {

        /**
         * Creates a list of [IncompleteFoodData] from a [Food] instance.
         */
        fun fromFood(food: Food): List<IncompleteFoodData> = when (food) {
            is Product -> listOf(food)
            is Recipe -> food.flatIngredients().filterIsInstance<Product>()
        }.distinct().filterNot { it.nutritionFacts.isComplete }.map {
            IncompleteFoodData(
                foodId = it.id,
                name = it.headline
            )
        }

        fun fromFoodList(foods: List<Food>): List<IncompleteFoodData> =
            foods.flatMap { fromFood(it) }.distinctBy { it.foodId }
    }
}

@Composable
fun IncompleteFoodsList(
    foods: List<IncompleteFoodData>,
    onFoodClick: (FoodId) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text =
            "* " + stringResource(
                Res.string.description_incomplete_nutrition_data
            ),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.outline
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(Res.string.headline_incomplete_products),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.outline
        )

        foods.forEach { food ->
            Text(
                text = food.name,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable(
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    indication = null,
                    onClick = {
                        onFoodClick(food.foodId)
                    }
                )
            )
        }
    }
}
