package com.maksimowiczm.foodyou.feature.about.ui

import FoodYou.feature.about.BuildConfig
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.maksimowiczm.foodyou.core.ui.InteractiveLogo
import com.maksimowiczm.foodyou.core.ui.SettingsListItem
import com.maksimowiczm.foodyou.feature.about.domain.Changelog
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AboutScreen(onBack: () -> Unit, onSponsor: () -> Unit, modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current

    val linkSourceCode = stringResource(Res.string.link_github_repository)
    val linkFeatureRequest = stringResource(Res.string.link_github_issue)
    val linkBugReport = stringResource(Res.string.link_github_issue)

    var showChangelog by rememberSaveable { mutableStateOf(false) }

    if (showChangelog) {
        ChangelogModalBottomSheet(
            onDismissRequest = { showChangelog = false }
        )
    }

    AboutScreen(
        onBack = onBack,
        onSponsor = onSponsor,
        onSourceCode = { uriHandler.openUri(linkSourceCode) },
        onChangelog = { showChangelog = true },
        onIdeas = { uriHandler.openUri(linkFeatureRequest) },
        onFeatureRequest = { uriHandler.openUri(linkFeatureRequest) },
        onBugReport = { uriHandler.openUri(linkBugReport) },
        onEmail = { uriHandler.openUri(BuildConfig.FEEDBACK_EMAIL_URI) },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AboutScreen(
    onBack: () -> Unit,
    onSponsor: () -> Unit,
    onSourceCode: () -> Unit,
    onChangelog: () -> Unit,
    onIdeas: () -> Unit,
    onFeatureRequest: () -> Unit,
    onBugReport: () -> Unit,
    onEmail: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        // Padding according to the Material Design App bars guidelines
        // https://m3.material.io/components/app-bars/specs
        val insets = TopAppBarDefaults.windowInsets
        val padding = PaddingValues(top = 8.dp, start = 4.dp)

        Box(
            modifier = Modifier
                .windowInsetsPadding(insets)
                .consumeWindowInsets(insets)
                .padding(padding)
                .zIndex(100f)
        ) {
            FilledIconButton(
                onClick = onBack,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(Res.string.action_go_back)
                )
            }
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .safeDrawingPadding()
                .fillMaxSize()
        ) {
            InteractiveLogo(Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            LogoLabel(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            AboutButtons(
                onSponsor = onSponsor,
                onSourceCode = onSourceCode,
                onChangelog = onChangelog,
                onIdeas = onIdeas,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            SettingsListItem(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.VolunteerActivism,
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(Res.string.headline_sponsor)) },
                onClick = onSponsor,
                supportingContent = {
                    Text(stringResource(Res.string.description_sponsor_short))
                }
            )
            SettingsListItem(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Code,
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(Res.string.headline_source_code)) },
                onClick = onSourceCode,
                supportingContent = {
                    Text(stringResource(Res.string.description_source_code))
                }
            )
            SettingsListItem(
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.TrendingUp,
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(Res.string.headline_changelog)) },
                onClick = onChangelog,
                supportingContent = { Text(stringResource(Res.string.description_changelog)) }
            )
            SettingsListItem(
                label = {
                    Text(stringResource(Res.string.action_feature_request_on_github))
                },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Lightbulb,
                        contentDescription = null
                    )
                },
                onClick = onFeatureRequest
            )
            SettingsListItem(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.BugReport,
                        contentDescription = null
                    )
                },
                label = {
                    Text(stringResource(Res.string.action_bug_report_on_github))
                },
                onClick = onBugReport
            )
            SettingsListItem(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = null
                    )
                },
                label = {
                    Text(stringResource(Res.string.action_write_an_email))
                },
                onClick = onEmail
            )
        }
    }
}

@Composable
private fun LogoLabel(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(Res.string.app_name),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = buildString {
                append(stringResource(Res.string.headline_version))
                append(" ")
                append(Changelog.currentVersion?.version)
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = buildAnnotatedString {
                val str = stringResource(Res.string.headline_launcher_icon_by_icons8)
                val link = stringResource(Res.string.link_icons8)

                str.split(" ").forEachIndexed { index, word ->
                    if (word == "Icons8") {
                        withLink(LinkAnnotation.Url(link)) {
                            withStyle(
                                MaterialTheme.typography.bodyMedium
                                    .merge(MaterialTheme.colorScheme.primary)
                                    .toSpanStyle()
                            ) {
                                append(word)
                            }
                        }
                    } else {
                        append(word)
                    }

                    if (index < str.split(" ").lastIndex) {
                        append(" ")
                    }
                }
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AboutButtons(
    onSponsor: () -> Unit,
    onSourceCode: () -> Unit,
    onChangelog: () -> Unit,
    onIdeas: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        FilledTonalButton(
            onClick = onSponsor,
            shape = CircleShape,
            modifier = Modifier.size(72.dp, 56.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.VolunteerActivism,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }
        OutlinedButton(
            onClick = onSourceCode,
            shape = CircleShape,
            modifier = Modifier.size(72.dp, 56.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Code,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }
        OutlinedButton(
            onClick = onChangelog,
            shape = CircleShape,
            modifier = Modifier.size(72.dp, 56.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.TrendingUp,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }
        OutlinedButton(
            onClick = onIdeas,
            shape = CircleShape,
            modifier = Modifier.size(72.dp, 56.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Lightbulb,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
