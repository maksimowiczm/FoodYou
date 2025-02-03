package com.maksimowiczm.foodyou.core.feature.product.ui.create

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.R
import com.maksimowiczm.foodyou.core.ui.theme.FoodYouTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateProductScreen(
    onNavigateBack: () -> Unit,
    onSuccess: (productId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateProductViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreateProductScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onCreate = viewModel::onCreateProduct,
        onSuccess = onSuccess,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateProductScreen(
    uiState: CreateProductState,
    onNavigateBack: () -> Unit,
    onCreate: (ProductFormState) -> Unit,
    onSuccess: (productId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(uiState) {
        when (uiState) {
            is CreateProductState.ProductCreated -> onSuccess(uiState.productId)
            CreateProductState.CreatingProduct,
            CreateProductState.Nothing
            -> Unit
        }
    }

    val formState = rememberProductFormState()

    Surface(modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .displayCutoutPadding()
                .safeDrawingPadding()
        ) {
            TopAppBar(
                title = { Text(stringResource(R.string.headline_create_product)) },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_go_back)
                        )
                    }
                }
            )

            ProductForm(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                formState = formState
            ) {
                CreateButton(
                    isLoading = uiState == CreateProductState.CreatingProduct ||
                        uiState is CreateProductState.ProductCreated,
                    formState = formState,
                    onClick = { onCreate(formState) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun CreateButton(
    isLoading: Boolean,
    formState: ProductFormState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = !isLoading && formState.isValid
    ) {
        if (isLoading) {
            val infiniteTransition = rememberInfiniteTransition("rotation")
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "rotation"
            )
            Icon(
                modifier = Modifier.graphicsLayer { rotationZ = rotation },
                imageVector = Icons.Default.Refresh,
                contentDescription = null
            )
        }

        Text(stringResource(R.string.action_create))
    }
}

@Preview(
    device = "spec:width=400dp,height=1000dp,dpi=240"
)
@Composable
private fun CreateProductScreenPreview() {
    FoodYouTheme {
        CreateProductScreen(
            uiState = CreateProductState.Nothing,
            onNavigateBack = {},
            onCreate = {},
            onSuccess = {}
        )
    }
}
