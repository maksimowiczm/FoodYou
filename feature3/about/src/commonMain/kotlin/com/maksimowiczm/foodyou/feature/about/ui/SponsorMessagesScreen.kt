package com.maksimowiczm.foodyou.feature.about.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.maksimowiczm.foodyou.core.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.utils.LocalDateFormatter
import com.maksimowiczm.foodyou.feature.about.data.database.Sponsorship
import com.maksimowiczm.foodyou.feature.about.data.database.SponsorshipMethod
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.defaultShimmerTheme
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import foodyou.app.generated.resources.*
import kotlinx.coroutines.FlowPreview
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SponsorMessagesScreen(
    onBack: () -> Unit,
    onSponsor: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SponsorMessagesViewModel = koinViewModel()
) {
    val pages = viewModel.sponsorshipPages.collectAsLazyPagingItems()

    SponsorMessagesScreen(
        onBack = onBack,
        onSponsor = onSponsor,
        pages = pages,
        modifier = modifier
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    FlowPreview::class
)
@Composable
private fun SponsorMessagesScreen(
    onBack: () -> Unit,
    onSponsor: () -> Unit,
    pages: LazyPagingItems<Sponsorship>,
    modifier: Modifier = Modifier
) {
    val dateFormatter = LocalDateFormatter.current
    val shimmer = rememberShimmer(
        shimmerBounds = ShimmerBounds.Window,
        theme = defaultShimmerTheme.copy(
            shaderColors = listOf(
                Color.White.copy(alpha = 0.6f),
                Color.White.copy(alpha = 1.00f),
                Color.White.copy(alpha = 0.6f)
            ),
            shimmerWidth = 500.dp
        )
    )

    val apiStatus = pages.apiStatus

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.headline_sponsor)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                actions = {
                    // Let's not dos the API with bored users, so we only show the refresh button when there is an error
                    if (apiStatus == ApiStatus.Error) {
                        IconButton(
                            onClick = {
                                if (apiStatus == ApiStatus.Error) {
                                    pages.retry()
                                } else {
                                    pages.refresh()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Refresh,
                                contentDescription = stringResource(Res.string.action_try_again)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = paddingValues.calculateTopPadding()
                )
                .zIndex(10f),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = apiStatus,
                transitionSpec = {
                    fadeIn() + slideInVertically { -it } togetherWith
                        fadeOut() + slideOutVertically { -it }
                }
            ) {
                when (it) {
                    ApiStatus.Loading -> LoadingIndicator(
                        modifier = Modifier.padding(top = 8.dp),
                        polygons = listOf(
                            MaterialShapes.Flower,
                            MaterialShapes.SoftBurst,
                            MaterialShapes.Sunny,
                            MaterialShapes.Gem
                        )
                    )

                    ApiStatus.Error -> Box(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }

                    ApiStatus.Success -> Unit
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            reverseLayout = true,
            contentPadding = paddingValues
        ) {
            item {
                BottomMessages(
                    onSponsor = onSponsor
                )
                Spacer(Modifier.height(16.dp))
            }

            items(
                count = pages.itemCount,
                key = pages.itemKey { it.id }
            ) { i ->
                val sponsorship = pages[i]
                val nextSponsorship = if (i < pages.itemCount - 1) pages.peek(i + 1) else null
                val previousSponsorship = if (i > 0) pages.peek(i - 1) else null

                if (sponsorship != null) {
                    val showDateHeader =
                        nextSponsorship == null ||
                            sponsorship.sponsorshipDate().date !=
                            nextSponsorship.sponsorshipDate().date

                    val previousSameDate =
                        previousSponsorship?.sponsorshipDate()?.date ==
                            sponsorship.sponsorshipDate().date

                    val iconResource = when (sponsorship.method) {
                        SponsorshipMethod.Kofi -> Res.drawable.kofi_logo
                        SponsorshipMethod.Liberapay -> Res.drawable.liberapay_logo
                        SponsorshipMethod.Crypto if (sponsorship.currency == "BTC") ->
                            Res.drawable.bitcoin_logo

                        SponsorshipMethod.Crypto if (sponsorship.currency == "XMR") ->
                            Res.drawable.monero_logo

                        else -> null
                    }

                    val icon = if (iconResource != null) {
                        @Composable {
                            Image(
                                painter = painterResource(iconResource),
                                contentDescription = null,
                                modifier = Modifier.sizeIn(
                                    maxWidth = ChatBubbleDefaults.iconSize,
                                    maxHeight = ChatBubbleDefaults.iconSize
                                )
                            )
                        }
                    } else {
                        null
                    }

                    Sent {
                        ChatBubble(
                            icon = icon,
                            author = sponsorship.sponsorName ?: "Annonymous",
                            authorExtra = if (sponsorship.currency == "EUR") {
                                "€${sponsorship.amount}"
                            } else {
                                "${sponsorship.amount} ${sponsorship.currency} (€${sponsorship.inEuro})"
                            },
                            message = sponsorship.message,
                            shape = RoundedCornerShape(
                                topStart = if (showDateHeader) 16.dp else 4.dp,
                                topEnd = if (showDateHeader) 16.dp else 4.dp,
                                bottomStart = if (previousSameDate) 4.dp else 16.dp,
                                bottomEnd = 4.dp
                            ),
                            containerColor = ChatBubbleDefaults.sentContainerColor,
                            contentColor = ChatBubbleDefaults.sentContentColor,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (!showDateHeader) {
                        Spacer(Modifier.height(2.dp))
                    } else {
                        Spacer(Modifier.height(4.dp))
                        ChatGroupHeader(
                            dateFormatter.formatDate(sponsorship.sponsorshipDate().date)
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                } else {
                    Sent {
                        ChatBubbleSkeleton(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = 16.dp,
                                bottomEnd = 4.dp
                            ),
                            containerColor = ChatBubbleDefaults.sentContainerColor,
                            contentColor = ChatBubbleDefaults.sentContentColor,
                            modifier = Modifier
                                .shimmer(shimmer)
                                .fillMaxWidth()
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BottomMessages(onSponsor: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
    ) {
        ChatGroupHeader(stringResource(Res.string.headline_now))

        Spacer(Modifier.height(4.dp))

        Received {
            ChatBubble(
                icon = null,
                author = null,
                authorExtra = null,
                message = "Thank you for your support!",
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = 4.dp,
                    bottomEnd = 16.dp
                ),
                containerColor = ChatBubbleDefaults.receivedContainerColor,
                contentColor = ChatBubbleDefaults.receivedContentColor
            )
        }

        Spacer(Modifier.height(8.dp))

        Sent {
            Surface(
                onClick = onSponsor,
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 4.dp
                ),
                color = ChatBubbleDefaults.sentContainerColor,
                contentColor = ChatBubbleDefaults.sentContentColor
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.VolunteerActivism,
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(Res.string.headline_sponsor),
                        style = MaterialTheme.typography.bodyMediumEmphasized
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatGroupHeader(text: String) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun Sent(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 32.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        content()
    }
}

@Composable
private fun Received(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 32.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        content()
    }
}

private enum class ApiStatus {
    Loading,
    Error,
    Success
}

private val LazyPagingItems<Sponsorship>.apiStatus
    @Composable get() = remember(this) {
        derivedStateOf {
            when {
                loadState.append is LoadState.Loading ||
                    loadState.refresh is LoadState.Loading ||
                    loadState.prepend is LoadState.Loading -> ApiStatus.Loading

                loadState.append is LoadState.Error ||
                    loadState.refresh is LoadState.Error ||
                    loadState.prepend is LoadState.Error -> ApiStatus.Error

                else -> ApiStatus.Success
            }
        }
    }.value
