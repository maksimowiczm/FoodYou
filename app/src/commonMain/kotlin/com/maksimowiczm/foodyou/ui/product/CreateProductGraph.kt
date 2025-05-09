package com.maksimowiczm.foodyou.ui.product

import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import kotlinx.serialization.Serializable

@Serializable
data class CreateProduct(val url: String)

fun NavGraphBuilder.createProductGraph() {
    forwardBackwardComposable<CreateProduct>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "foodyou://createproduct?url={url}"
            }
        )
    ) {
        val route = it.toRoute<CreateProduct>()

        Text(
            text = "Create Product: ${route.url}",
            modifier = Modifier.safeContentPadding()
        )
    }
}
