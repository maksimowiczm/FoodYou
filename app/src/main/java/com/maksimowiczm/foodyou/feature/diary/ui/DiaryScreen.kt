package com.maksimowiczm.foodyou.feature.diary.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun DiaryScreen(
    modifier: Modifier = Modifier,
    viewModel: DiaryViewModel = koinViewModel()
) {
    val message by viewModel.message.collectAsStateWithLifecycle(null)

    Surface(modifier) {
        if (message != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Message(
                    message = message!!
                )
            }
        }
    }
}

@Composable
fun Message(
    message: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = message,
        style = MaterialTheme.typography.headlineLarge
    )
}
