package com.maksimowiczm.foodyou.common.domain.food

import kotlin.jvm.JvmName

/**
 * Domain service for updating and transforming food composition structures.
 *
 * This service handles the recursive traversal and transformation of the component tree, keeping
 * the domain entities ([FoodCompositionComponent]) focused on representing the data.
 */
object FoodCompositionUpdateService {

    /**
     * Updates a leaf component identified by [identity] with new [name] and [nutritionFacts]. The
     * update is propagated through the entire [components] tree.
     */
    fun update(
        components: List<FoodCompositionComponent>,
        identity: FoodCompositionComponentIdentity.Leaf,
        name: FoodName,
        nutritionFacts: NutritionFacts,
        quantity: (current: FoodComponentComponentQuantity) -> FoodComponentComponentQuantity,
        image: FoodCompositionComponentImage?,
    ): List<FoodCompositionComponent> =
        transform(components, identity) { component ->
            component.copy(
                name = name,
                nutritionFacts = nutritionFacts,
                quantity = quantity(component.quantity),
                image = image,
            )
        }

    /**
     * Updates a composite component identified by [identity] with new [name] and [newComponents].
     * The update is propagated through the entire [components] tree.
     */
    fun update(
        components: List<FoodCompositionComponent>,
        identity: FoodCompositionComponentIdentity.Composite,
        name: FoodName,
        newComponents: List<FoodCompositionComponent>,
        quantity: (current: FoodComponentComponentQuantity) -> FoodComponentComponentQuantity,
        image: FoodCompositionComponentImage?,
    ): List<FoodCompositionComponent> =
        transform(components, identity) { component ->
            component.copy(
                name = name,
                components = newComponents,
                quantity = quantity(component.quantity),
                image = image,
            )
        }

    /**
     * Removes all components identified by [identity] from the [components] tree. If a composite
     * component becomes empty after removal, it is also removed. Returns `null` if the entire
     * composition is removed.
     */
    fun remove(
        components: List<FoodCompositionComponent>,
        identity: FoodCompositionComponentIdentity,
    ): List<FoodCompositionComponent> = transformList(components, identity) { null }

    /** Replaces all components identified by [identity] with their anonymous counterparts. */
    fun unlink(
        components: List<FoodCompositionComponent>,
        identity: FoodCompositionComponentIdentity,
    ): List<FoodCompositionComponent> = transformList(components, identity) { it.anonymize() }

    private fun transform(
        components: List<FoodCompositionComponent>,
        targetId: FoodCompositionComponentIdentity.Leaf,
        transformer: (FoodCompositionComponent.Simple) -> FoodCompositionComponent?,
    ): List<FoodCompositionComponent> = transformList(components, targetId, transformer)

    private fun transform(
        components: List<FoodCompositionComponent>,
        targetId: FoodCompositionComponentIdentity.Composite,
        transformer: (FoodCompositionComponent.Composite) -> FoodCompositionComponent?,
    ): List<FoodCompositionComponent> = transformList(components, targetId, transformer)

    private fun transformList(
        components: List<FoodCompositionComponent>,
        targetId: FoodCompositionComponentIdentity,
        transformer: (FoodCompositionComponent) -> FoodCompositionComponent?,
    ): List<FoodCompositionComponent> =
        transformList<FoodCompositionComponent>(components, targetId, transformer)

    @JvmName("transformListGeneric")
    private fun <T : FoodCompositionComponent> transformList(
        components: List<FoodCompositionComponent>,
        targetId: FoodCompositionComponentIdentity,
        transformer: (T) -> FoodCompositionComponent?,
    ): List<FoodCompositionComponent> {
        val updatedComponents = components.mapNotNull { transformSingle(it, targetId, transformer) }
        return when {
            updatedComponents == components -> components
            else -> updatedComponents
        }
    }

    private fun <T : FoodCompositionComponent> transformSingle(
        component: FoodCompositionComponent,
        targetId: FoodCompositionComponentIdentity,
        transformer: (T) -> FoodCompositionComponent?,
    ): FoodCompositionComponent? {
        if (component.identity == targetId) {
            @Suppress("UNCHECKED_CAST")
            return transformer(component as T)
        }

        return if (component is FoodCompositionComponent.Composite) {
            when (val updatedInner = transformList(component.components, targetId, transformer)) {
                component.components -> component
                else -> component.copy(components = updatedInner)
            }
        } else {
            component
        }
    }
}
