package com.maksimowiczm.foodyou.app.ui.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import com.maksimowiczm.foodyou.app.ui.changelog.ChangelogModalBottomSheet
import com.maksimowiczm.foodyou.app.ui.common.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.app.ui.common.component.InteractiveLogo
import com.maksimowiczm.foodyou.app.ui.common.theme.brandTypography
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalAppConfig
import com.maksimowiczm.foodyou.common.compose.component.StatusBarProtection
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun AboutScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val appConfig = LocalAppConfig.current
    val uriHandler = LocalUriHandler.current

    val scrollState = rememberScrollState()
    val systemTopBarHeight = WindowInsets.systemBars.getTop(LocalDensity.current)

    var showChangelog by rememberSaveable { mutableStateOf(false) }
    if (showChangelog) {
        ChangelogModalBottomSheet(onDismissRequest = { showChangelog = false })
    }

    Box(modifier) {
        // Padding according to the Material Design App bars guidelines
        // https://m3.material.io/components/app-bars/specs
        val insets = TopAppBarDefaults.windowInsets
        val padding = PaddingValues(top = 8.dp, start = 4.dp)

        Box(
            modifier =
                Modifier.windowInsetsPadding(insets)
                    .consumeWindowInsets(insets)
                    .padding(padding)
                    .zIndex(100f)
        ) {
            ArrowBackIconButton(onBack)
        }

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.systemBars))
            InteractiveLogo(
                Modifier.padding(horizontal = 32.dp)
                    .widthIn(max = 350.dp)
                    .aspectRatio(1f)
                    .fillMaxSize()
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(Res.string.app_name),
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = TextAlign.Center,
                style = brandTypography.brandName,
            )
            Text(
                text =
                    buildString {
                        append(stringResource(Res.string.headline_version))
                        append(" ")
                        append(appConfig.versionName)
                    },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = icons8stringResource(MaterialTheme.typography.bodyMedium),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(64.dp))
            AboutButtons(
                onSourceCode = { uriHandler.openUri(appConfig.sourceCodeUri) },
                onChangelog = { showChangelog = true },
                onIdea = { uriHandler.openUri(appConfig.issueTrackerUri) },
                onEmail = { uriHandler.openUri(appConfig.contactEmailUri) },
            )
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
        }
    }
    StatusBarProtection(
        progress = {
            lerp(start = 0f, stop = 1f, fraction = scrollState.value.toFloat() / systemTopBarHeight)
        }
    )
}

@Composable
private fun AboutButtons(
    onSourceCode: () -> Unit,
    onChangelog: () -> Unit,
    onIdea: () -> Unit,
    onEmail: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val buttonHeight = 56.dp

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
    ) {
        OutlinedButton(
            onClick = onSourceCode,
            shapes = ButtonDefaults.shapesFor(buttonHeight),
            modifier = Modifier.height(buttonHeight),
            contentPadding = ButtonDefaults.contentPaddingFor(buttonHeight),
        ) {
            Icon(
                imageVector = Icons.Outlined.Code,
                contentDescription = stringResource(Res.string.headline_source_code),
                modifier = Modifier.size(ButtonDefaults.iconSizeFor(buttonHeight)),
            )
        }
        OutlinedButton(
            onClick = onChangelog,
            shapes = ButtonDefaults.shapesFor(buttonHeight),
            modifier = Modifier.height(buttonHeight),
            contentPadding = ButtonDefaults.contentPaddingFor(buttonHeight),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.TrendingUp,
                contentDescription = stringResource(Res.string.headline_changelog),
                modifier = Modifier.size(ButtonDefaults.iconSizeFor(buttonHeight)),
            )
        }
        OutlinedButton(
            onClick = onIdea,
            shapes = ButtonDefaults.shapesFor(buttonHeight),
            modifier = Modifier.height(buttonHeight),
            contentPadding = ButtonDefaults.contentPaddingFor(buttonHeight),
        ) {
            Icon(
                imageVector = Icons.Outlined.Lightbulb,
                contentDescription = stringResource(Res.string.action_feature_request_on_github),
                modifier = Modifier.size(ButtonDefaults.iconSizeFor(buttonHeight)),
            )
        }
        OutlinedButton(
            onClick = onEmail,
            shapes = ButtonDefaults.shapesFor(buttonHeight),
            modifier = Modifier.height(buttonHeight),
            contentPadding = ButtonDefaults.contentPaddingFor(buttonHeight),
        ) {
            Icon(
                imageVector = Icons.Outlined.Mail,
                contentDescription = stringResource(Res.string.action_write_an_email),
                modifier = Modifier.size(ButtonDefaults.iconSizeFor(buttonHeight)),
            )
        }
    }
}
