package com.maksimowiczm.foodyou.feature.addfood

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.NavigationFeature
import com.maksimowiczm.foodyou.feature.addfood.data.AddFoodRepository
import com.maksimowiczm.foodyou.feature.addfood.ui.portion.CreatePortionViewModel
import com.maksimowiczm.foodyou.feature.addfood.ui.portion.PortionScreen
import com.maksimowiczm.foodyou.feature.addfood.ui.portion.UpdatePortionViewModel
import com.maksimowiczm.foodyou.feature.product.ProductFeature
import com.maksimowiczm.foodyou.feature.setup
import com.maksimowiczm.foodyou.navigation.forwardBackwardComposable
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
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
        viewModelOf(::CreatePortionViewModel)
        viewModelOf(::UpdatePortionViewModel)

        addFoodRepository().bind()
    }

    override fun KoinApplication.setup() {
        modules(module)

        setup(productFeature)
    }

    data class PortionScreenProps(
        val onBack: () -> Unit,
        val onSuccess: () -> Unit,
        val onProductEdit: (productId: Long) -> Unit,
        val onProductDelete: () -> Unit
    )

    data class GraphProps(val create: PortionScreenProps, val edit: PortionScreenProps)

    override fun NavGraphBuilder.graph(navController: NavController, props: GraphProps) {
        val (create, edit) = props

        forwardBackwardComposable<Create> {
            val (onBack, onSuccess, onProductEdit, onCreateProductDelete) = create

            PortionScreen(
                onBack = onBack,
                onSuccess = onSuccess,
                onEditClick = onProductEdit,
                onDelete = { onCreateProductDelete() },
                viewModel = koinViewModel<CreatePortionViewModel>()
            )
        }

        forwardBackwardComposable<Edit> {
            val (onBack, onSuccess, onProductEdit, onEditProductDelete) = edit

            PortionScreen(
                onBack = onBack,
                onSuccess = onSuccess,
                onEditClick = onProductEdit,
                onDelete = { onEditProductDelete() },
                viewModel = koinViewModel<UpdatePortionViewModel>()
            )
        }
    }

    @Serializable
    sealed interface Route

    @Serializable
    data class Create(val epochDay: Int, val mealId: Long, val productId: Long) : Route

    @Serializable
    data class Edit(val epochDay: Int, val mealId: Long, val measurementId: Long) : Route

    companion object {
        fun <R : Route> NavController.navigateToPortion(route: R, navOptions: NavOptions? = null) {
            navigate(
                route = route,
                navOptions = navOptions
            )
        }

        inline fun <reified R : Route> NavController.popPortion(
            inclusive: Boolean = true,
            saveState: Boolean = false
        ) {
            popBackStack<R>(
                inclusive = inclusive,
                saveState = saveState
            )
        }
    }
}
