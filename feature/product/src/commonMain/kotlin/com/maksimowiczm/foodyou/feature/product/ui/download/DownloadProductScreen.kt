package com.maksimowiczm.foodyou.feature.product.ui.download

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumExtendedFloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.component.unorderedList
import com.maksimowiczm.foodyou.core.ui.ext.add
import com.maksimowiczm.foodyou.feature.product.domain.RemoteProduct
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal expect fun DownloadProductScreen(
    text: String?,
    onBack: () -> Unit,
    onDownload: (RemoteProduct) -> Unit,
    modifier: Modifier = Modifier
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun DownloadProductScreen(
    isMutating: Boolean,
    error: DownloadError?,
    textFieldState: TextFieldState,
    onBack: () -> Unit,
    onDownload: () -> Unit,
    onPaste: () -> Unit,
    onOpenFoodFacts: () -> Unit,
    onSuggestDatabase: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var fabHeightPx by remember { mutableIntStateOf(0) }
    val fabHeightDp = with(LocalDensity.current) { fabHeightPx.toDp() }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.action_download_product)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !isMutating,
                modifier = Modifier.onGloballyPositioned { fabHeightPx = it.size.height },
                enter = scaleIn() + slideIn { IntOffset(it.width / 2, it.height * 3 / 2) },
                exit = scaleOut() + slideOut { IntOffset(it.width / 2, it.height * 3 / 2) }
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    FloatingActionButton(
                        onClick = {
                            if (!isMutating) {
                                onPaste()
                            }
                        },
                        modifier = Modifier.testTag(DownloadProductScreenTestTags.PASTE_FAB),
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentPaste,
                            contentDescription = stringResource(Res.string.action_paste_url)
                        )
                    }

                    MediumExtendedFloatingActionButton(
                        onClick = onDownload,
                        modifier = Modifier.testTag(DownloadProductScreenTestTags.DOWNLOAD_FAB)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(Res.string.action_download))
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = paddingValues.add(PaddingValues(bottom = fabHeightDp + 16.dp))
        ) {
            stickyHeader {
                AnimatedContent(
                    targetState = isMutating,
                    modifier = Modifier.fillMaxWidth(),
                    transitionSpec = {
                        fadeIn() + expandVertically() togetherWith fadeOut() + shrinkVertically()
                    }
                ) {
                    if (it) {
                        LinearProgressIndicator(
                            Modifier.testTag(DownloadProductScreenTestTags.PROGRESS_INDICATOR)
                        )
                    } else {
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }

            item {
                Text(
                    text = stringResource(Res.string.description_download_product),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            item {
                OutlinedTextField(
                    state = textFieldState,
                    modifier = Modifier
                        .testTag(DownloadProductScreenTestTags.TEXT_FIELD)
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    enabled = !isMutating,
                    trailingIcon = {
                        if (textFieldState.text.isNotEmpty()) {
                            IconButton(
                                onClick = textFieldState::clearText
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = stringResource(Res.string.action_clear)
                                )
                            }
                        }
                    },
                    supportingText = {
                        Text(stringResource(Res.string.description_download_product_hint))
                    },
                    placeholder = { Text(stringResource(Res.string.product_link)) },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    onKeyboardAction = { onDownload() }
                )
            }

            item {
                if (error != null) {
                    DownloadErrorCard(
                        error = error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .testTag(DownloadProductScreenTestTags.ERROR_CARD)
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.headline_supported_URLs),
                        style = MaterialTheme.typography.labelMedium
                    )

                    Text(
                        text = unorderedList(
                            "Open Food Facts (world.openfoodfacts.org)",
                            "USDA FoodData Central (fdc.nal.usda.gov)"
                        ),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            item {
                ActionChips(
                    isMutating = isMutating,
                    onPaste = onPaste,
                    onOpenFoodFacts = onOpenFoodFacts,
                    onSuggestDatabase = onSuggestDatabase,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}
