package com.maksimowiczm.foodyou.feature.addfood

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.NavigationFeature
import com.maksimowiczm.foodyou.feature.addfood.data.AddFoodRepository
import com.maksimowiczm.foodyou.feature.addfood.ui.portion.PortionScreen
import com.maksimowiczm.foodyou.feature.addfood.ui.portion.PortionViewModel
import com.maksimowiczm.foodyou.feature.product.ProductFeature
import com.maksimowiczm.foodyou.feature.setup
import com.maksimowiczm.foodyou.navigation.forwardBackwardComposable
import kotlinx.serialization.Serializable
import org.koin.core.KoinApplication
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

class PortionFeature(
    addFoodRepository: Module.() -> KoinDefinition<AddFoodRepository>,
    private val productFeature: ProductFeature
) : Feature.Koin,
    NavigationFeature<PortionFeature.GraphProps> {
    private val module = module {
        viewModelOf(::PortionViewModel)

        addFoodRepository().bind()
    }

    override fun KoinApplication.setup() {
        modules(module)

        setup(productFeature)
    }

    data class GraphProps(
        val onBack: () -> Unit,
        val onSuccess: () -> Unit,
        val onProductEdit: (productId: Long) -> Unit,
        val onProductDelete: () -> Unit
    )

    override fun NavGraphBuilder.graph(navController: NavController, props: GraphProps) {
        val (onBack, onSuccess, onProductEdit, onProductDelete) = props

        forwardBackwardComposable<Route> {
            PortionScreen(
                onBack = onBack,
                onSuccess = onSuccess,
                onEditClick = onProductEdit,
                onDeleteClick = {
                    onProductDelete()
                }
            )
        }
    }

    @Serializable
    data class Route(val epochDay: Int, val meal: Long, val productId: Long)

    companion object {
        fun NavController.navigateToPortion(route: Route, navOptions: NavOptions? = null) {
            navigate(
                route = route,
                navOptions = navOptions
            )
        }

        fun NavController.popPortion(inclusive: Boolean = true, saveState: Boolean = false) {
            popBackStack<Route>(
                inclusive = inclusive,
                saveState = saveState
            )
        }
    }
}
