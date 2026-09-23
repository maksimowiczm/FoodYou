package com.maksimowiczm.foodyou.shared.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LoadingScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = { ArrowBackIconButton(onBack) },
            )
        },
    ) {
        LoadingScreenContent(Modifier.fillMaxSize().padding(it).consumeWindowInsets(it))
    }
}

@Composable
fun LoadingScreenContent(modifier: Modifier = Modifier) {
    Box(modifier) {
        LoadingIndicator(Modifier.size(160.dp).align(Alignment.Center))
    }
}

@Preview
@Composable
fun LoadingScreenPreview() {
    LoadingScreen(onBack = {})
}
