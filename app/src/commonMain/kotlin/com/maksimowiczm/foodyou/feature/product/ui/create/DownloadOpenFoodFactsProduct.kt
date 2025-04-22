package com.maksimowiczm.foodyou.feature.product.ui.create

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.input.Input
import com.maksimowiczm.foodyou.core.util.ClipboardManager
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalAnimationApi::class)
@Composable
internal fun DownloadOpenFoodFactsProduct(
    linkInput: Input<OpenFoodFactsLinkError>,
    onLinkChange: (String) -> Unit,
    isDownloading: Boolean,
    error: DownloadProductFailed?,
    animatedVisibilityScope: AnimatedVisibilityScope,
    contentPadding: PaddingValues,
    onSearch: () -> Unit,
    onDownload: (url: String) -> Unit,
    onBarcodeScanner: () -> Unit,
    modifier: Modifier = Modifier,
    clipboardManager: ClipboardManager = koinInject()
) {
    val layoutDirection = LocalLayoutDirection.current
    val insets = WindowInsets(
        left = contentPadding.calculateLeftPadding(layoutDirection),
        right = contentPadding.calculateRightPadding(layoutDirection),
        top = contentPadding.calculateTopPadding(),
        bottom = contentPadding.calculateBottomPadding()
    )

    var fabHeight by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onDownload(linkInput.value) },
                modifier = Modifier
                    .animateFloatingActionButton(
                        visible = !animatedVisibilityScope.transition.isRunning,
                        alignment = Alignment.BottomEnd
                    ).onSizeChanged {
                        fabHeight = it.height
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = stringResource(Res.string.action_download)
                )
                Spacer(Modifier.width(8.dp))
                Text(stringResource(Res.string.action_download))
            }
        },
        contentWindowInsets = insets
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.imePadding(),
            contentPadding = paddingValues,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            stickyHeader {
                AnimatedContent(
                    targetState = isDownloading,
                    modifier = Modifier.fillMaxWidth(),
                    transitionSpec = { fadeIn() togetherWith fadeOut() }
                ) {
                    if (it) {
                        LinearProgressIndicator()
                    } else {
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }

            item {
                Text(
                    text = stringResource(Res.string.open_food_facts_disclaimer),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            item {
                OutlinedTextField(
                    value = linkInput.value,
                    onValueChange = { onLinkChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    enabled = !isDownloading,
                    isError = linkInput.isInvalid || error != null,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        if (linkInput.value.isNotEmpty()) {
                            IconButton(
                                onClick = { onLinkChange("") }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = stringResource(Res.string.action_clear)
                                )
                            }
                        }
                    },
                    label = { Text(stringResource(Res.string.product_link)) },
                    supportingText = {
                        linkInput as? Input.Invalid<OpenFoodFactsLinkError>
                            ?: return@OutlinedTextField
                        Text(linkInput.errors.stringResource())
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions {
                        onDownload(linkInput.value)
                    }
                )
            }

            item {
                CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                    LocalTextStyle provides MaterialTheme.typography.bodySmall
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    ) {
                        Text(stringResource(Res.string.headline_example_links))
                        Text(stringResource(Res.string.link_open_food_facts_product_example_1))
                        Text(stringResource(Res.string.link_open_food_facts_product_example_2))
                    }
                }
            }

            item {
                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(
                        onClick = {
                            val text = clipboardManager.paste()
                            if (text != null && text.isNotEmpty()) {
                                onLinkChange(text)
                            }
                        },
                        label = {
                            Text(stringResource(Res.string.action_paste_url))
                        },
                        enabled = !isDownloading,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.ContentPaste,
                                contentDescription = null,
                                modifier = Modifier.size(AssistChipDefaults.IconSize)
                            )
                        }
                    )
                    AssistChip(
                        onClick = onSearch,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(AssistChipDefaults.IconSize)
                            )
                        },
                        label = {
                            Text(stringResource(Res.string.action_browse_open_food_facts))
                        }
                    )
                    AssistChip(
                        onClick = onBarcodeScanner,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = null,
                                modifier = Modifier.size(AssistChipDefaults.IconSize)
                            )
                        },
                        enabled = !isDownloading,
                        label = {
                            Text(stringResource(Res.string.action_scan_barcode))
                        }
                    )
                }
            }

            item {
                if (error?.throwable != null) {
                    DownloadErrorCard(
                        error = error.throwable,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
            }

            item {
                val dp = LocalDensity.current.run { fabHeight.toDp() }
                Spacer(Modifier.height(dp))
            }
        }
    }
}

@Composable
private fun DownloadErrorCard(error: Throwable, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .animateContentSize()
        ) {
            Text(
                text = stringResource(Res.string.neutral_failed_to_download_the_product)
            )

            // TODO
            //  Might want to show more user-friendly error messages
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                text = error.toString(),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
