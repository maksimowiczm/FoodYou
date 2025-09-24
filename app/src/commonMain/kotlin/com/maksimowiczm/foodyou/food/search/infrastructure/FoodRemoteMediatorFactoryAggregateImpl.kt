package com.maksimowiczm.foodyou.food.search.infrastructure

import com.maksimowiczm.foodyou.food.search.domain.FoodRemoteMediatorFactoryAggregate
import com.maksimowiczm.foodyou.food.search.infrastructure.openfoodfacts.OpenFoodFactsRemoteMediatorFactory
import com.maksimowiczm.foodyou.food.search.infrastructure.usda.USDARemoteMediatorFactory

internal class FoodRemoteMediatorFactoryAggregateImpl(
    override val usdaRemoteMediatorFactory: USDARemoteMediatorFactory,
    override val openFoodFactsRemoteMediatorFactory: OpenFoodFactsRemoteMediatorFactory,
) : FoodRemoteMediatorFactoryAggregate
