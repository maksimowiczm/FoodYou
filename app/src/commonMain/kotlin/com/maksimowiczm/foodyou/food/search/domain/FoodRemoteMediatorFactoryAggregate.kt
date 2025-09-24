package com.maksimowiczm.foodyou.food.search.domain

interface FoodRemoteMediatorFactoryAggregate {
    val openFoodFactsRemoteMediatorFactory: ProductRemoteMediatorFactory
    val usdaRemoteMediatorFactory: ProductRemoteMediatorFactory
}
