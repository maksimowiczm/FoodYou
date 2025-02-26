package com.maksimowiczm.foodyou.feature.settings.aboutsettings.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.BuildConfig
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.ui.component.CardButton
import com.maksimowiczm.foodyou.ui.modifier.horizontalDisplayCutoutPadding
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun AboutScreen(
    modifier: Modifier = Modifier,
    viewModel: AboutSettingsViewModel = koinViewModel()
) {
    val githubStarClicked by viewModel.githubStar.collectAsStateWithLifecycle()

    AboutScreen(
        onRequestFeature = viewModel::openGithubIssue,
        onBugReport = viewModel::openGithubIssue,
        onReadme = viewModel::openGithubReadme,
        githubStarClicked = githubStarClicked,
        onGithubStarClick = viewModel::onGithubStarClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AboutScreen(
    onRequestFeature: () -> Unit,
    onBugReport: () -> Unit,
    onReadme: () -> Unit,
    githubStarClicked: Boolean,
    onGithubStarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val contentWindowInsets = ScaffoldDefaults.contentWindowInsets
        .exclude(WindowInsets.systemBars.only(WindowInsetsSides.Bottom))
        .exclude(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.headline_about))
                },
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = contentWindowInsets
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            item {
                ShareYourThoughtsItem(
                    onRequestFeature = onRequestFeature,
                    onBugReport = onBugReport
                )
            }

            item {
                Spacer(Modifier.height(16.dp))
            }

            item {
                ShowSomeLoveItem(
                    githubStarClicked = githubStarClicked,
                    onGithubStarClick = onGithubStarClick
                )
            }

            item {
                Spacer(Modifier.height(16.dp))
            }

            item {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .horizontalDisplayCutoutPadding(),
                    text = stringResource(R.string.headline_miscellaneous),
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
                VersionListItem()
            }

            item {
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
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
        modifier = modifier.displayCutoutPadding()
    ) {
        Text(
            text = stringResource(R.string.headline_share_your_thoughts),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Text(
            text = stringResource(R.string.description_share_your_thoughts),
            textAlign = TextAlign.Justify,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(8.dp))

        CardButton(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            leadingIcon = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.ic_github_mark),
                    contentDescription = null
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.action_feature_request_on_github),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            onClick = onRequestFeature
        )

        Spacer(Modifier.height(8.dp))

        CardButton(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            leadingIcon = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.ic_github_mark),
                    contentDescription = null
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.action_bug_report_on_github),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            onClick = onBugReport
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
        modifier = modifier.displayCutoutPadding()
    ) {
        Text(
            text = stringResource(R.string.headline_show_some_love),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Text(
            text = stringResource(R.string.description_show_some_love),
            textAlign = TextAlign.Justify,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(8.dp))

        CardButton(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
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
                            painter = painterResource(R.drawable.ic_star_24),
                            contentDescription = null
                        )
                    }
                }
            },
            text = {
                Text(
                    text = stringResource(R.string.action_leave_a_star_on_github),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            onClick = onGithubStarClick
        )
    }
}

@Composable
private fun ReadmeListItem(onReadme: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        modifier = modifier
            .clickable { onReadme() }
            .horizontalDisplayCutoutPadding(),
        headlineContent = {
            Text(stringResource(R.string.headline_readme))
        },
        supportingContent = {
            Text(stringResource(R.string.description_README_setting))
        },
        leadingContent = {
            Icon(
                painter = painterResource(id = R.drawable.ic_description_24),
                contentDescription = null
            )
        }
    )
}

@Composable
private fun VersionListItem(modifier: Modifier = Modifier) {
    ListItem(
        modifier = modifier.horizontalDisplayCutoutPadding(),
        headlineContent = {
            Text(stringResource(R.string.headline_version))
        },
        leadingContent = {
            Icon(
                painter = painterResource(id = R.drawable.ic_info_24),
                contentDescription = null
            )
        },
        supportingContent = {
            Text(
                text = BuildConfig.VERSION_NAME
            )
        }
    )
}

@PreviewFontScale
@Composable
private fun AboutScreenPreview() {
    FoodYouTheme {
        AboutScreen(
            onRequestFeature = {},
            onBugReport = {},
            onReadme = {},
            githubStarClicked = true,
            onGithubStarClick = {}
        )
    }
}
