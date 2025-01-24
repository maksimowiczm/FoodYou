package com.maksimowiczm.foodyou.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.feature.addfood.navigation.AddFoodRoute
import com.maksimowiczm.foodyou.feature.addfood.navigation.addFoodGraph
import com.maksimowiczm.foodyou.feature.addfood.navigation.navigateToAddFood
import com.maksimowiczm.foodyou.feature.diary.navigation.DiaryFeature
import com.maksimowiczm.foodyou.feature.diary.navigation.diaryGraph
import com.maksimowiczm.foodyou.feature.product.navigation.ProductsRoute
import com.maksimowiczm.foodyou.feature.product.navigation.navigateToProducts
import com.maksimowiczm.foodyou.feature.product.navigation.productsGraph

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FoodYouNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = DiaryFeature
    ) {
        diaryGraph(
            onAddProductToMeal = { meal, date ->
                navController.navigateToAddFood(
                    route = AddFoodRoute.Search(
                        meal = meal,
                        epochDay = date.toEpochDay()
                    ),
                    navOptions = navOptions {
                        launchSingleTop = true
                    }
                )
            }
        )
        addFoodGraph(
            sharedTransitionScope = this@FoodYouNavHost,
            searchOnCloseClick = {
                navController.popBackStack<AddFoodRoute.Search>(inclusive = true)
            },
            searchOnProductClick = { productId, meal, date ->
                navController.navigateToAddFood(
                    route = AddFoodRoute.CreatePortion(
                        productId = productId,
                        meal = meal,
                        epochDay = date.toEpochDay()
                    )
                )
            },
            searchOnCreateProduct = { meal, date ->
                navController.navigateToProducts(
                    ProductsRoute.CreateProduct(
                        epochDay = date.toEpochDay(),
                        mealType = meal
                    )
                )
            },
            createOnSuccess = {
                navController.popBackStack<AddFoodRoute.CreatePortion>(inclusive = true)
            },
            createOnNavigateBack = {
                navController.popBackStack<AddFoodRoute.CreatePortion>(inclusive = true)
            }
        )
        productsGraph(
            createOnNavigateBack = {
                navController.popBackStack()
            },
            createOnSuccess = { productId, epochDay, mealType ->
                navController.navigateToAddFood(
                    route = AddFoodRoute.CreatePortion(
                        productId = productId,
                        meal = mealType,
                        epochDay = epochDay
                    ),
                    navOptions = navOptions {
                        popUpTo<ProductsRoute.CreateProduct> {
                            inclusive = true
                        }
                    }
                )
            }
        )
    }
}
