package com.maksimowiczm.foodyou.common.domain.food

import com.maksimowiczm.foodyou.common.domain.Weight

/**
 * Domain service for updating and transforming [FoodComposition] structures.
 *
 * This service handles the recursive traversal and transformation of the component tree, keeping
 * the domain entities ([FoodComposition], [FoodCompositionComponent]) focused on representing the
 * data.
 */
object FoodCompositionUpdateService {

    /**
     * Updates a leaf component identified by [identity] with new [name] and [nutritionFacts]. The
     * update is propagated through the entire [composition] tree.
     */
    fun update(
        composition: FoodComposition,
        identity: FoodCompositionComponentIdentity.Leaf,
        name: FoodName,
        nutritionFacts: NutritionFacts,
        servingWeight: Weight?,
        packageWeight: Weight?,
        image: FoodCompositionComponentImage?,
    ): FoodComposition =
        transform(composition, identity) { component ->
            check(component is FoodCompositionComponent.Simple) {
                "Expected a Simple component for identity $identity, but found ${component::class}"
            }
            val updatedQuantity = updateQuantity(component.quantity, servingWeight, packageWeight)
            component.copy(
                name = name,
                nutritionFacts = nutritionFacts,
                quantity = updatedQuantity,
                image = image,
            )
        } ?: composition

    /**
     * Updates a composite component identified by [identity] with new [name] and [newComposition].
     * The update is propagated through the entire [composition] tree.
     */
    fun update(
        composition: FoodComposition,
        identity: FoodCompositionComponentIdentity.Composite,
        name: FoodName,
        newComposition: FoodComposition,
        servingWeight: Weight?,
        packageWeight: Weight?,
        image: FoodCompositionComponentImage?,
    ): FoodComposition =
        transform(composition, identity) { component ->
            check(component is FoodCompositionComponent.Composite) {
                "Expected a Composite component for identity $identity, but found ${component::class}"
            }
            val updatedQuantity = updateQuantity(component.quantity, servingWeight, packageWeight)
            component.copy(
                name = name,
                composition = newComposition,
                quantity = updatedQuantity,
                image = image,
            )
        } ?: composition

    /**
     * Removes all components identified by [identity] from the [composition] tree. If a composite
     * component becomes empty after removal, it is also removed. Returns `null` if the entire
     * composition is removed.
     */
    fun remove(
        composition: FoodComposition,
        identity: FoodCompositionComponentIdentity,
    ): FoodComposition? = transform(composition, identity) { null }

    /** Replaces all components identified by [identity] with their anonymous counterparts. */
    fun unlink(
        composition: FoodComposition,
        identity: FoodCompositionComponentIdentity,
    ): FoodComposition = transform(composition, identity) { it.anonymize() } ?: composition

    private fun transform(
        composition: FoodComposition,
        targetId: FoodCompositionComponentIdentity,
        transformer: (FoodCompositionComponent) -> FoodCompositionComponent?,
    ): FoodComposition? {
        val updatedComponents =
            composition.components.mapNotNull { transform(it, targetId, transformer) }
        return when {
            updatedComponents.isEmpty() -> null
            updatedComponents == composition.components -> composition
            else -> composition.copy(components = updatedComponents)
        }
    }

    private fun transform(
        component: FoodCompositionComponent,
        targetId: FoodCompositionComponentIdentity,
        transformer: (FoodCompositionComponent) -> FoodCompositionComponent?,
    ): FoodCompositionComponent? {
        if (component.identity == targetId) return transformer(component)

        return if (component is FoodCompositionComponent.Composite) {
            when (val updatedInner = transform(component.composition, targetId, transformer)) {
                null -> null
                component.composition -> component
                else -> component.copy(composition = updatedInner)
            }
        } else {
            component
        }
    }

    private fun updateQuantity(
        quantity: FoodComponentComponentQuantity,
        servingWeight: Weight?,
        packageWeight: Weight?,
    ): FoodComponentComponentQuantity =
        when (quantity) {
            is FoodComponentComponentQuantity.Serving ->
                if (servingWeight != null) quantity.copy(servingWeight = servingWeight)
                else FoodComponentComponentQuantity.Weight(quantity.absoluteWeight)
            is FoodComponentComponentQuantity.Package ->
                if (packageWeight != null) quantity.copy(packageWeight = packageWeight)
                else FoodComponentComponentQuantity.Weight(quantity.absoluteWeight)
            is FoodComponentComponentQuantity.Weight -> quantity
        }
}
