package com.maksimowiczm.foodyou.feature.productredesign.ui.update

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.feature.productredesign.ui.ProductForm
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun UpdateProductApp(
    productId: Long,
    onBack: () -> Unit,
    onProductUpdate: (productId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UpdateProductViewModel = koinViewModel(
        parameters = { parametersOf(productId) }
    )
) {
    val state by viewModel.formState.collectAsStateWithLifecycle()

    when (val state = state) {
        null -> Unit
        else -> Scaffold(
            modifier = modifier
        ) { paddingValues ->
            ProductForm(
                state = state,
                contentPadding = paddingValues,
                onNameChange = remember(viewModel) { viewModel::onNameChange },
                onBrandChange = remember(viewModel) { viewModel::onBrandChange },
                onBarcodeChange = remember(viewModel) { viewModel::onBarcodeChange },
                onProteinsChange = remember(viewModel) { viewModel::onProteinsChange },
                onCarbohydratesChange = remember(viewModel) { viewModel::onCarbohydratesChange },
                onFatsChange = remember(viewModel) { viewModel::onFatsChange },
                onSugarsChange = remember(viewModel) { viewModel::onSugarsChange },
                onSaturatedFatsChange = remember(viewModel) { viewModel::onSaturatedFatsChange },
                onSaltChange = remember(viewModel) { viewModel::onSaltChange },
                onSodiumChange = remember(viewModel) { viewModel::onSodiumChange },
                onFiberChange = remember(viewModel) { viewModel::onFiberChange },
                onPackageWeightChange = remember(viewModel) { viewModel::onPackageWeightChange },
                onServingWeightChange = remember(viewModel) { viewModel::onServingWeightChange },
                onUseOpenFoodFactsProduct = {}
            )
        }
    }
}
