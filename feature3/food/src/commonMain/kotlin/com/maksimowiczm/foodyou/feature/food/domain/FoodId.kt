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

    @Serializable(with = FoodIdProductAsLongSerializer::class)
    @JvmInline
    value class Product(val id: Long) : FoodId

    @JvmInline
    value class Recipe(val id: Long) : FoodId
}

object FoodIdProductAsLongSerializer : KSerializer<FoodId.Product> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        serialName = "FoodId.Product",
        kind = PrimitiveKind.LONG
    )

    override fun serialize(encoder: Encoder, value: FoodId.Product) {
        encoder.encodeLong(value.id)
    }

    override fun deserialize(decoder: Decoder): FoodId.Product {
        val id = decoder.decodeLong()
        return FoodId.Product(id)
    }
}
