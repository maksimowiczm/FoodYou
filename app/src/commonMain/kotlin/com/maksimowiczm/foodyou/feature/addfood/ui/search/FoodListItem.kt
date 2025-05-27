package com.maksimowiczm.foodyou.feature.addfood.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ui.theme.LocalNutrientsPalette

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun FoodListItem(
    name: @Composable () -> Unit,
    proteins: @Composable () -> Unit,
    carbohydrates: @Composable () -> Unit,
    fats: @Composable () -> Unit,
    calories: @Composable () -> Unit,
    measurement: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    containerColor: Color = Color.Transparent,
    contentColor: Color = Color.Unspecified,
    shape: Shape = RectangleShape,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
) {
    val nutrientsPalette = LocalNutrientsPalette.current

    val headlineContent = @Composable {
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.titleMediumEmphasized
        ) {
            name()
        }
    }

    val supportingContent = @Composable {
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.bodyMedium
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

    val content = @Composable {
        Row(
            modifier = Modifier.padding(contentPadding),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                headlineContent()
                supportingContent()
            }

            trailingContent?.invoke()
        }
    }

    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = modifier,
            color = containerColor,
            contentColor = contentColor,
            shape = shape,
            content = content
        )
    } else {
        Surface(
            modifier = modifier,
            color = containerColor,
            contentColor = contentColor,
            shape = shape,
            content = content
        )
    }
}
