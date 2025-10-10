package com.maksimowiczm.foodyou.app.ui.language

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.maksimowiczm.foodyou.app.domain.AppConfig
import com.maksimowiczm.foodyou.app.ui.common.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LanguageScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel: LanguageViewModel = koinViewModel()
    val appConfig: AppConfig = koinInject()
    val uriHandler = LocalUriHandler.current

    val currentTranslation by viewModel.translation.collectAsStateWithLifecycle()

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
            contentPadding = paddingValues.add(vertical = 8.dp),
        ) {
            item {
                TranslateButton(
                    onClick = { uriHandler.openUri(appConfig.translateUri) },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            item { Spacer(Modifier.height(8.dp)) }
            item {
                LanguageListItem(
                    languageName = stringResource(Res.string.headline_system),
                    selected = currentTranslation == null,
                    onSelect = { viewModel.onLanguageSelect(null) },
                )
            }
            items(languages) { translation ->
                LanguageListItem(
                    languageName = translation.languageName,
                    selected = translation == currentTranslation,
                    onSelect = { viewModel.onLanguageSelect(translation) },
                    authors = translation.authorsStrings,
                )
            }
        }
    }
}

@Composable
private fun LanguageListItem(
    languageName: String,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    authors: List<Author> = listOf(),
) {
    ListItem(
        headlineContent = { Text(languageName) },
        modifier = modifier.heightIn(min = 56.dp).clickable { onSelect() },
        supportingContent = {
            authors
                .takeIf { it.isNotEmpty() }
                ?.let { Column { it.forEach { author -> Text(author.toAnnotatedString()) } } }
        },
        leadingContent = { RadioButton(selected = selected, onClick = null) },
    )
}
