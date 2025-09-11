package com.maksimowiczm.foodyou.app.infrastructure.network

import com.maksimowiczm.foodyou.app.business.opensource.domain.search.FoodRemoteMediatorFactoryAggregate
import com.maksimowiczm.foodyou.app.infrastructure.network.openfoodfacts.OpenFoodFactsRemoteMediatorFactory
import com.maksimowiczm.foodyou.app.infrastructure.network.usda.USDARemoteMediatorFactory

internal class FoodRemoteMediatorFactoryAggregateImpl(
    override val usdaRemoteMediatorFactory: USDARemoteMediatorFactory,
    override val openFoodFactsRemoteMediatorFactory: OpenFoodFactsRemoteMediatorFactory,
) : FoodRemoteMediatorFactoryAggregate
