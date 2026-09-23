package com.maksimowiczm.foodyou.features.about

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.capabilities.brand.brand
import com.maksimowiczm.foodyou.capabilities.theme.PreviewFoodYouTheme
import com.maksimowiczm.foodyou.shared.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.shared.ui.extension.add
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

internal val contributors =
    listOf(
        "Bruno - Português for his wife",
        "DarjanZlobec",
        "GrizzleNL",
        "loomweaver",
        "marsianer",
        "Martin Best",
        "mikropsoft",
        "serrq",
    )

@Composable
fun ContributorsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    val offset =
        rememberInfiniteTransition()
            .animateFloat(
                initialValue = 0f,
                targetValue = 2f,
                animationSpec =
                    infiniteRepeatable(
                        animation = tween(durationMillis = 10_000, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse,
                    ),
            )
    val brush =
        remember(offset) {
            object : ShaderBrush() {
                override fun createShader(size: Size): Shader {
                    val widthOffset = size.width * offset.value
                    val heightOffset = size.height * offset.value
                    return LinearGradientShader(
                        colors =
                            listOf(
                                colorScheme.primary,
                                colorScheme.secondary,
                                colorScheme.tertiary,
                            ),
                        from = Offset(widthOffset, heightOffset),
                        to = Offset(widthOffset + size.width, heightOffset + size.height),
                        tileMode = TileMode.Mirror,
                    )
                }
            }
        }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = modifier,
    ) { contentPadding ->
        LazyColumn(
            modifier =
                Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                    .padding(horizontal = 8.dp),
            contentPadding = contentPadding.add(vertical = 8.dp),
        ) {
            item {
                Text(
                    text = stringResource(Res.string.headline_special_thanks_to),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.brand.displaySmall.copy(brush = brush),
                )
            }
            item { Spacer(Modifier.height(16.dp)) }
            itemsIndexed(
                items = contributors,
                key = { _, it -> it },
            ) { i, it ->
                Text(
                    text = it,
                    style = MaterialTheme.typography.brand.titleLarge,
                )
                if (i < contributors.lastIndex) Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Preview
@Composable
private fun ContributorsScreenPreview() {
    PreviewFoodYouTheme {
        ContributorsScreen(onBack = {})
    }
}
