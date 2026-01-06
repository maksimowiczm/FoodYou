package com.maksimowiczm.foodyou.app.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.common.component.InteractiveLogo
import com.maksimowiczm.foodyou.app.ui.common.component.PrivacyPolicyChip
import com.maksimowiczm.foodyou.app.ui.common.theme.brandTypography
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalAppConfig
import com.maksimowiczm.foodyou.common.compose.extension.add
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun BeforeYouStartScreen(onContinue: () -> Unit, modifier: Modifier = Modifier) {
    val appConfig = LocalAppConfig.current
    val uriHandler = LocalUriHandler.current

    BeforeYouStartScreen(
        onContinue = onContinue,
        onPrivacyPolicy = { uriHandler.openUri(appConfig.privacyPolicyUri) },
        modifier = modifier,
    )
}

@Composable
private fun BeforeYouStartScreen(
    onContinue: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val fabHeight = ButtonDefaults.LargeContainerHeight

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
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            Button(
                onClick = onContinue,
                shapes = ButtonDefaults.shapesFor(fabHeight),
                contentPadding = ButtonDefaults.contentPaddingFor(fabHeight),
            ) {
                Text(
                    text = stringResource(Res.string.action_agree_and_continue),
                    style = ButtonDefaults.textStyleFor(fabHeight),
                )
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues.add(bottom = fabHeight + 16.dp).add(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                ) {
                    InteractiveLogo(
                        modifier =
                            Modifier.padding(horizontal = 64.dp)
                                .widthIn(max = 350.dp)
                                .aspectRatio(1f)
                                .fillMaxSize()
                    )
                    Text(
                        text = stringResource(Res.string.app_name),
                        style = brandTypography.brandName,
                    )
                }
            }

            item {
                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    PrivacyPolicyChip(onPrivacyPolicy)
                }
            }
            item {
                Text(
                    text = stringResource(Res.string.onboarding_privacy_tip),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}
