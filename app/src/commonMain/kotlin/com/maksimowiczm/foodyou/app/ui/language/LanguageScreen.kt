package com.maksimowiczm.foodyou.app.ui.language

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.common.config.AppConfig
import com.maksimowiczm.foodyou.settings.domain.entity.Translation
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LanguageScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel: LanguageViewModel = koinViewModel()
    val appConfig: AppConfig = koinInject()
    val uriHandler = LocalUriHandler.current

    val selectedTranslation by viewModel.translation.collectAsStateWithLifecycle()
    val translations by viewModel.translations.collectAsStateWithLifecycle()

    LanguageScreen(
        onBack = onBack,
        onLanguageSelect = viewModel::selectTranslation,
        onHelpTranslate = { uriHandler.openUri(appConfig.translationUrl) },
        selectedTranslation = selectedTranslation,
        translations = translations,
        modifier = modifier,
    )
}

@Composable
private fun LanguageScreen(
    onBack: () -> Unit,
    onLanguageSelect: (translation: Translation?) -> Unit,
    onHelpTranslate: () -> Unit,
    selectedTranslation: Translation,
    translations: List<Translation>,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(Res.string.headline_language)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.action_go_back),
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues,
        ) {
            item { TranslateButton(onClick = onHelpTranslate, modifier = Modifier.padding(8.dp)) }

            item {
                ListItem(
                    headlineContent = { Text(stringResource(Res.string.headline_system)) },
                    leadingContent = {
                        RadioButton(selected = false, onClick = { onLanguageSelect(null) })
                    },
                    modifier = Modifier.clickable { onLanguageSelect(null) },
                )
            }

            translations.forEach { translation ->
                item {
                    ListItem(
                        headlineContent = { Text(translation.languageName) },
                        supportingContent = {
                            translation.authorsStrings
                                .takeIf { it.isNotEmpty() }
                                ?.let {
                                    Column {
                                        it.forEach { author -> Text(author.toAnnotatedString()) }
                                    }
                                }
                        },
                        leadingContent = {
                            RadioButton(
                                selected = selectedTranslation == translation,
                                onClick = { onLanguageSelect(translation) },
                            )
                        },
                        modifier = Modifier.clickable { onLanguageSelect(translation) },
                    )
                }
            }

            item { SystemSettingsListItem() }
        }
    }
}

@Composable internal expect fun SystemSettingsListItem(modifier: Modifier = Modifier)
