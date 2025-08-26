package com.maksimowiczm.foodyou.feature.about.sponsor.ui

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
import androidx.compose.material.icons.outlined.Check
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.config.AppConfig
import com.maksimowiczm.foodyou.business.sponsorship.domain.AvailableSponsorMethod
import com.maksimowiczm.foodyou.business.sponsorship.domain.CryptoSponsorMethod
import com.maksimowiczm.foodyou.business.sponsorship.domain.LinkSponsorMethod
import com.maksimowiczm.foodyou.shared.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.shared.ui.ext.add
import com.maksimowiczm.foodyou.shared.ui.utils.LocalClipboardManager
import foodyou.app.generated.resources.*
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun SponsorScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    val clipboardManager = LocalClipboardManager.current
    val appConfig = koinInject<AppConfig>()

    SponsorScreen(
        onBack = onBack,
        onOpenUrl = { uriHandler.openUri(it) },
        onCopy = { clipboardManager.copy("address", it) },
        onContact = { uriHandler.openUri(appConfig.contactEmailUri) },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SponsorScreen(
    onBack: () -> Unit,
    onOpenUrl: (String) -> Unit,
    onCopy: (String) -> Unit,
    onContact: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_sponsor)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier.fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = paddingValues.add(vertical = 8.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CompositionLocalProvider(
                        LocalTextStyle provides MaterialTheme.typography.bodyMedium
                    ) {
                        Text(stringResource(Res.string.description_sponsor_1))
                        Text(stringResource(Res.string.description_sponsor_2))
                        Text(stringResource(Res.string.description_sponsor_3))
                    }
                }
            }

            item {
                Column {
                    Text(
                        text = stringResource(Res.string.sponsor_bank_or_card).uppercase(),
                        modifier = Modifier.padding(start = 16.dp),
                        style = MaterialTheme.typography.labelLarge,
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        AvailableSponsorMethod.fiat.forEach {
                            it.SponsorCard(onClick = { onOpenUrl(it.url) })
                        }
                    }
                }
            }

            items(items = AvailableSponsorMethod.crypto) {
                it.SponsorCard(onClick = { onCopy(it.address) })
            }

            item { ContactCard(onContact = onContact) }
        }
    }
}

@Composable
private fun LinkSponsorMethod.SponsorCard(onClick: () -> Unit, modifier: Modifier = Modifier) {
    SponsorCard(
        label = name,
        leadingIcon = { Icon(Modifier.width(24.dp)) },
        trailingIcon = { Icon(imageVector = Icons.Outlined.Link, contentDescription = null) },
        onClick = onClick,
        modifier = modifier,
        color =
            if (primary) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainer
            },
        contentColor =
            if (primary) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurface
            },
    )
}

@Composable
private fun CryptoSponsorMethod.SponsorCard(onClick: () -> Unit, modifier: Modifier = Modifier) {
    var clicked by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(clicked) {
        if (clicked) {
            delay(2000)
            clicked = false
        }
    }

    Column(modifier) {
        Text(
            text = name.uppercase(),
            modifier = Modifier.padding(start = 16.dp),
            style = MaterialTheme.typography.labelLarge,
        )
        SponsorCard(
            label = address,
            leadingIcon = { Icon(Modifier.height(24.dp)) },
            trailingIcon = {
                if (clicked) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                } else {
                    Icon(imageVector = Icons.Outlined.ContentCopy, contentDescription = null)
                }
            },
            onClick = {
                clicked = true
                onClick()
            },
            color =
                if (primary) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceContainer
                },
            contentColor =
                if (primary) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SponsorCard(
    label: String,
    leadingIcon: @Composable () -> Unit,
    trailingIcon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().heightIn(min = 56.dp),
        shape = MaterialTheme.shapes.medium,
        color = color,
        contentColor = contentColor,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leadingIcon()
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMediumEmphasized,
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
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(imageVector = Icons.Outlined.Hail, contentDescription = null)
                Text(
                    text = stringResource(Res.string.headline_contact),
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            Text(
                text = stringResource(Res.string.description_sponsor_contact),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
