package com.maksimowiczm.foodyou.app.ui.common.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.account.domain.NutrientsOrder
import com.maksimowiczm.foodyou.app.ui.common.theme.LocalNutrientsPalette
import com.maksimowiczm.foodyou.app.ui.common.theme.PreviewFoodYouTheme
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalNutrientsOrder

@Composable
fun FoodListItem(
    headline: @Composable () -> Unit,
    image: @Composable (() -> Unit)?,
    proteins: @Composable () -> Unit,
    carbohydrates: @Composable () -> Unit,
    fats: @Composable () -> Unit,
    energy: @Composable () -> Unit,
    quantity: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    containerColor: Color = Color.Transparent,
    contentColor: Color = LocalContentColor.current,
    shape: Shape = RectangleShape,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
) {
    val nutrientsPalette = LocalNutrientsPalette.current
    val order = LocalNutrientsOrder.current

    val supportingContent =
        @Composable {
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyMedium) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        energy()

                        order.forEach { field ->
                            when (field) {
                                NutrientsOrder.Proteins ->
                                    CompositionLocalProvider(
                                        LocalContentColor provides
                                            nutrientsPalette.proteinsOnSurfaceContainer
                                    ) {
                                        proteins()
                                    }

                                NutrientsOrder.Fats ->
                                    CompositionLocalProvider(
                                        LocalContentColor provides
                                            nutrientsPalette.fatsOnSurfaceContainer
                                    ) {
                                        fats()
                                    }

                                NutrientsOrder.Carbohydrates ->
                                    CompositionLocalProvider(
                                        LocalContentColor provides
                                            nutrientsPalette.carbohydratesOnSurfaceContainer
                                    ) {
                                        carbohydrates()
                                    }

                                NutrientsOrder.Other,
                                NutrientsOrder.Vitamins,
                                NutrientsOrder.Minerals -> Unit
                            }
                        }
                    }
                    quantity()
                }
            }
        }

    val content =
        @Composable {
            Row(
                modifier = Modifier.padding(contentPadding),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                image?.invoke()

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    CompositionLocalProvider(
                        LocalTextStyle provides MaterialTheme.typography.titleMediumEmphasized
                    ) {
                        headline()
                    }
                    supportingContent()
                }
            }
        }

    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = modifier,
            color = containerColor,
            contentColor = contentColor,
            shape = shape,
            content = content,
        )
    } else {
        Surface(
            modifier = modifier,
            color = containerColor,
            contentColor = contentColor,
            shape = shape,
            content = content,
        )
    }
}

@Preview
@Composable
private fun FoodListItemPreview() {
    PreviewFoodYouTheme {
        Surface {
            FoodListItem(
                headline = { Text("Chicken breast") },
                image = null,
                proteins = { Text("30g") },
                carbohydrates = { Text("0g") },
                fats = { Text("3g") },
                energy = { Text("165 kcal") },
                quantity = { Text("100g") },
            )
        }
    }
}
