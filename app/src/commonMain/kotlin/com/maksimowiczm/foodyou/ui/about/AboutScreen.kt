package com.maksimowiczm.foodyou.ui.about

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.BuildConfig
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.action_go_back
import foodyou.app.generated.resources.app_name
import foodyou.app.generated.resources.headline_launcher_icon_by_icons8
import foodyou.app.generated.resources.headline_version
import foodyou.app.generated.resources.ic_sushi
import foodyou.app.generated.resources.link_icons8
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        // Padding according to the Material Design App bars guidelines
        // https://m3.material.io/components/app-bars/specs
        val insets = TopAppBarDefaults.windowInsets
        val padding = PaddingValues(top = 8.dp, start = 4.dp)

        Box(
            modifier = Modifier
                .windowInsetsPadding(insets)
                .consumeWindowInsets(insets)
                .padding(padding)
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

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = WindowInsets.safeDrawing.asPaddingValues()
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
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun InteractiveLogo(
    modifier: Modifier = Modifier,
    iconColor: Color = MaterialTheme.colorScheme.surface,
    backgroundColor: Color = MaterialTheme.colorScheme.outline
) {
    val shapes = remember {
        listOf(
            MaterialShapes.Square,
            MaterialShapes.Gem,
            MaterialShapes.Sunny,
            MaterialShapes.Cookie6Sided
        )
    }

    var shapeIndex by rememberSaveable { mutableIntStateOf(0) }
    val shape by remember { derivedStateOf { shapes[shapeIndex] } }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(350.dp)
                .clip(shape.toShape())
                .background(backgroundColor)
                .clickable { shapeIndex = (shapeIndex + 1) % shapes.size },
            contentAlignment = Alignment.Center
        ) {
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
