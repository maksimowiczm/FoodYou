package com.maksimowiczm.foodyou.feature.fooddiary.ui.search.openfoodfacts

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.ext.add
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.ui.EnergyProgressIndicator
import com.maksimowiczm.foodyou.feature.food.ui.NutrientList
import com.maksimowiczm.foodyou.feature.fooddiary.domain.domainFacts
import com.maksimowiczm.foodyou.feature.fooddiary.domain.headline
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.data.OpenFoodFactsDatabase
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.data.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import com.maksimowiczm.foodyou.feature.measurement.ui.stringResource
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
internal fun OpenFoodFactsProductScreen(
    onBack: () -> Unit,
    onImport: (localProductId: FoodId.Product) -> Unit,
    productId: Long,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    val database = koinInject<OpenFoodFactsDatabase>()

    val product = database.openFoodFactsDao.observeProductById(productId)
        .collectAsStateWithLifecycle(null).value

    val onOpenFoodFacts = if (!product?.barcode.isNullOrBlank()) {
        { uriHandler.openUri("https://world.openfoodfacts.org/product/${product.barcode}") }
    } else {
        null
    }

    val onImport = {
        // TODO
    }

    if (product == null) {
        // TODO loading state
    } else {
        OpenFoodFactsProductScreen(
            onBack = onBack,
            onImport = onImport,
            onOpenFoodFacts = onOpenFoodFacts,
            product = product,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun OpenFoodFactsProductScreen(
    onBack: () -> Unit,
    onImport: () -> Unit,
    onOpenFoodFacts: (() -> Unit)?,
    product: OpenFoodFactsProduct,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            MediumFlexibleTopAppBar(
                title = { Text(product.headline) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                actions = {
                    IconButton(
                        onClick = onOpenFoodFacts ?: {},
                        enabled = onOpenFoodFacts != null
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.openfoodfacts_logo),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            LargeExtendedFloatingActionButton(
                text = {
                    Text(stringResource(Res.string.action_import))
                },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.FileOpen,
                        contentDescription = null,
                        modifier = Modifier.size(FloatingActionButtonDefaults.LargeIconSize)
                    )
                },
                onClick = onImport,
                expanded = true
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(horizontal = 8.dp),
            contentPadding = paddingValues
                .add(vertical = 8.dp)
                .add(
                    bottom = 96.dp + 32.dp // FAB
                ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Surface(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(
                                Res.string.description_import_open_food_facts_product
                            ),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            item {
                val facts = product.domainFacts ?: return@item
                val measurement = Measurement.Gram(100f)

                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ViewList,
                                contentDescription = null
                            )
                        }

                        EnergyProgressIndicator(
                            proteins = facts.proteins.value,
                            carbohydrates = facts.carbohydrates.value,
                            fats = facts.fats.value,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text(
                        text = stringResource(Res.string.in_x, measurement.stringResource()),
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.labelLarge
                    )

                    NutrientList(
                        facts = facts
                    )
                }
            }
        }
    }
}
