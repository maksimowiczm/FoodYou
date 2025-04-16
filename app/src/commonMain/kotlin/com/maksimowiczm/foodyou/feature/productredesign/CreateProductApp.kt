package com.maksimowiczm.foodyou.feature.productredesign

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.maksimowiczm.foodyou.core.navigation.CrossFadeComposableDefaults
import com.maksimowiczm.foodyou.core.navigation.ForwardBackwardComposableDefaults
import com.maksimowiczm.foodyou.core.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.link_open_food_facts
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun CreateProductApp(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateProductViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    val currentDestination by navController.currentBackStackEntryFlow.collectAsStateWithLifecycle(
        null
    )
    val onBack = {
        val destination = currentDestination?.destination

        when {
            destination == null -> Unit
            destination.hasRoute<CreateProductHome>() == true -> onBack()
            destination.hasRoute<CreateOpenFoodFactsProduct>() == true ->
                navController.popBackStack<CreateOpenFoodFactsProduct>(inclusive = true)

            destination.hasRoute<CreateProductForm>() == true ->
                navController.popBackStack<CreateProductForm>(inclusive = true)
        }
    }
    // TODO Replace it with WebView on android?
    val onOpenFoodFacts = run {
        val uriHandler = LocalUriHandler.current
        val openFoodFactsUrl = stringResource(Res.string.link_open_food_facts)

        remember(uriHandler, openFoodFactsUrl) {
            {
                uriHandler.openUri(openFoodFactsUrl)
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text("Create Product")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        CreateProductNavHost(
            onOpenFoodFacts = onOpenFoodFacts,
            contentPadding = paddingValues,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            navController = navController
        )
    }
}

@Serializable
private data object CreateProductHome

@Serializable
private data object CreateOpenFoodFactsProduct

@Serializable
private data object CreateProductForm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateProductNavHost(
    onOpenFoodFacts: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = CreateProductHome,
        modifier = modifier
    ) {
        crossfadeComposable<CreateProductHome>(
            popEnterTransition = {
                if (initialState.destination.hasRoute<CreateOpenFoodFactsProduct>() ||
                    initialState.destination.hasRoute<CreateProductForm>()
                ) {
                    ForwardBackwardComposableDefaults.popEnterTransition()
                } else {
                    CrossFadeComposableDefaults.enterTransition()
                }
            },
            exitTransition = {
                if (targetState.destination.hasRoute<CreateOpenFoodFactsProduct>() ||
                    targetState.destination.hasRoute<CreateProductForm>()
                ) {
                    ForwardBackwardComposableDefaults.exitTransition()
                } else {
                    CrossFadeComposableDefaults.exitTransition()
                }
            }
        ) {
            CreateProductHomeScreen(
                onCreateOpenFoodFacts = remember(navController) {
                    {
                        navController.navigate(CreateOpenFoodFactsProduct) {
                            launchSingleTop = true
                        }
                    }
                },
                onCreateProduct = remember(navController) {
                    {
                        navController.navigate(CreateProductForm) {
                            launchSingleTop = true
                        }
                    }
                },
                modifier = Modifier
                    .padding(contentPadding)
                    .consumeWindowInsets(contentPadding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            )
        }
        forwardBackwardComposable<CreateOpenFoodFactsProduct> {
            DownloadOpenFoodFactsProduct(
                animatedVisibilityScope = this,
                onSearch = onOpenFoodFacts,
                onDownload = {
                    // TODO
                },
                contentPadding = contentPadding
            )
        }
        forwardBackwardComposable<CreateProductForm> {
            ProductForm(
                contentPadding = contentPadding
            )
        }
    }
}
