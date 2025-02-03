package com.maksimowiczm.foodyou.core.feature.addfood.ui.search

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.BottomAppBarScrollBehavior
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.R
import com.maksimowiczm.foodyou.core.feature.camera.ui.CameraSharedTransitionKeys
import com.maksimowiczm.foodyou.core.feature.product.ui.ProductSharedTransitionKeys
import com.maksimowiczm.foodyou.core.ui.LocalSharedTransitionScope

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SearchBottomBar(
    animatedVisibilityScope: AnimatedVisibilityScope,
    state: SearchBottomBarState,
    onCreateProduct: () -> Unit,
    onBarcodeScanner: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: BottomAppBarScrollBehavior? = null
) {
    val calories by animateIntAsState(state.totalCalories)

    val sharedTransitionScope = LocalSharedTransitionScope.current
        ?: error("No shared transition scope found")

    Surface(
        modifier = modifier,
        color = BottomAppBarDefaults.containerColor
    ) {
        Column {
            BottomAppBar(
                actions = {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // TODO
                            //  Something to fill the empty space

                            // Temporary solution TF :D
                            Text(
                                modifier = Modifier.padding(start = 8.dp),
                                text = "$calories",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                modifier = Modifier.padding(start = 4.dp),
                                text = stringResource(R.string.unit_kcal),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                            tooltip = {
                                PlainTooltip {
                                    Text(stringResource(R.string.action_create_new_product))
                                }
                            },
                            state = rememberTooltipState()
                        ) {
                            with(sharedTransitionScope) {
                                FilledIconButton(
                                    shape = MaterialTheme.shapes.medium,
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        contentColor = MaterialTheme.colorScheme.secondary,
                                        containerColor = MaterialTheme.colorScheme.onSecondary
                                    ),
                                    onClick = onCreateProduct,
                                    modifier = Modifier.sharedBounds(
                                        sharedContentState = rememberSharedContentState(
                                            ProductSharedTransitionKeys.PRODUCT_CREATE_SCREEN
                                        ),
                                        animatedVisibilityScope = animatedVisibilityScope
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = stringResource(R.string.action_create_new_product)
                                    )
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal)
                ),
                floatingActionButton = {
                    with(sharedTransitionScope) {
                        FloatingActionButton(
                            modifier = Modifier.sharedBounds(
                                sharedContentState = rememberSharedContentState(
                                    CameraSharedTransitionKeys.BARCODE_SCANNER
                                ),
                                animatedVisibilityScope = animatedVisibilityScope
                            ),
                            onClick = onBarcodeScanner
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_qr_code_scanner_24),
                                contentDescription = stringResource(R.string.action_scan_barcode)
                            )
                        }
                    }
                },
                windowInsets = WindowInsets(0),
                scrollBehavior = scrollBehavior
            )

            if (scrollBehavior != null) {
                val density = LocalDensity.current
                val bottomInsetHeight = WindowInsets.systemBars.getBottom(density)
                val spacerHeight =
                    bottomInsetHeight - bottomInsetHeight * scrollBehavior.state.collapsedFraction
                val height = with(density) { spacerHeight.toDp() }

                Spacer(Modifier.height(height))
            }
        }
    }
}
