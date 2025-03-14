package com.maksimowiczm.foodyou.feature.settings.language.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LanguageScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LanguageViewModel = koinViewModel(),
    trailingContent: @Composable (Modifier) -> Unit
) {
    LanguageScreen(
        onBack = onBack,
        selectedTag = remember { viewModel.tag },
        onLanguageSelect = viewModel::onLanguageSelect,
        onHelpTranslate = viewModel::onHelpTranslate,
        modifier = modifier,
        trailingContent = trailingContent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageScreen(
    onBack: () -> Unit,
    selectedTag: String,
    onLanguageSelect: (tag: String?) -> Unit,
    onHelpTranslate: () -> Unit,
    modifier: Modifier = Modifier,
    trailingContent: @Composable (Modifier) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.headline_language)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.action_go_back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues
        ) {
            item {
                Card(
                    onClick = onHelpTranslate,
                    modifier = Modifier.padding(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = null
                        )
                        Column {
                            Text(
                                text = stringResource(Res.string.action_translate),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = stringResource(Res.string.neutral_help_translating_the_app),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            item {
                ListItem(
                    headlineContent = {
                        Text(stringResource(Res.string.headline_system))
                    },
                    leadingContent = {
                        RadioButton(
                            selected = languages.none { it.value.tag == selectedTag },
                            onClick = { onLanguageSelect(null) }
                        )
                    },
                    modifier = Modifier.clickable { onLanguageSelect(null) }
                )
            }

            languages.forEach { (name, translation) ->
                item {
                    ListItem(
                        headlineContent = {
                            Text(name)
                        },
                        supportingContent = {
                            translation.authorsStrings.takeIf { it.isNotEmpty() }?.let {
                                Column {
                                    Text(stringResource(Res.string.headline_authors))

                                    it.forEach { author ->
                                        Text(author.toAnnotatedString())
                                    }
                                }
                            }
                        },
                        leadingContent = {
                            RadioButton(
                                selected = selectedTag == translation.tag,
                                onClick = { onLanguageSelect(translation.tag) }
                            )
                        },
                        modifier = Modifier.clickable { onLanguageSelect(translation.tag) }
                    )
                }
            }

            item {
                HorizontalDivider()
            }

            item {
                trailingContent(Modifier)
            }
        }
    }
}

@Preview
@Composable
private fun LanguageScreenPreview() {
    FoodYouTheme {
        LanguageScreen(
            onBack = {},
            selectedTag = "",
            onLanguageSelect = {},
            onHelpTranslate = {},
            trailingContent = {}
        )
    }
}
