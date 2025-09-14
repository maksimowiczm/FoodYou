package com.maksimowiczm.foodyou.app.infrastructure.opensource.foodsearch

import com.maksimowiczm.foodyou.app.business.opensource.domain.search.FoodRemoteMediatorFactoryAggregate
import com.maksimowiczm.foodyou.app.infrastructure.opensource.openfoodfacts.network.OpenFoodFactsRemoteMediatorFactory
import com.maksimowiczm.foodyou.app.infrastructure.opensource.usda.network.USDARemoteMediatorFactory

internal class FoodRemoteMediatorFactoryAggregateImpl(
    override val usdaRemoteMediatorFactory: USDARemoteMediatorFactory,
    override val openFoodFactsRemoteMediatorFactory: OpenFoodFactsRemoteMediatorFactory,
) : FoodRemoteMediatorFactoryAggregate
