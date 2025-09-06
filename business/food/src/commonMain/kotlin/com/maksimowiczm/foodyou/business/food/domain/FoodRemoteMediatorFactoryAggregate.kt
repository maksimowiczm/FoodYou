package com.maksimowiczm.foodyou.business.food.domain

interface FoodRemoteMediatorFactoryAggregate {
    val openFoodFactsRemoteMediatorFactory: ProductRemoteMediatorFactory
    val usdaRemoteMediatorFactory: ProductRemoteMediatorFactory
}
