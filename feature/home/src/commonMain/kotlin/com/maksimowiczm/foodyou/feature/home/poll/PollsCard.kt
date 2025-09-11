package com.maksimowiczm.foodyou.feature.home.poll

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.business.opensource.domain.poll.LinkPoll
import com.maksimowiczm.foodyou.app.business.opensource.domain.poll.Poll
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun PollsCard(modifier: Modifier = Modifier) {
    val viewModel: PollsViewModel = koinViewModel()
    val uriHandler = LocalUriHandler.current

    val polls by viewModel.polls.collectAsStateWithLifecycle()

    PollsCard(
        polls = polls,
        onDismiss = { poll -> viewModel.dismissPoll(poll.id) },
        onOpen = { poll ->
            when (poll) {
                is LinkPoll -> uriHandler.openUri(poll.url)
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun PollsCard(
    polls: List<Poll>,
    onDismiss: (Poll) -> Unit,
    onOpen: (Poll) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (polls.isEmpty()) {
        return
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        polls.forEach { poll ->
            when (poll) {
                is LinkPoll ->
                    LinkPollCard(
                        poll = poll,
                        onDismiss = { onDismiss(poll) },
                        onOpen = { onOpen(poll) },
                    )
            }
        }
    }
}

@Composable
private fun LinkPollCard(
    poll: LinkPoll,
    onDismiss: () -> Unit,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(text = poll.title, style = MaterialTheme.typography.titleLarge)
            Text(text = poll.description, style = MaterialTheme.typography.bodyMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            ) {
                TextButton(onDismiss) { Text(stringResource(Res.string.action_dont_show_again)) }
                Button(onOpen) { Text(stringResource(Res.string.action_open_poll)) }
            }
        }
    }
}
