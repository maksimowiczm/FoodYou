package com.maksimowiczm.foodyou.feature.food.ui.search

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun OpenFoodFactsCard(
    state: OpenFoodFactsState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color by animateColorAsState(
        targetValue = when (state) {
            is OpenFoodFactsState.Error -> MaterialTheme.colorScheme.errorContainer
            is OpenFoodFactsState.Loaded -> MaterialTheme.colorScheme.surfaceContainer
            OpenFoodFactsState.Loading -> MaterialTheme.colorScheme.surfaceContainer
            OpenFoodFactsState.PrivacyPolicyRequested -> MaterialTheme.colorScheme.errorContainer
        }
    )

    val contentColor by animateColorAsState(
        targetValue = when (state) {
            is OpenFoodFactsState.Error -> MaterialTheme.colorScheme.onErrorContainer
            is OpenFoodFactsState.Loaded -> MaterialTheme.colorScheme.onSurface
            OpenFoodFactsState.Loading -> MaterialTheme.colorScheme.onSurface
            OpenFoodFactsState.PrivacyPolicyRequested -> MaterialTheme.colorScheme.onErrorContainer
        }
    )

    Surface(
        onClick = onClick,
        modifier = modifier,
        color = color,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(
                    min = 48.dp
                ),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(Res.drawable.openfoodfacts_logo),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(Res.string.headline_open_food_facts),
                    style = MaterialTheme.typography.titleMedium
                )

                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.bodyMedium
                ) {
                    val text = when (state) {
                        is OpenFoodFactsState.Error -> stringResource(
                            Res.string.neutral_an_error_occurred
                        )
                        is OpenFoodFactsState.Loaded -> stringResource(
                            Res.string.neutral_x_products_found,
                            state.productsFound
                        )

                        OpenFoodFactsState.Loading -> stringResource(Res.string.neutral_loading)
                        OpenFoodFactsState.PrivacyPolicyRequested -> stringResource(
                            Res.string.neutral_action_required
                        )
                    }

                    Text(text)
                }
            }

            when (state) {
                is OpenFoodFactsState.Error -> Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onErrorContainer)
                        .size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.errorContainer
                    )
                }

                is OpenFoodFactsState.Loaded -> Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                OpenFoodFactsState.Loading -> ContainedLoadingIndicator()

                OpenFoodFactsState.PrivacyPolicyRequested -> Unit
            }
        }
    }
}

@Composable
internal fun BrowsingOpenFoodFactsCard(onClose: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(Res.drawable.openfoodfacts_logo),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = stringResource(Res.string.description_you_are_browsing_open_food_facts),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            FilledIconButton(
                onClick = onClose,
                modifier = Modifier.size(48.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = null
                )
            }
        }
    }
}
