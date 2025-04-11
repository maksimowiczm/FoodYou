package com.maksimowiczm.foodyou.feature.recipe.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun CreateRecipeScreen(
    modifier: Modifier = Modifier
) {
    CreateRecipeScreen(
        arg = Unit,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateRecipeScreen(
    arg: Unit,
    modifier: Modifier = Modifier,
) {
    Surface { Spacer(Modifier.fillMaxSize()) }
}