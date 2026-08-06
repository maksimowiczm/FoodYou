package com.maksimowiczm.foodyou.features.language

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.shared.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.shared.ui.extension.add
import com.maksimowiczm.foodyou.shared.ui.extension.confirm
import com.maksimowiczm.foodyou.shared.ui.theme.PreviewFoodYouTheme
import com.maksimowiczm.foodyou.shared.ui.utility.LocalAppConfig
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LanguageScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel: LanguageViewModel = koinViewModel()
    val appConfig = LocalAppConfig.current
    val uriHandler = LocalUriHandler.current

    val currentTranslation by viewModel.translation.collectAsStateWithLifecycle()

    LanguageScreen(
        onBack = onBack,
        onTranslate = { uriHandler.openUri(appConfig.translateUri) },
        onSelectTranslation = viewModel::onLanguageSelect,
        currentTranslation = currentTranslation,
        modifier = modifier,
    )
}

@Composable
private fun LanguageScreen(
    onBack: () -> Unit,
    onTranslate: () -> Unit,
    onSelectTranslation: (Translation?) -> Unit,
    currentTranslation: Translation?,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.headline_language)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues.add(8.dp),
        ) {
            item {
                TranslateButton(
                    onClick = onTranslate,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
            }
            item { Spacer(Modifier.height(8.dp)) }
            item {
                SegmentedListItem(
                    selected = currentTranslation == null,
                    onClick = {
                        onSelectTranslation(null)
                        hapticFeedback.confirm()
                    },
                    shapes =
                        ListItemDefaults.segmentedShapes(index = 0, count = languages.size + 1),
                    leadingContent = {
                        RadioButton(selected = currentTranslation == null, onClick = null)
                    },
                    colors =
                        ListItemDefaults.segmentedColors(
                            selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                    content = { Text(stringResource(Res.string.headline_system)) },
                )
            }
            itemsIndexed(languages) { index, translation ->
                SegmentedListItem(
                    selected = currentTranslation == translation,
                    onClick = {
                        onSelectTranslation(translation)
                        hapticFeedback.confirm()
                    },
                    shapes =
                        ListItemDefaults.segmentedShapes(
                            index = index + 1,
                            count = languages.size + 1,
                        ),
                    leadingContent = {
                        RadioButton(selected = currentTranslation == translation, onClick = null)
                    },
                    colors =
                        ListItemDefaults.segmentedColors(
                            selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                    content = { Text(translation.languageName) },
                )
            }
        }
    }
}

@Preview
@Composable
private fun LanguageScreenPreview() {
    PreviewFoodYouTheme {
        LanguageScreen(
            onBack = {},
            onTranslate = {},
            onSelectTranslation = {},
            currentTranslation = null,
        )
    }
}
