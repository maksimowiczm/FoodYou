package com.maksimowiczm.foodyou.app.business.opensource.domain.search

interface FoodRemoteMediatorFactoryAggregate {
    val openFoodFactsRemoteMediatorFactory: ProductRemoteMediatorFactory
    val usdaRemoteMediatorFactory: ProductRemoteMediatorFactory
}
