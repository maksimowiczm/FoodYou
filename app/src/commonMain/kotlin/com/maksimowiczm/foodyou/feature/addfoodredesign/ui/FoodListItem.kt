package com.maksimowiczm.foodyou.feature.addfoodredesign.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ui.theme.LocalNutrientsPalette

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FoodListItem(
    name: @Composable () -> Unit,
    proteins: @Composable () -> Unit,
    carbohydrates: @Composable () -> Unit,
    fats: @Composable () -> Unit,
    calories: @Composable () -> Unit,
    measurement: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    brand: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    containerColor: Color = Color.Transparent,
    contentColor: Color = Color.Unspecified
) {
    val nutrientsPalette = LocalNutrientsPalette.current

    val overlineContent = brand?.let {
        @Composable {
            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.labelMedium
            ) {
                brand()
            }
        }
    }

    val headlineContent = @Composable {
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.titleLargeEmphasized
        ) {
            name()
        }
    }

    val supportingContent = @Composable {
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.bodyLarge
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CompositionLocalProvider(
                        LocalContentColor provides nutrientsPalette.proteinsOnSurfaceContainer
                    ) {
                        proteins()
                    }
                    CompositionLocalProvider(
                        LocalContentColor provides nutrientsPalette.carbohydratesOnSurfaceContainer
                    ) {
                        carbohydrates()
                    }
                    CompositionLocalProvider(
                        LocalContentColor provides nutrientsPalette.fatsOnSurfaceContainer
                    ) {
                        fats()
                    }
                    calories()
                }

                measurement()
            }
        }
    }

    Surface(
        modifier = modifier,
        color = containerColor,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                overlineContent?.invoke()
                headlineContent()
                supportingContent()
            }

            trailingContent?.invoke()
        }
    }
}
