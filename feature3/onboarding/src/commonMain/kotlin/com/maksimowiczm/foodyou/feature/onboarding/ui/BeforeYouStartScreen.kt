package com.maksimowiczm.foodyou.feature.onboarding.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import com.maksimowiczm.foodyou.core.ui.InteractiveLogo
import com.maksimowiczm.foodyou.core.ui.ext.add
import foodyou.app.generated.resources.*
import kotlinx.coroutines.flow.filter
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun BeforeYouStartScreen(onAgree: () -> Unit, modifier: Modifier = Modifier) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()

    var everAllScrolled by remember { mutableStateOf(false) }
    LaunchedEffect(listState) {
        snapshotFlow { listState.canScrollForward }.filter { !it }.collect {
            everAllScrolled = true
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.headline_before_you_start),
                        style = MaterialTheme.typography.displaySmall
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = paddingValues
                    .add(bottom = 72.dp) // Button height + padding
                    .add(vertical = 8.dp),
                state = listState
            ) {
                item {
                    InteractiveLogo(Modifier.fillMaxWidth())
                }

                item {
                    Text(
                        text = stringResource(Res.string.description_before_you_start),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            AnimatedVisibility(
                visible = everAllScrolled,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
                    .zIndex(1f)
                    .padding(
                        bottom = paddingValues.calculateBottomPadding()
                    ),
                enter = slideInVertically { it } + fadeIn()
            ) {
                Button(
                    onClick = onAgree,
                    shapes = ButtonDefaults.shapes(),
                    modifier = Modifier.height(56.dp)
                ) {
                    Text(
                        stringResource(Res.string.action_agree_and_continue)
                    )
                }
            }
        }
    }
}
