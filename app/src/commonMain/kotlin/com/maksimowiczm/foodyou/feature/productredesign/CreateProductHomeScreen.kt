package com.maksimowiczm.foodyou.feature.productredesign

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreateProductHomeScreen(
    onCreateOpenFoodFacts: () -> Unit,
    onCreateProduct: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.align(Alignment.BottomCenter).width(IntrinsicSize.Max),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedButton(
                onClick = onCreateOpenFoodFacts,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Open Food Facts product",
                    overflow = TextOverflow.Visible,
                    maxLines = 1
                )
            }
            Button(
                onClick = onCreateProduct,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Your product",
                    overflow = TextOverflow.Visible,
                    maxLines = 1
                )
            }
        }
    }
}
