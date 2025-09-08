package com.maksimowiczm.foodyou.feature.about.master.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.business.settings.domain.Changelog
import com.maksimowiczm.foodyou.business.settings.domain.Version
import com.maksimowiczm.foodyou.feature.about.master.presentation.ChangelogViewModel
import com.maksimowiczm.foodyou.feature.about.master.presentation.stringResource
import com.maksimowiczm.foodyou.shared.ui.unorderedList
import com.maksimowiczm.foodyou.shared.ui.utils.LocalClipboardManager
import com.maksimowiczm.foodyou.shared.ui.utils.LocalDateFormatter
import foodyou.app.generated.resources.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ChangelogModalBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val changelogViewModel: ChangelogViewModel = koinViewModel()
    val changelog = changelogViewModel.changelog.collectAsStateWithLifecycle().value

    if (changelog == null) {
        return
    }

    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        contentWindowInsets = { WindowInsets(0) },
    ) {
        SheetContent(changelog = changelog)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SheetContent(changelog: Changelog, modifier: Modifier = Modifier) {
    val paddingValues =
        WindowInsets.systemBars
            .only(WindowInsetsSides.Bottom)
            .add(WindowInsets(bottom = 8.dp))
            .asPaddingValues()

    val versions =
        remember(changelog) {
            val currentVersion = changelog.currentVersion

            if (currentVersion?.isPreview == true) {
                changelog.versions
            } else {
                changelog.versions.filterNot { it.isPreview }
            }
        }

    LazyColumn(
        modifier = modifier,
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.headline_whats_new),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall,
            )
        }

        items(items = versions) { version ->
            ChangelogItem(
                version = version,
                isCurrentVersion = version == changelog.currentVersion,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

@Composable
private fun ChangelogItem(
    version: Version,
    isCurrentVersion: Boolean,
    modifier: Modifier = Modifier,
) {
    val dateFormatter = LocalDateFormatter.current
    val clipboardManager = LocalClipboardManager.current

    val changelogString = stringResource(Res.string.headline_changelog)
    val changelogText = version.stringResource()
    val coroutineScope = rememberCoroutineScope()
    var copied by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(copied) {
        if (copied) {
            delay(2000)
            copied = false
        }
    }

    Card(
        modifier = modifier,
        colors =
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(text = version.version, style = MaterialTheme.typography.titleLarge)
                        if (isCurrentVersion) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            ) {
                                Text(stringResource(Res.string.headline_installed))
                            }
                        }
                    }
                    Text(
                        text = dateFormatter.formatDateShort(version.date),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            clipboardManager.copy(label = changelogString, text = changelogText)
                            copied = true
                        }
                    }
                ) {
                    Crossfade(targetState = copied) {
                        when (it) {
                            false ->
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = stringResource(Res.string.action_copy),
                                )

                            true ->
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                        }
                    }
                }
            }

            if (version.newFeatures.isNotEmpty()) {
                Column {
                    Text(
                        text = stringResource(Res.string.changelog_new_features),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = unorderedList(version.newFeatures),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            if (version.changes.isNotEmpty()) {
                Column {
                    Text(
                        text = stringResource(Res.string.changelog_changes),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = unorderedList(version.changes),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            if (version.bugFixes.isNotEmpty()) {
                Column {
                    Text(
                        text = stringResource(Res.string.changelog_bug_fixes),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = unorderedList(version.bugFixes),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            if (version.translations.isNotEmpty()) {
                Column {
                    Text(
                        text = stringResource(Res.string.changelog_translations),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = unorderedList(version.translations),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            version.notes?.let { notes ->
                Column {
                    Text(
                        text = stringResource(Res.string.changelog_notes),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(text = notes, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
