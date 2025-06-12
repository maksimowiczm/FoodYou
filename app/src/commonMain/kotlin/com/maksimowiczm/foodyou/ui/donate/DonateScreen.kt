package com.maksimowiczm.foodyou.ui.donate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.BuildConfig
import com.maksimowiczm.foodyou.core.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.ext.add
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource

@Composable
fun DonateScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current

    DonateScreen(
        onBack = onBack,
        onContact = { uriHandler.openUri(BuildConfig.FEEDBACK_EMAIL_URI) },
        onKofi = {},
        onLiberapay = {},
        onBitcoin = {},
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DonateScreen(
    onBack: () -> Unit,
    onKofi: () -> Unit,
    onLiberapay: () -> Unit,
    onBitcoin: () -> Unit,
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
                Text(
                    text = stringResource(Res.string.description_donate),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            item {
                Fiat(
                    onKofi = onKofi,
                    onLiberapay = onLiberapay
                )
            }

            item {
                Bitcoin(
                    onClick = onBitcoin
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Fiat(onKofi: () -> Unit, onLiberapay: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(
            text = "BANK ACCOUNT OR CREDIT CARD",
            modifier = Modifier.padding(start = 16.dp),
            style = MaterialTheme.typography.labelLarge
        )

        DonateCard(
            onClick = onKofi,
            label = DonateConfig.KOFI_URL,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Hail,
                    contentDescription = null
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Link,
                    contentDescription = null
                )
            }
        )
        Spacer(Modifier.height(8.dp))
        DonateCard(
            onClick = onLiberapay,
            label = DonateConfig.LIBERAPAY_URL,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Hail,
                    contentDescription = null
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Link,
                    contentDescription = null
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun Bitcoin(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(
            text = "BITCOIN",
            modifier = Modifier.padding(start = 16.dp),
            style = MaterialTheme.typography.labelLarge
        )

        DonateCard(
            onClick = onClick,
            label = DonateConfig.BITCOIN,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Hail,
                    contentDescription = null
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.ContentCopy,
                    contentDescription = null
                )
            }
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
        modifier = modifier.fillMaxWidth(),
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
