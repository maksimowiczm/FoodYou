package com.maksimowiczm.foodyou.ui.about

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.toPath
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.graphics.shapes.Morph
import com.maksimowiczm.foodyou.BuildConfig
import com.maksimowiczm.foodyou.core.ui.ext.add
import com.maksimowiczm.foodyou.ui.changelog.ChangelogModalBottomSheet
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.action_bug_report_on_github
import foodyou.app.generated.resources.action_feature_request_on_github
import foodyou.app.generated.resources.action_go_back
import foodyou.app.generated.resources.action_write_an_email
import foodyou.app.generated.resources.app_name
import foodyou.app.generated.resources.description_changelog
import foodyou.app.generated.resources.description_donate_short
import foodyou.app.generated.resources.description_source_code
import foodyou.app.generated.resources.headline_changelog
import foodyou.app.generated.resources.headline_donate
import foodyou.app.generated.resources.headline_launcher_icon_by_icons8
import foodyou.app.generated.resources.headline_source_code
import foodyou.app.generated.resources.headline_version
import foodyou.app.generated.resources.ic_sushi
import foodyou.app.generated.resources.link_github_issue
import foodyou.app.generated.resources.link_github_repository
import foodyou.app.generated.resources.link_icons8
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AboutScreen(onBack: () -> Unit, onDonate: () -> Unit, modifier: Modifier = Modifier) {
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
        onDonate = onDonate,
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
    onDonate: () -> Unit,
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

        val columnPadding = WindowInsets.safeDrawing.asPaddingValues()
            .add(insets.asPaddingValues())
            .add(PaddingValues(top = 8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = columnPadding
        ) {
            item {
                InteractiveLogo(Modifier.fillMaxWidth())
            }

            item {
                Spacer(Modifier.height(16.dp))
            }

            item {
                LogoLabel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            item {
                AboutButtons(
                    onDonate = onDonate,
                    onSourceCode = onSourceCode,
                    onChangelog = onChangelog,
                    onIdeas = onIdeas,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }

            item {
                ListItem(
                    headlineContent = { Text(stringResource(Res.string.headline_donate)) },
                    modifier = Modifier.clickable { onDonate() },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.VolunteerActivism,
                            contentDescription = null
                        )
                    },
                    supportingContent = {
                        Text(stringResource(Res.string.description_donate_short))
                    }
                )
            }

            item {
                ListItem(
                    headlineContent = { Text(stringResource(Res.string.headline_source_code)) },
                    modifier = Modifier.clickable { onSourceCode() },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.Code,
                            contentDescription = null
                        )
                    },
                    supportingContent = {
                        Text(stringResource(Res.string.description_source_code))
                    }
                )
            }

            item {
                ListItem(
                    headlineContent = { Text(stringResource(Res.string.headline_changelog)) },
                    modifier = Modifier.clickable { onChangelog() },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.TrendingUp,
                            contentDescription = null
                        )
                    },
                    supportingContent = { Text(stringResource(Res.string.description_changelog)) }
                )
            }

            item {
                ListItem(
                    headlineContent = {
                        Text(stringResource(Res.string.action_feature_request_on_github))
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.Lightbulb,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable { onFeatureRequest() }
                )
            }

            item {
                ListItem(
                    headlineContent = {
                        Text(stringResource(Res.string.action_bug_report_on_github))
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.BugReport,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable { onBugReport() }
                )
            }

            item {
                ListItem(
                    headlineContent = { Text(stringResource(Res.string.action_write_an_email)) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.Email,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable { onEmail() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun InteractiveLogo(
    modifier: Modifier = Modifier,
    iconColor: Color = MaterialTheme.colorScheme.onTertiaryContainer,
    backgroundColor: Color = MaterialTheme.colorScheme.tertiaryContainer
) {
    val infiniteTransition = rememberInfiniteTransition()

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(
                easing = LinearEasing,
                durationMillis = 2 * 60 * 1000
            ),
            repeatMode = RepeatMode.Restart
        )
    )
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(
                easing = LinearEasing,
                durationMillis = 5 * 1000
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    val shapeA = MaterialShapes.Sunny
    val shapeB = MaterialShapes.Pentagon
    val morph = Morph(shapeA, shapeB)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.size(350.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationZ = rotation
                        clip = true
                        shape = MorphShape(
                            morph = morph,
                            percentage = progress
                        )
                    }
                    .background(backgroundColor),
                content = {}
            )
            Icon(
                painter = painterResource(Res.drawable.ic_sushi),
                contentDescription = null,
                modifier = Modifier.size(150.dp),
                tint = iconColor
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
                append(BuildConfig.VERSION_NAME)
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AboutButtons(
    onDonate: () -> Unit,
    onSourceCode: () -> Unit,
    onChangelog: () -> Unit,
    onIdeas: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        OutlinedButton(
            onClick = onDonate,
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

private class MorphShape(private val morph: Morph, private val percentage: Float) : Shape {

    private val matrix = Matrix()

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        matrix.scale(size.width, size.height)

        val path = morph.toPath(progress = percentage)
        path.transform(matrix)

        return Outline.Generic(path)
    }
}
