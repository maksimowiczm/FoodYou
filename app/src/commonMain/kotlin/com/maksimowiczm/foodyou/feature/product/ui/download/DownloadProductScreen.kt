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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ui.component.ArrowBackIconButton
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun DownloadProductScreen(
    isMutating: Boolean,
    error: DownloadError?,
    textFieldState: TextFieldState,
    onBack: () -> Unit,
    onDownload: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

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
                enter = scaleIn() + slideIn { IntOffset(it.width / 2, it.height * 3 / 2) },
                exit = scaleOut() + slideOut { IntOffset(it.width / 2, it.height * 3 / 2) }
            ) {
                ExtendedFloatingActionButton(
                    onClick = onDownload,
                    modifier = Modifier.testTag(DownloadProductScreenTestTags.FAB)
                ) {
                    Row {
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
            contentPadding = paddingValues
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
                        text = makeBulletedList(
                            "Open Food Facts (world.openfoodfacts.org)"
                        ),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            item {
                ActionChips(
                    isMutating = isMutating,
                    onPaste = { textFieldState.setTextAndPlaceCursorAtEnd(it) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun makeBulletedList(vararg items: String): AnnotatedString {
    val bulletString = "\u2022\t\t"
    val textStyle = LocalTextStyle.current
    val textMeasurer = rememberTextMeasurer()
    val bulletStringWidth = remember(textStyle, textMeasurer) {
        textMeasurer.measure(text = bulletString, style = textStyle).size.width
    }
    val restLine = with(LocalDensity.current) { bulletStringWidth.toSp() }
    val paragraphStyle = ParagraphStyle(textIndent = TextIndent(restLine = restLine))

    return buildAnnotatedString {
        items.forEach { text ->
            withStyle(style = paragraphStyle) {
                append(bulletString)
                append(text)
            }
        }
    }
}
