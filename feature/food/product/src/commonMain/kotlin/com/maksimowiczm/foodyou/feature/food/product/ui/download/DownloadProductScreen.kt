package com.maksimowiczm.foodyou.feature.food.product.ui.download

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeExtendedFloatingActionButton
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.node.Ref
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.food.domain.usecase.DownloadProductError
import com.maksimowiczm.foodyou.shared.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.shared.ui.unorderedList
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun DownloadProductScreen(
    isDownloading: Boolean,
    error: DownloadProductError?,
    textFieldState: TextFieldState,
    onBack: () -> Unit,
    onDownload: () -> Unit,
    onPaste: () -> Unit,
    onOpenFoodFacts: () -> Unit,
    onUsda: () -> Unit,
    onUpdateUsdaApiKey: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.action_download_product)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            Column(
                modifier =
                    Modifier.animateFloatingActionButton(
                        visible = !isDownloading,
                        alignment = Alignment.BottomEnd,
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End,
            ) {
                FloatingActionButton(
                    onClick = {
                        if (!isDownloading) {
                            onPaste()
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentPaste,
                        contentDescription = stringResource(Res.string.action_paste_url),
                    )
                }

                LargeExtendedFloatingActionButton(
                    onClick = {
                        if (!isDownloading) {
                            onDownload()
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(FloatingActionButtonDefaults.LargeIconSize),
                        )
                    },
                    text = { Text(stringResource(Res.string.action_download)) },
                )
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues,
        ) {
            stickyHeader {
                AnimatedContent(
                    targetState = isDownloading,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 12.dp),
                    transitionSpec = {
                        fadeIn() + expandVertically() togetherWith fadeOut() + shrinkVertically()
                    },
                ) {
                    if (it) {
                        LinearWavyProgressIndicator()
                    } else {
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }

            item {
                Text(
                    text = stringResource(Res.string.description_download_product),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            item { Spacer(Modifier.height(16.dp)) }

            item {
                OutlinedTextField(
                    state = textFieldState,
                    modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                    enabled = !isDownloading,
                    trailingIcon = {
                        if (textFieldState.text.isNotEmpty()) {
                            IconButton(onClick = textFieldState::clearText) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = stringResource(Res.string.action_clear),
                                )
                            }
                        }
                    },
                    supportingText = {
                        Text(stringResource(Res.string.description_download_product_hint))
                    },
                    placeholder = { Text(stringResource(Res.string.product_link)) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    onKeyboardAction = { onDownload() },
                )
            }

            item { Spacer(Modifier.height(16.dp)) }

            item {
                val ref = remember { Ref<DownloadProductError>() }

                ref.value = error ?: ref.value

                AnimatedVisibility(
                    visible = error != null,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                ) {
                    ref.value?.let {
                        Column {
                            DownloadErrorCard(it, onUpdateUsdaApiKey)
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }
            }

            item { SupportedUrls(modifier = Modifier.padding(horizontal = 16.dp)) }

            item { Spacer(Modifier.height(16.dp)) }

            item {
                ActionChips(
                    isDownloading = isDownloading,
                    onPaste = onPaste,
                    onOpenFoodFacts = onOpenFoodFacts,
                    onUsda = onUsda,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun SupportedUrls(modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(Res.string.headline_supported_URLs),
            style = MaterialTheme.typography.labelMedium,
        )

        Text(
            text =
                unorderedList(
                    "Open Food Facts (world.openfoodfacts.org)",
                    "USDA FoodData Central (fdc.nal.usda.gov)",
                ),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun ActionChips(
    isDownloading: Boolean,
    onPaste: () -> Unit,
    onOpenFoodFacts: () -> Unit,
    onUsda: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AssistChip(
            onClick = onPaste,
            label = { Text(stringResource(Res.string.action_paste_url)) },
            enabled = !isDownloading,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.ContentPaste,
                    contentDescription = null,
                    modifier = Modifier.size(AssistChipDefaults.IconSize),
                )
            },
        )
        AssistChip(
            onClick = onOpenFoodFacts,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(AssistChipDefaults.IconSize),
                )
            },
            label = { Text(stringResource(Res.string.action_browse_open_food_facts)) },
        )
        AssistChip(
            onClick = onUsda,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(AssistChipDefaults.IconSize),
                )
            },
            label = { Text(stringResource(Res.string.action_browse_usda)) },
        )
    }
}
