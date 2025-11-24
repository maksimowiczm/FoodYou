package com.maksimowiczm.foodyou.app.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.maksimowiczm.foodyou.app.ui.common.component.InteractiveLogo
import com.maksimowiczm.foodyou.common.compose.extension.add
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BeforeYouStartScreen(onAgree: () -> Unit, modifier: Modifier = Modifier) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.headline_before_you_start),
                        style = MaterialTheme.typography.displaySmall,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        Box(
            modifier =
                Modifier.fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding =
                    paddingValues
                        .add(bottom = 72.dp) // Button height + padding
                        .add(vertical = 8.dp),
            ) {
                item {
                    InteractiveLogo(
                        Modifier.padding(horizontal = 64.dp)
                            .widthIn(max = 350.dp)
                            .aspectRatio(1f)
                            .fillMaxSize()
                    )
                }

                item {
                    Text(
                        text = stringResource(Res.string.description_before_you_start),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            Button(
                onClick = onAgree,
                shapes = ButtonDefaults.shapes(),
                modifier =
                    Modifier.padding(bottom = 8.dp)
                        .padding(bottom = paddingValues.calculateBottomPadding())
                        .height(56.dp)
                        .align(Alignment.BottomCenter)
                        .zIndex(1f),
            ) {
                Text(stringResource(Res.string.action_agree_and_continue))
            }
        }
    }
}
