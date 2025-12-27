package com.maksimowiczm.foodyou.food.search.infrastructure

import com.maksimowiczm.foodyou.food.search.domain.FoodRemoteMediatorFactoryAggregate
import com.maksimowiczm.foodyou.food.search.infrastructure.openfoodfacts.OpenFoodFactsRemoteMediatorFactory

internal class FoodRemoteMediatorFactoryAggregateImpl(
    override val openFoodFactsRemoteMediatorFactory: OpenFoodFactsRemoteMediatorFactory,
) : FoodRemoteMediatorFactoryAggregate
