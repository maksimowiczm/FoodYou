package com.maksimowiczm.foodyou.feature.productredesign

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ui.ext.plus
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun DownloadOpenFoodFactsProduct(
    animatedVisibilityScope: AnimatedVisibilityScope,
    contentPadding: PaddingValues,
    onSearch: () -> Unit,
    onDownload: () -> Unit,
    modifier: Modifier = Modifier
) {
    val layoutDirection = LocalLayoutDirection.current
    val insets = remember(contentPadding, layoutDirection) {
        WindowInsets(
            left = contentPadding.calculateLeftPadding(layoutDirection),
            right = contentPadding.calculateRightPadding(layoutDirection),
            top = contentPadding.calculateTopPadding(),
            bottom = contentPadding.calculateBottomPadding()
        )
    }

    var fabHeight by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            Column(
                modifier = Modifier
                    .animateFloatingActionButton(
                        visible = !animatedVisibilityScope.transition.isRunning,
                        alignment = Alignment.BottomEnd
                    ).onSizeChanged {
                        fabHeight = it.height
                    },
                horizontalAlignment = Alignment.End
            ) {
                SmallFloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    onClick = onSearch
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                }
                ExtendedFloatingActionButton(
                    onClick = onDownload
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Download")
                }
            }
        },
        contentWindowInsets = insets
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues + PaddingValues(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = stringResource(Res.string.open_food_facts_disclaimer),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            item {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Product link") }
                )
            }

            item {
                CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                    LocalTextStyle provides MaterialTheme.typography.bodySmall
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Example links")
                        Text("https://world.openfoodfacts.org/product/1234567890123")
                        Text("https://world.openfoodfacts.org/product/1234567890123/product-name")
                    }
                }
            }

            item {
                val dp = LocalDensity.current.run { fabHeight.toDp() }
                Spacer(Modifier.height(dp))
            }
        }
    }
}
