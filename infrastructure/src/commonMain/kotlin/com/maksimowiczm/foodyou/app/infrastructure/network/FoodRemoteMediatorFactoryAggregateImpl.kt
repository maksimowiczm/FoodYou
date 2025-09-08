package com.maksimowiczm.foodyou.app.infrastructure.network

import com.maksimowiczm.foodyou.app.infrastructure.network.openfoodfacts.OpenFoodFactsRemoteMediatorFactory
import com.maksimowiczm.foodyou.app.infrastructure.network.usda.USDARemoteMediatorFactory
import com.maksimowiczm.foodyou.business.food.domain.FoodRemoteMediatorFactoryAggregate

internal class FoodRemoteMediatorFactoryAggregateImpl(
    override val usdaRemoteMediatorFactory: USDARemoteMediatorFactory,
    override val openFoodFactsRemoteMediatorFactory: OpenFoodFactsRemoteMediatorFactory,
) : FoodRemoteMediatorFactoryAggregate
