package com.maksimowiczm.foodyou.feature.about.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Moving
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.BuildConfig
import com.maksimowiczm.foodyou.core.ui.component.CardButton
import com.maksimowiczm.foodyou.core.ui.utils.LocalClipboardManager
import com.maksimowiczm.foodyou.feature.changelog.ChangelogModalBottomSheet
import foodyou.app.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun AboutScreen(
    modifier: Modifier = Modifier,
    viewModel: AboutSettingsViewModel = koinViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    val githubStarClicked by viewModel.githubStar.collectAsStateWithLifecycle()

    val uriHandler = LocalUriHandler.current
    var showChangelogSheet by rememberSaveable { mutableStateOf(false) }

    val requestFeatureLink = stringResource(Res.string.link_github_issue)
    val bugReportLink = stringResource(Res.string.link_github_issue)
    val readmeLink = stringResource(Res.string.link_github_repository)
    val githubStarLink = stringResource(Res.string.link_github_repository)
    val icons8Link = stringResource(Res.string.link_icons8)
    val versionString = stringResource(Res.string.headline_version)

    if (showChangelogSheet) {
        ChangelogModalBottomSheet(
            onDismissRequest = { showChangelogSheet = false }
        )
    }

    AboutScreen(
        onRequestFeature = remember(uriHandler, requestFeatureLink) {
            { uriHandler.openUri(requestFeatureLink) }
        },
        onBugReport = remember(uriHandler, bugReportLink) {
            { uriHandler.openUri(bugReportLink) }
        },
        onReadme = remember(uriHandler, readmeLink) {
            { uriHandler.openUri(readmeLink) }
        },
        onIcons8 = remember(uriHandler, icons8Link) {
            { uriHandler.openUri(icons8Link) }
        },
        githubStarClicked = githubStarClicked,
        onGithubStarClick = remember(viewModel, uriHandler, githubStarLink) {
            {
                viewModel.onGithubStarClick()
                uriHandler.openUri(githubStarLink)
            }
        },
        onChangelog = { showChangelogSheet = true },
        onVersion = remember(coroutineScope, clipboardManager, versionString) {
            {
                coroutineScope.launch {
                    clipboardManager.copy(
                        label = versionString,
                        text = BuildConfig.VERSION_NAME
                    )
                }
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AboutScreen(
    onRequestFeature: () -> Unit,
    onBugReport: () -> Unit,
    onReadme: () -> Unit,
    onIcons8: () -> Unit,
    githubStarClicked: Boolean,
    onGithubStarClick: () -> Unit,
    onChangelog: () -> Unit,
    onVersion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.headline_about)) },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues
        ) {
            item {
                ShareYourThoughtsItem(
                    onRequestFeature = onRequestFeature,
                    onBugReport = onBugReport,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Spacer(Modifier.height(16.dp))
            }

            item {
                ShowSomeLoveItem(
                    githubStarClicked = githubStarClicked,
                    onGithubStarClick = onGithubStarClick,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Spacer(Modifier.height(16.dp))
            }

            item {
                Text(
                    text = stringResource(Res.string.headline_miscellaneous),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            item {
                ReadmeListItem(
                    onReadme = onReadme
                )
            }

            item {
                AboutIcons8(
                    onOpenIcons8 = onIcons8
                )
            }

            item {
                ChangelogListItem(
                    modifier = Modifier.clickable { onChangelog() }
                )
            }

            item {
                VersionListItem(
                    modifier = Modifier.clickable { onVersion() }
                )
            }
        }
    }
}

@Composable
private fun ShareYourThoughtsItem(
    onRequestFeature: () -> Unit,
    onBugReport: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(Res.string.headline_share_your_thoughts),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge
        )

        Text(
            text = stringResource(Res.string.description_share_your_thoughts),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Justify,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(8.dp))

        CardButton(
            leadingIcon = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(Res.drawable.ic_github_mark),
                    contentDescription = null
                )
            },
            text = {
                Text(
                    text = stringResource(Res.string.action_feature_request_on_github),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            onClick = onRequestFeature,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        CardButton(
            leadingIcon = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(Res.drawable.ic_github_mark),
                    contentDescription = null
                )
            },
            text = {
                Text(
                    text = stringResource(Res.string.action_bug_report_on_github),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            onClick = onBugReport,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ShowSomeLoveItem(
    githubStarClicked: Boolean,
    onGithubStarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(Res.string.headline_show_some_love),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge
        )

        Text(
            text = stringResource(Res.string.description_show_some_love),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Justify,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(8.dp))

        CardButton(
            leadingIcon = {
                AnimatedContent(
                    targetState = githubStarClicked
                ) {
                    if (it) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color.Red
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = null
                        )
                    }
                }
            },
            text = {
                Text(
                    text = stringResource(Res.string.action_leave_a_star_on_github),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            onClick = onGithubStarClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ReadmeListItem(onReadme: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = {
            Text(stringResource(Res.string.headline_readme))
        },
        modifier = modifier.clickable { onReadme() },
        supportingContent = {
            Text(stringResource(Res.string.description_README_setting))
        },
        leadingContent = {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null
            )
        }
    )
}

@Composable
private fun VersionListItem(modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = {
            Text(stringResource(Res.string.headline_version))
        },
        modifier = modifier,
        leadingContent = {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null
            )
        },
        supportingContent = {
            Text(text = BuildConfig.VERSION_NAME)
        }
    )
}

@Composable
private fun ChangelogListItem(modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = {
            Text(stringResource(Res.string.headline_changelog))
        },
        modifier = modifier,
        leadingContent = {
            Icon(
                imageVector = Icons.Default.Moving,
                contentDescription = null
            )
        },
        supportingContent = {
            Text(stringResource(Res.string.description_changelog))
        }
    )
}
