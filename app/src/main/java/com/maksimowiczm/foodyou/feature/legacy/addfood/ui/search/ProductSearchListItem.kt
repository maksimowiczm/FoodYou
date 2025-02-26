package com.maksimowiczm.foodyou.feature.legacy.addfood.ui.search

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.data.model.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.feature.legacy.addfood.ui.ListItem
import com.maksimowiczm.foodyou.ui.component.ToggleButton
import com.maksimowiczm.foodyou.ui.component.ToggleButtonDefaults
import com.maksimowiczm.foodyou.ui.ext.toDp
import com.maksimowiczm.foodyou.ui.modifier.horizontalDisplayCutoutPadding
import com.maksimowiczm.foodyou.ui.preview.ProductWithWeightMeasurementPreviewParameter
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Composable
fun ProductSearchListItem(
    model: ProductWithWeightMeasurement,
    onClick: () -> Unit,
    isChecked: Boolean,
    onCheckChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    colors: ProductSearchListItemColors = ProductSearchListItemDefaults.colors()
) {
    val containerColor =
        if (isChecked) colors.checkedContainerColor else colors.uncheckedContainerColor
    val contentColor = if (isChecked) colors.checkedContentColor else colors.uncheckedContentColor

    model.ListItem(
        modifier = modifier,
        onClick = onClick,
        trailingContent = {
            ToggleButton(
                checked = isChecked,
                onCheckChange = onCheckChange,
                colors = ToggleButtonDefaults.colors(
                    checkedColor = colors.checkedToggleButtonContainerColor,
                    checkedContentColor = colors.checkedToggleButtonContentColor,
                    uncheckedColor = colors.uncheckedToggleButtonContainerColor
                ),
                indication = LocalIndication.current
            ) {
                if (isChecked) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null
                    )
                }
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = containerColor,
            headlineColor = contentColor,
            overlineColor = contentColor,
            supportingColor = contentColor,
            trailingIconColor = contentColor
        )
    )
}

data class ProductSearchListItemColors(
    val uncheckedContainerColor: Color,
    val uncheckedContentColor: Color,
    val uncheckedToggleButtonContainerColor: Color,
    val checkedContainerColor: Color,
    val checkedContentColor: Color,
    val checkedToggleButtonContainerColor: Color,
    val checkedToggleButtonContentColor: Color
)

object ProductSearchListItemDefaults {
    @Composable
    fun colors(
        uncheckedContainerColor: Color = MaterialTheme.colorScheme.surface,
        uncheckedContentColor: Color = MaterialTheme.colorScheme.onSurface,
        uncheckedToggleButtonContainerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
        checkedContainerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
        checkedContentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
        checkedToggleButtonContainerColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
        checkedToggleButtonContentColor: Color = MaterialTheme.colorScheme.onTertiaryContainer
    ) = ProductSearchListItemColors(
        uncheckedContainerColor = uncheckedContainerColor,
        uncheckedContentColor = uncheckedContentColor,
        uncheckedToggleButtonContainerColor = uncheckedToggleButtonContainerColor,
        checkedContainerColor = checkedContainerColor,
        checkedContentColor = checkedContentColor,
        checkedToggleButtonContainerColor = checkedToggleButtonContainerColor,
        checkedToggleButtonContentColor = checkedToggleButtonContentColor
    )
}

@Composable
fun ProductSearchListItemSkeleton(
    modifier: Modifier = Modifier,
    shimmer: Shimmer = rememberShimmer(
        shimmerBounds = ShimmerBounds.Window
    )
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
        trailingContent = {
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Spacer(
                    Modifier
                        .shimmer(shimmer)
                        .size(24.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                )
            }
        },
        modifier = modifier.horizontalDisplayCutoutPadding()
    )
}

@Preview
@Composable
private fun ProductSearchListItemSkeletonPreview() {
    FoodYouTheme {
        ProductSearchListItemSkeleton()
    }
}

@Preview
@Composable
private fun ProductSearchListItemPreview() {
    FoodYouTheme {
        ProductSearchListItem(
            model = ProductWithWeightMeasurementPreviewParameter().values.first(),
            onClick = {},
            onCheckChange = {},
            isChecked = true
        )
    }
}

@Preview
@Preview(
    fontScale = 2f
)
@Composable
private fun ProductSearchListItemPreview(
    @PreviewParameter(ProductWithWeightMeasurementPreviewParameter::class) model:
    ProductWithWeightMeasurement
) {
    FoodYouTheme {
        ProductSearchListItem(
            model = model,
            onClick = {},
            onCheckChange = {},
            isChecked = false
        )
    }
}
