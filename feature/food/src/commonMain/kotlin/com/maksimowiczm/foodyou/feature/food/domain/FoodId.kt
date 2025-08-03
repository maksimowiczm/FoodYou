package com.maksimowiczm.foodyou.feature.food.domain

import kotlin.jvm.JvmInline
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

sealed interface FoodId {

    @Serializable(with = ProductIdSerializer::class)
    @JvmInline
    value class Product(val id: Long) : FoodId

    @Serializable(with = RecipeIdSerializer::class)
    @JvmInline
    value class Recipe(val id: Long) : FoodId
}

abstract class FoodIdAsLongSerializer<T : FoodId> : KSerializer<T> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        serialName = "FoodId",
        kind = PrimitiveKind.LONG
    )

    abstract fun deserialize(long: Long): T

    override fun serialize(encoder: Encoder, value: T) {
        when (value) {
            is FoodId.Product -> encoder.encodeLong(value.id)
            is FoodId.Recipe -> encoder.encodeLong(value.id)
        }
    }

    override fun deserialize(decoder: Decoder): T = deserialize(decoder.decodeLong())
}

object ProductIdSerializer : FoodIdAsLongSerializer<FoodId.Product>() {
    override fun deserialize(long: Long): FoodId.Product = FoodId.Product(long)
}

object RecipeIdSerializer : FoodIdAsLongSerializer<FoodId.Recipe>() {
    override fun deserialize(long: Long): FoodId.Recipe = FoodId.Recipe(long)
}
