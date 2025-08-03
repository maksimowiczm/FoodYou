package com.maksimowiczm.foodyou.feature.food.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.preferences.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.preferences.getBlocking
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.core.ui.ext.toDp
import com.maksimowiczm.foodyou.core.ui.theme.LocalNutrientsPalette
import com.maksimowiczm.foodyou.feature.food.preferences.NutrientsOrder
import com.maksimowiczm.foodyou.feature.food.preferences.NutrientsOrderPreference
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.shimmer

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
    onClick: (() -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    containerColor: Color = Color.Transparent,
    contentColor: Color = LocalContentColor.current,
    shape: Shape = RectangleShape,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
) {
    val nutrientsPalette = LocalNutrientsPalette.current
    val orderPreference = userPreference<NutrientsOrderPreference>()
    val order = orderPreference.collectAsStateWithLifecycle(orderPreference.getBlocking()).value

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
                        LocalTextStyle provides MaterialTheme.typography.bodyMedium
                    ) {
                        calories()
                    }

                    order.forEach { field ->
                        when (field) {
                            NutrientsOrder.Proteins -> CompositionLocalProvider(
                                LocalContentColor provides
                                    nutrientsPalette.proteinsOnSurfaceContainer,
                                LocalTextStyle provides MaterialTheme.typography.bodyMedium
                            ) {
                                proteins()
                            }

                            NutrientsOrder.Fats -> CompositionLocalProvider(
                                LocalContentColor provides nutrientsPalette.fatsOnSurfaceContainer,
                                LocalTextStyle provides MaterialTheme.typography.bodyMedium
                            ) {
                                fats()
                            }

                            NutrientsOrder.Carbohydrates -> CompositionLocalProvider(
                                LocalContentColor provides
                                    nutrientsPalette.carbohydratesOnSurfaceContainer,
                                LocalTextStyle provides MaterialTheme.typography.bodyMedium
                            ) {
                                carbohydrates()
                            }

                            NutrientsOrder.Other,
                            NutrientsOrder.Vitamins,
                            NutrientsOrder.Minerals -> Unit
                        }
                    }
                }
            }
        }
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.bodyMedium
        ) {
            measurement()
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FoodErrorListItem(
    headline: String,
    errorMessage: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: Shape = RectangleShape,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
) {
    val content = @Composable {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.titleMediumEmphasized
            ) {
                Text(headline)
            }
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyMedium) {
                Text(errorMessage)
            }
        }
    }

    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            color = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            content = content
        )
    } else {
        Surface(
            modifier = modifier,
            shape = shape,
            color = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            content = content
        )
    }
}

// TODO
//  Update skeleton to match the design
@Composable
fun FoodListItemSkeleton(
    shimmer: Shimmer,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable () -> Unit)? = null
) {
    ListItem(
        headlineContent = {
            Column {
                Spacer(Modifier.height(2.dp))
                Spacer(
                    Modifier
                        .shimmer(shimmer)
                        .height(LocalTextStyle.current.toDp() - 4.dp)
                        .width(200.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                )
                Spacer(Modifier.height(2.dp))
            }
        },
        modifier = modifier,
        overlineContent = {
            Spacer(
                Modifier
                    .shimmer(shimmer)
                    .height(LocalTextStyle.current.toDp())
                    .width(100.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )
        },
        supportingContent = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(
                    Modifier
                        .shimmer(shimmer)
                        .height(LocalTextStyle.current.toDp())
                        .width(125.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                )
                Spacer(
                    Modifier
                        .shimmer(shimmer)
                        .height(LocalTextStyle.current.toDp())
                        .width(75.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                )
            }
        },
        trailingContent = trailingContent
    )
}
