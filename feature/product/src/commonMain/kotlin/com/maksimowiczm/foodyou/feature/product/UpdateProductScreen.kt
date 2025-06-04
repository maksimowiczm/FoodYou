package com.maksimowiczm.foodyou.feature.product

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.maksimowiczm.foodyou.feature.product.ui.update.UpdateProductEvent
import com.maksimowiczm.foodyou.feature.product.ui.update.UpdateProductScreen
import com.maksimowiczm.foodyou.feature.product.ui.update.UpdateProductScreenViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun UpdateProductScreen(
    productId: Long,
    onBack: () -> Unit,
    onUpdate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<UpdateProductScreenViewModel>(
        parameters = { parametersOf(productId) }
    )

    val lifecycleOwner = LocalLifecycleOwner.current
    val latestOnUpdate by rememberUpdatedState(onUpdate)

    LaunchedEffect(lifecycleOwner, viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.events.collectLatest { event ->
                when (event) {
                    UpdateProductEvent.Updated -> latestOnUpdate()
                }
            }
        }
    }

    UpdateProductScreen(
        product = viewModel.product!!,
        onBack = onBack,
        onUpdate = viewModel::onUpdate,
        modifier = modifier
    )
}
