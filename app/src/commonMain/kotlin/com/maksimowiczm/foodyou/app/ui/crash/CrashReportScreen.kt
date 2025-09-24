package com.maksimowiczm.foodyou.app.ui.crash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.FlexibleBottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.common.compose.utility.LocalClipboardManager
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun CrashReportScreen(message: String, issueTrackerUrl: String, modifier: Modifier = Modifier) {
    val clipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current

    CrashReportScreen(
        message = message,
        onCopyAndSend = {
            clipboardManager.copy("Report", message)
            uriHandler.openUri(issueTrackerUrl)
        },
        modifier = modifier,
    )
}

@Composable
private fun CrashReportScreen(
    message: String,
    onCopyAndSend: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier,
        bottomBar = {
            FlexibleBottomAppBar(horizontalArrangement = Arrangement.Center) {
                Button(onClick = onCopyAndSend) {
                    Text(stringResource(Res.string.action_copy_and_open_bug_report))
                }
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text(
                    text = stringResource(Res.string.headline_something_went_wrong),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.headlineLarge,
                )
            }

            item {
                Text(
                    text = message,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
