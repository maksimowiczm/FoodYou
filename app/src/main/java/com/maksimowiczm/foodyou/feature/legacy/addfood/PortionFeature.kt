package com.maksimowiczm.foodyou.feature.legacy.addfood

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.maksimowiczm.foodyou.data.AddFoodRepository
import com.maksimowiczm.foodyou.feature.legacy.addfood.ui.portion.CreatePortionViewModel
import com.maksimowiczm.foodyou.feature.legacy.addfood.ui.portion.PortionScreen
import com.maksimowiczm.foodyou.feature.legacy.addfood.ui.portion.UpdatePortionViewModel
import com.maksimowiczm.foodyou.feature.legacy.product.ProductFeature
import com.maksimowiczm.foodyou.navigation.crossfadeComposable
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module

class PortionFeature(
    addFoodRepository: Module.() -> KoinDefinition<AddFoodRepository>,
    private val productFeature: ProductFeature
) {

    data class PortionScreenProps(
        val onBack: () -> Unit,
        val onSuccess: () -> Unit,
        val onProductEdit: (productId: Long) -> Unit,
        val onProductDelete: () -> Unit
    )

    data class GraphProps(val create: PortionScreenProps, val edit: PortionScreenProps)

    fun NavGraphBuilder.graph(navController: NavController, props: GraphProps) {
        val (create, edit) = props

        crossfadeComposable<Create> {
            val (onBack, onSuccess, onProductEdit, onCreateProductDelete) = create

            PortionScreen(
                onBack = onBack,
                onSuccess = onSuccess,
                onEditClick = onProductEdit,
                onDelete = { onCreateProductDelete() },
                viewModel = koinViewModel<CreatePortionViewModel>()
            )
        }

        crossfadeComposable<Edit> {
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
