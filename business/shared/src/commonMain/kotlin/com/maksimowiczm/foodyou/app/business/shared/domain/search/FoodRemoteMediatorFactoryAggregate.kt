package com.maksimowiczm.foodyou.app.business.shared.domain.search

interface FoodRemoteMediatorFactoryAggregate {
    val openFoodFactsRemoteMediatorFactory: ProductRemoteMediatorFactory
    val usdaRemoteMediatorFactory: ProductRemoteMediatorFactory
}
