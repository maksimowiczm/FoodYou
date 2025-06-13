package com.maksimowiczm.foodyou.ui.donate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Hail
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.BuildConfig
import com.maksimowiczm.foodyou.core.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.ext.add
import com.maksimowiczm.foodyou.core.ui.utils.LocalClipboardManager
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun DonateScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    val clipboardManager = LocalClipboardManager.current

    DonateScreen(
        onBack = onBack,
        onOpenUrl = { uriHandler.openUri(it) },
        onCopy = { clipboardManager.copy("address", it) },
        onContact = { uriHandler.openUri(BuildConfig.FEEDBACK_EMAIL_URI) },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DonateScreen(
    onBack: () -> Unit,
    onOpenUrl: (String) -> Unit,
    onCopy: (String) -> Unit,
    onContact: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_donate)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = paddingValues.add(vertical = 8.dp)
        ) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CompositionLocalProvider(
                        LocalTextStyle provides MaterialTheme.typography.bodyMedium
                    ) {
                        Text(stringResource(Res.string.description_donate_1))
                        Text(stringResource(Res.string.description_donate_2))
                        Text(stringResource(Res.string.description_donate_3))
                    }
                }
            }

            item {
                Column {
                    Text(
                        text = stringResource(Res.string.donate_bank_or_card).uppercase(),
                        modifier = Modifier.padding(start = 16.dp),
                        style = MaterialTheme.typography.labelLarge
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DonateOption.fiat.forEach {
                            it.DonateCard(
                                onClick = { onOpenUrl(it.url) }
                            )
                        }
                    }
                }
            }

            items(
                items = DonateOption.crypto
            ) {
                it.DonateCard(
                    onClick = { onCopy(it.address) }
                )
            }

            item {
                ContactCard(
                    onContact = onContact
                )
            }
        }
    }
}

@Composable
private fun LinkDonateOption.DonateCard(onClick: () -> Unit, modifier: Modifier = Modifier) {
    DonateCard(
        label = name,
        leadingIcon = { Icon(Modifier.width(24.dp)) },
        trailingIcon = {
            Icon(
                imageVector = Icons.Outlined.Link,
                contentDescription = null
            )
        },
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
private fun CryptoDonateOption.DonateCard(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(
            text = name.uppercase(),
            modifier = Modifier.padding(start = 16.dp),
            style = MaterialTheme.typography.labelLarge
        )
        DonateCard(
            label = address,
            leadingIcon = { Icon(Modifier.height(24.dp)) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.ContentCopy,
                    contentDescription = null
                )
            },
            onClick = onClick
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DonateCard(
    label: String,
    leadingIcon: @Composable () -> Unit,
    trailingIcon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().heightIn(min = 56.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon()
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMediumEmphasized
            )
            trailingIcon()
        }
    }
}

@Composable
private fun ContactCard(onContact: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onContact,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Hail,
                    contentDescription = null
                )
                Text(
                    text = stringResource(Res.string.headline_contact),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Text(
                text = stringResource(Res.string.description_donate_contact),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
