package com.maksimowiczm.foodyou.app.ui.sponsor

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Hail
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.MoneyOff
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.business.shared.domain.config.AppConfig
import com.maksimowiczm.foodyou.app.ui.shared.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.shared.common.extension.now
import com.maksimowiczm.foodyou.shared.compose.component.StatusBarProtection
import com.maksimowiczm.foodyou.shared.compose.component.StatusBarProtectionDefaults
import com.maksimowiczm.foodyou.shared.compose.extension.add
import com.maksimowiczm.foodyou.shared.compose.utility.LocalClipboardManager
import com.maksimowiczm.foodyou.shared.compose.utility.LocalDateFormatter
import com.maksimowiczm.foodyou.shared.compose.utility.formatClipZeros
import com.maksimowiczm.foodyou.sponsorship.domain.entity.AvailableSponsorMethod
import com.maksimowiczm.foodyou.sponsorship.domain.entity.CryptoSponsorMethod
import com.maksimowiczm.foodyou.sponsorship.domain.entity.LinkSponsorMethod
import foodyou.app.generated.resources.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.yearMonth
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SponsorScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<SponsorViewModel>()

    val privacyAccepted by viewModel.privacyAccepted.collectAsStateWithLifecycle()

    if (privacyAccepted) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        SponsorScreen(
            uiState = uiState,
            onBack = onBack,
            onOrderChange = viewModel::changeMessagesOrder,
            onNextMonth = viewModel::nextMonth,
            onPreviousMonth = viewModel::previousMonth,
            modifier = modifier,
        )
    } else {
        SponsorPrivacyScreen(
            onBack = onBack,
            onAllow = { viewModel.setPrivacyAccepted(true) },
            modifier = modifier,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SponsorPrivacyScreen(
    onBack: () -> Unit,
    onAllow: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = {}, navigationIcon = { ArrowBackIconButton(onBack) }) },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .consumeWindowInsets(paddingValues)
                    .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PrivacyTip,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                    )
                    Text(
                        text = stringResource(Res.string.description_sponsors_privacy),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Button(
                        onClick = onAllow,
                        shapes = ButtonDefaults.shapesFor(ButtonDefaults.LargeContainerHeight),
                        modifier = Modifier.height(ButtonDefaults.LargeContainerHeight),
                        contentPadding =
                            ButtonDefaults.contentPaddingFor(ButtonDefaults.LargeContainerHeight),
                    ) {
                        Text(
                            text = stringResource(Res.string.action_allow),
                            style = ButtonDefaults.textStyleFor(ButtonDefaults.LargeContainerHeight),
                        )
                    }
                }
            }

            item { Methods() }

            item { ContactCard() }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun SponsorScreen(
    uiState: SponsorScreenUiState,
    onBack: () -> Unit,
    onOrderChange: (MessagesOrder) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val scrolledOffset = rememberSaveable { mutableFloatStateOf(0f) }
    val nestedScrollConnection =
        StatusBarProtectionDefaults.scrollConnection { scrolledOffset.value = it.y }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopBar(
                onBack = onBack,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth,
                yearMonth = uiState.yearMonth,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().nestedScroll(nestedScrollConnection),
            state = lazyListState,
            contentPadding = paddingValues.add(bottom = 8.dp),
        ) {
            item {
                if (uiState.isLoading) {
                    LinearWavyProgressIndicator(Modifier.fillMaxWidth())
                } else {
                    Spacer(Modifier.height(10.dp))
                }
            }

            item { Spacer(Modifier.height(24.dp)) }

            item {
                ThisMonth(
                    uiState = uiState,
                    onProgressBarClick = {
                        coroutineScope.launch { lazyListState.animateScrollToItem(6) }
                    },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            item { Spacer(Modifier.height(24.dp)) }

            item { Methods(modifier = Modifier.padding(horizontal = 16.dp)) }

            item { Spacer(Modifier.height(24.dp)) }

            item {
                Goals(
                    goals = uiState.goals,
                    remainingForNextGoal = uiState.remainingForNextGoal?.formatClipZeros("%.2f"),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            item { Spacer(Modifier.height(24.dp)) }

            sponsorsList(
                messages = uiState.messages,
                order = uiState.messagesOrder,
                onOrderChange = onOrderChange,
                contentPadding = PaddingValues(horizontal = 16.dp),
            )

            item { Spacer(Modifier.height(24.dp)) }

            item { ContactCard(modifier = Modifier.padding(horizontal = 16.dp)) }
        }
    }

    val density = LocalDensity.current
    StatusBarProtection(
        progress = {
            if (lazyListState.canScrollBackward)
                lerp(0f, 1f, scrolledOffset.value / with(density) { 56.dp.toPx() })
            else 0f
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    onBack: () -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    yearMonth: YearMonth,
) {
    val startYearMonth = remember { YearMonth(2025, 6) }
    val currentYearMonth = remember { LocalDate.now().yearMonth }

    TopAppBar(
        title = {},
        navigationIcon = {
            FilledIconButton(
                onClick = onBack,
                shapes = IconButtonDefaults.shapes(),
                colors =
                    IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(Res.string.action_go_back),
                )
            }
        },
        actions = {
            ButtonGroup(
                overflowIndicator = {},
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                customItem(
                    buttonGroupContent = {
                        FilledIconButton(
                            onClick = onPreviousMonth,
                            shapes = IconButtonDefaults.shapes(),
                            enabled = yearMonth > startYearMonth,
                            colors =
                                IconButtonDefaults.filledIconButtonColors(
                                    containerColor =
                                        MaterialTheme.colorScheme.surfaceContainerHighest,
                                    contentColor = MaterialTheme.colorScheme.onSurface,
                                ),
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                                contentDescription = null,
                            )
                        }
                    },
                    menuContent = {},
                )
                customItem(
                    buttonGroupContent = {
                        FilledIconButton(
                            onClick = onNextMonth,
                            shapes = IconButtonDefaults.shapes(),
                            enabled = yearMonth < currentYearMonth,
                            colors =
                                IconButtonDefaults.filledIconButtonColors(
                                    containerColor =
                                        MaterialTheme.colorScheme.surfaceContainerHighest,
                                    contentColor = MaterialTheme.colorScheme.onSurface,
                                ),
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                                contentDescription = null,
                            )
                        }
                    },
                    menuContent = {},
                )
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent,
            ),
    )
}

@Composable
private fun Methods(modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    val clipboardManager = LocalClipboardManager.current

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(text = "Methods", style = sponsorTypography.title)

        Column {
            Text(
                text = stringResource(Res.string.sponsor_bank_or_card).uppercase(),
                modifier = Modifier.padding(start = 16.dp),
                style = MaterialTheme.typography.labelLarge,
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AvailableSponsorMethod.fiat.forEach {
                    it.SponsorCard(onClick = { uriHandler.openUri(it.url) })
                }
            }
        }

        AvailableSponsorMethod.crypto.forEach {
            it.SponsorCard(onClick = { clipboardManager.copy("address", it.address) })
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
                style = MaterialTheme.typography.bodyMedium,
            )
            trailingIcon()
        }
    }
}

@Composable
private fun ContactCard(modifier: Modifier = Modifier) {
    val appConfig = koinInject<AppConfig>()
    val uriHandler = LocalUriHandler.current

    Card(
        onClick = { uriHandler.openUri(appConfig.contactEmailUri) },
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

@Composable
private fun ThisMonth(
    uiState: SponsorScreenUiState,
    onProgressBarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dateFormatter = LocalDateFormatter.current

    val title = dateFormatter.formatMonthYear(uiState.yearMonth)

    Column(modifier) {
        Text(text = title, style = sponsorTypography.title)
        Spacer(Modifier.height(8.dp))
        Text(
            text =
                stringResource(
                    Res.string.headline_this_month_we_raised,
                    "€${uiState.amount.formatClipZeros("%.2f")}",
                ),
            style = MaterialTheme.typography.titleMedium,
        )
        ProgressBar(
            uiState = uiState,
            modifier =
                Modifier.fillMaxWidth()
                    .height(32.dp)
                    .clickable(
                        indication = null,
                        interactionSource = null,
                        onClick = onProgressBarClick,
                    ),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.description_sponsorship_goals),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ProgressBar(uiState: SponsorScreenUiState, modifier: Modifier = Modifier) {
    if (uiState.goals.size != 3) {
        return
    }

    val colorScheme = MaterialTheme.colorScheme

    val amount by animateFloatAsState(targetValue = uiState.amount.toFloat())
    val goalAmount = uiState.goals.last().amount.toDouble()

    Canvas(modifier.clip(MaterialTheme.shapes.medium)) {
        val spacer = 2.dp.toPx()

        val firstPart = ((uiState.goals[0].amount / goalAmount) * size.width).toFloat()
        val secondPart = ((uiState.goals[1].amount / goalAmount) * size.width).toFloat()
        val thirdPart = ((uiState.goals[2].amount / goalAmount) * size.width).toFloat()
        val progress = ((amount / goalAmount) * size.width).toFloat()

        drawRoundRect(
            color = colorScheme.surfaceContainerHighest,
            size = Size(width = firstPart, height = size.height),
            cornerRadius = CornerRadius(4.dp.toPx()),
        )
        drawRoundRect(
            color = colorScheme.surfaceContainerHighest,
            topLeft = Offset(x = firstPart + spacer, y = 0f),
            size = Size(width = secondPart - firstPart - spacer, height = size.height),
            cornerRadius = CornerRadius(4.dp.toPx()),
        )
        drawRoundRect(
            color = colorScheme.surfaceContainerHighest,
            topLeft = Offset(x = secondPart + spacer, y = 0f),
            size = Size(width = thirdPart - secondPart - 2 * spacer, height = size.height),
            cornerRadius = CornerRadius(4.dp.toPx()),
        )
        clipRect(right = progress.coerceAtMost(size.width)) {
            drawRoundRect(
                color = colorScheme.tertiary,
                size = Size(width = firstPart, height = size.height),
                cornerRadius = CornerRadius(4.dp.toPx()),
            )
            drawRoundRect(
                color = colorScheme.secondary,
                topLeft = Offset(x = firstPart + spacer, y = 0f),
                size = Size(width = secondPart - firstPart - spacer, height = size.height),
                cornerRadius = CornerRadius(4.dp.toPx()),
            )
            drawRoundRect(
                color = colorScheme.primary,
                topLeft = Offset(x = secondPart + spacer, y = 0f),
                size = Size(width = thirdPart - secondPart - 2 * spacer, height = size.height),
                cornerRadius = CornerRadius(4.dp.toPx()),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Goals(
    goals: List<GoalUiModel>,
    remainingForNextGoal: String?,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            text = stringResource(Res.string.headline_sponsorship_goals),
            style = sponsorTypography.title,
        )
        Spacer(Modifier.height(8.dp))

        for (i in goals.indices) {
            val goal = goals[i]
            val fulfilled = goal.fulfilled
            val previousGoal = goals.getOrNull(i - 1)
            val previousFulfilled = previousGoal?.fulfilled
            val nextGoal = goals.getOrNull(i + 1)
            val nextFulfilled = nextGoal?.fulfilled

            val color =
                when (i) {
                    0 -> MaterialTheme.colorScheme.tertiary
                    1 -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.primary
                }

            val shape =
                when (i) {
                    0 -> MaterialShapes.Cookie6Sided
                    1 -> MaterialShapes.Cookie12Sided
                    else -> MaterialShapes.VerySunny
                }.toShape()

            val containerShape =
                when {
                    // First item
                    i == 0 && fulfilled && nextFulfilled == false ->
                        RoundedCornerShape(16.dp, 16.dp, 16.dp, 16.dp)

                    i == 0 && previousFulfilled == true && !fulfilled ->
                        RoundedCornerShape(16.dp, 16.dp, 4.dp, 4.dp)

                    i == 0 -> RoundedCornerShape(16.dp, 16.dp, 4.dp, 4.dp)

                    // Last item
                    i == goals.size - 1 && fulfilled && nextFulfilled == false ->
                        RoundedCornerShape(4.dp, 4.dp, 16.dp, 16.dp)

                    i == goals.size - 1 && previousFulfilled == true && !fulfilled ->
                        RoundedCornerShape(16.dp, 16.dp, 16.dp, 16.dp)

                    i == goals.size - 1 -> RoundedCornerShape(4.dp, 4.dp, 16.dp, 16.dp)

                    // Middle items
                    fulfilled && nextFulfilled == false ->
                        RoundedCornerShape(4.dp, 4.dp, 16.dp, 16.dp)

                    previousFulfilled == true && !fulfilled ->
                        RoundedCornerShape(16.dp, 16.dp, 4.dp, 4.dp)

                    else -> RoundedCornerShape(4.dp)
                }

            Goal(
                goal = goal,
                shape = shape,
                shapeColor = color,
                animate = fulfilled,
                modifier = Modifier.clip(containerShape),
            )
            if (fulfilled && nextFulfilled == false) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text =
                        stringResource(
                            Res.string.description_x_away_from_next_goal,
                            "€$remainingForNextGoal",
                        ),
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(Modifier.height(2.dp))
            } else if (i < goals.size - 1) {
                Spacer(Modifier.height(2.dp))
            }
        }
    }
}

@Composable
private fun Goal(
    goal: GoalUiModel,
    shape: Shape,
    shapeColor: Color,
    animate: Boolean,
    modifier: Modifier = Modifier,
) {
    val color by
        animateColorAsState(
            if (goal.fulfilled) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceContainer
        )
    val contentColor by
        animateColorAsState(
            if (goal.fulfilled) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.onSurface
        )

    val rotation by
        rememberInfiniteTransition()
            .animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec =
                    InfiniteRepeatableSpec(
                        animation = tween(easing = LinearEasing, durationMillis = 2 * 60 * 1000),
                        repeatMode = RepeatMode.Restart,
                    ),
            )

    Surface(modifier = modifier, color = color) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Canvas(
                    Modifier.size(150.dp)
                        .padding(16.dp)
                        .graphicsLayer { rotationZ = if (animate) rotation else 0f }
                        .clip(shape)
                ) {
                    drawRect(shapeColor)
                }
                Text(
                    text = "€${goal.amount}",
                    style = sponsorTypography.goal,
                    color = contentColorFor(shapeColor),
                )
            }
            Column {
                Text(
                    text = goal.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor,
                )
                Text(
                    text = goal.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun LazyListScope.sponsorsList(
    messages: List<SponsorMessageUiModel>,
    order: MessagesOrder,
    onOrderChange: (MessagesOrder) -> Unit,
    contentPadding: PaddingValues,
) {
    item {
        Row(modifier = Modifier.fillMaxWidth().padding(contentPadding)) {
            Text(stringResource(Res.string.headline_sponsors), style = sponsorTypography.title)
            Spacer(Modifier.weight(1f))
            TextButton(
                onClick = { onOrderChange(order.toggle()) },
                modifier = Modifier.height(ButtonDefaults.ExtraSmallContainerHeight),
                contentPadding =
                    ButtonDefaults.contentPaddingFor(ButtonDefaults.ExtraSmallContainerHeight),
            ) {
                Icon(
                    imageVector = Icons.Outlined.SwapVert,
                    contentDescription = stringResource(Res.string.action_reorder),
                    modifier = Modifier.size(ButtonDefaults.ExtraSmallIconSize),
                )
                Spacer(Modifier.width(ButtonDefaults.ExtraSmallIconSpacing))
                Text(
                    text =
                        when (order) {
                            MessagesOrder.NewestFirst -> stringResource(Res.string.headline_latest)
                            MessagesOrder.TopFirst ->
                                stringResource(Res.string.headline_top_sponsorships)
                        },
                    style = ButtonDefaults.textStyleFor(ButtonDefaults.ExtraSmallContainerHeight),
                )
            }
        }
    }
    item { Spacer(Modifier.height(8.dp)) }
    if (messages.isEmpty()) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(contentPadding),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Outlined.MoneyOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(48.dp),
                )
                Text(
                    text = stringResource(Res.string.description_no_sponsors_this_month),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    } else {
        itemsIndexed(items = messages, key = { _, message -> message.id }) { i, message ->
            val shape =
                when (i) {
                    0 -> RoundedCornerShape(16.dp, 16.dp, 4.dp, 4.dp)
                    messages.size - 1 -> RoundedCornerShape(4.dp, 4.dp, 16.dp, 16.dp)
                    else -> RoundedCornerShape(4.dp)
                }

            SponsorMessageCard(
                message = message,
                shape = shape,
                modifier = Modifier.fillMaxWidth().animateItem().padding(contentPadding),
            )

            if (i < messages.size - 1) {
                Spacer(Modifier.height(2.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SponsorMessageCard(
    message: SponsorMessageUiModel,
    shape: Shape,
    modifier: Modifier = Modifier,
) {
    val sponsor = message.sponsor ?: stringResource(Res.string.headline_anonymous)
    val amount =
        if (message.currency == "EUR") {
            "€${message.amount}"
        } else {
            "${message.amount} ${message.currency} (€${message.inEuro})"
        }

    Surface(
        modifier = modifier,
        shape = shape,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                message.icon()?.let { icon ->
                    icon(Modifier.sizeIn(maxWidth = 24.dp, maxHeight = 24.dp))
                }
                Text(text = sponsor, style = MaterialTheme.typography.titleLargeEmphasized)
                Spacer(Modifier.weight(1f))
                val color = LocalContentColor.current
                BasicText(
                    text = amount,
                    maxLines = 1,
                    autoSize =
                        TextAutoSize.StepBased(
                            minFontSize = MaterialTheme.typography.bodySmall.fontSize,
                            maxFontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        ),
                    style = LocalTextStyle.current,
                    color = { color },
                )
            }

            if (!message.message.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(text = message.message, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
