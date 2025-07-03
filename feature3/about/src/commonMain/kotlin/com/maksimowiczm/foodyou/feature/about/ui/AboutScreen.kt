package com.maksimowiczm.foodyou.feature.about.ui

import FoodYou.feature3.about.BuildConfig
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.ripple
import androidx.compose.material3.toPath
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.graphics.shapes.RoundedPolygon
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
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
            ListItem(
                headlineContent = { Text(stringResource(Res.string.headline_sponsor)) },
                modifier = Modifier.clickable { onSponsor() },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.VolunteerActivism,
                        contentDescription = null
                    )
                },
                supportingContent = {
                    Text(stringResource(Res.string.description_sponsor_short))
                }
            )
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun InteractiveLogo(
    modifier: Modifier = Modifier,
    iconColor: Color = MaterialTheme.colorScheme.onTertiaryContainer,
    backgroundColor: Color = MaterialTheme.colorScheme.tertiaryContainer
) {
    val infiniteTransition = rememberInfiniteTransition()
    val coroutineScope = rememberCoroutineScope()

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

    val morphs = remember {
        val shapes = listOf(
            MaterialShapes.Diamond,
            MaterialShapes.Gem,
            MaterialShapes.Oval,
            MaterialShapes.Pill,
            MaterialShapes.VerySunny,
            MaterialShapes.Sunny,
            MaterialShapes.Pentagon,
            MaterialShapes.Burst,
            MaterialShapes.Boom,
            MaterialShapes.Flower,
            MaterialShapes.PixelCircle,
            MaterialShapes.Cookie4Sided,
            MaterialShapes.Cookie6Sided,
            MaterialShapes.Cookie7Sided,
            MaterialShapes.Cookie9Sided,
            MaterialShapes.Cookie12Sided,
            MaterialShapes.Ghostish,
            MaterialShapes.Clover4Leaf,
            MaterialShapes.Clover8Leaf
        ).shuffled()

        val pairs = mutableListOf<Pair<RoundedPolygon, RoundedPolygon>>()
        for (i in 1 until shapes.size) {
            pairs.add(Pair(shapes[i - 1], shapes[i]))
        }
        pairs.add(Pair(shapes.last(), shapes.first()))

        pairs.map { (start, end) ->
            Morph(start, end)
        }
    }
    val progress = rememberWrapAroundCounter(morphs.size.toFloat())
    val morph by remember {
        derivedStateOf {
            val index = (progress.value / 1f).toInt()

            if (index >= morphs.size) {
                morphs[0]
            } else {
                morphs[index]
            }
        }
    }

    val motionScheme = MaterialTheme.motionScheme
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.size(350.dp).clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    coroutineScope.launch {
                        progress.increment(motionScheme.fastSpatialSpec())
                    }
                }
            ),
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
                            percentage = progress.value % 1f
                        )
                    }
                    .background(backgroundColor)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = ripple()
                    ) {
                        coroutineScope.launch {
                            progress.increment(motionScheme.slowSpatialSpec())
                        }
                    },
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
        OutlinedButton(
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

@Stable
private class WrapAroundCounter(
    private val maxValue: Float,
    private val animatable: Animatable<Float, AnimationVector1D>
) {
    val value: Float by derivedStateOf { animatable.value % maxValue }

    suspend fun increment(animationSpec: AnimationSpec<Float> = spring()) {
        animatable.animateTo(
            targetValue = (animatable.value + 1f).roundToInt().toFloat(),
            animationSpec = animationSpec
        )
    }
}

@Composable
private fun rememberWrapAroundCounter(
    maxValue: Float,
    initialValue: Float = 0f
): WrapAroundCounter {
    val animatable = remember(initialValue) { Animatable(initialValue) }

    val counter = remember(animatable, maxValue) {
        WrapAroundCounter(
            maxValue = maxValue,
            animatable = animatable
        )
    }

    return counter
}
