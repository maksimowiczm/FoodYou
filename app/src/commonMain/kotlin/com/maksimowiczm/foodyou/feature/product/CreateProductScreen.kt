package com.maksimowiczm.foodyou.feature.product

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.maksimowiczm.foodyou.feature.product.ui.create.CreateProductApp
import com.maksimowiczm.foodyou.feature.product.ui.create.CreateProductEvent
import com.maksimowiczm.foodyou.feature.product.ui.create.CreateProductScreenViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreateProductScreen(
    onBack: () -> Unit,
    onCreate: (productId: Long) -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null
) {
    CreateProductScreenImpl(
        onBack = onBack,
        onCreate = onCreate,
        modifier = modifier,
        text = text
    )
}

@Composable
private fun CreateProductScreenImpl(
    onBack: () -> Unit,
    onCreate: (productId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateProductScreenViewModel = koinViewModel(),
    text: String? = null
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val latestOnCreate by rememberUpdatedState(onCreate)

    LaunchedEffect(lifecycleOwner, viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.events.collectLatest { event ->
                when (event) {
                    is CreateProductEvent.Created -> latestOnCreate(event.id)
                }
            }
        }
    }

    CreateProductApp(
        onBack = onBack,
        onCreate = viewModel::onCreate,
        modifier = modifier,
        text = text
    )
}
