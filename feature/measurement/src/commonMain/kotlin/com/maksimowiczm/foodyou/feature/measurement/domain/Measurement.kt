package com.maksimowiczm.foodyou.feature.measurement.domain

import com.maksimowiczm.foodyou.feature.measurement.data.Measurement as MeasurementType
import kotlin.jvm.JvmInline
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

@Serializable(with = MeasurementSerializer::class)
sealed interface Measurement {

    @JvmInline
    value class Gram(val value: Float) : Measurement

    @JvmInline
    value class Milliliter(val value: Float) : Measurement

    @JvmInline
    value class Package(val quantity: Float) : Measurement {

        /**
         * Calculates the weight of the package based on the given package weight.
         */
        fun weight(packageWeight: Float) = packageWeight * quantity
    }

    @JvmInline
    value class Serving(val quantity: Float) : Measurement {

        /**
         * Calculates the weight of the serving based on the given serving weight.
         */
        fun weight(servingWeight: Float) = servingWeight * quantity
    }

    companion object {

        fun equal(a: Measurement, b: Measurement): Boolean = when {
            a is Gram && b is Gram -> a.value == b.value
            a is Milliliter && b is Milliliter -> a.value == b.value
            a is Package && b is Package -> a.quantity == b.quantity
            a is Serving && b is Serving -> a.quantity == b.quantity
            else -> false
        }

        fun notEqual(a: Measurement, b: Measurement): Boolean = !equal(a, b)
    }
}

val Measurement.rawValue: Float
    get() = when (this) {
        is Measurement.Gram -> value
        is Measurement.Milliliter -> value
        is Measurement.Package -> quantity
        is Measurement.Serving -> quantity
    }

val Measurement.type: MeasurementType
    get() = when (this) {
        is Measurement.Gram -> MeasurementType.Gram
        is Measurement.Milliliter -> MeasurementType.Milliliter
        is Measurement.Package -> MeasurementType.Package
        is Measurement.Serving -> MeasurementType.Serving
    }

fun Measurement.Companion.from(type: MeasurementType, rawValue: Float): Measurement = when (type) {
    MeasurementType.Gram -> Measurement.Gram(rawValue)
    MeasurementType.Milliliter -> Measurement.Milliliter(rawValue)
    MeasurementType.Package -> Measurement.Package(rawValue)
    MeasurementType.Serving -> Measurement.Serving(rawValue)
}

object MeasurementSerializer : KSerializer<Measurement> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Measurement") {
        element<String>("type")
        element<Float>("value")
    }

    override fun serialize(encoder: Encoder, value: Measurement) {
        encoder.encodeStructure(descriptor) {
            when (value) {
                is Measurement.Gram -> {
                    encodeStringElement(descriptor, 0, "gram")
                    encodeFloatElement(descriptor, 1, value.value)
                }
                is Measurement.Milliliter -> {
                    encodeStringElement(descriptor, 0, "milliliter")
                    encodeFloatElement(descriptor, 1, value.value)
                }
                is Measurement.Package -> {
                    encodeStringElement(descriptor, 0, "package")
                    encodeFloatElement(descriptor, 1, value.quantity)
                }
                is Measurement.Serving -> {
                    encodeStringElement(descriptor, 0, "serving")
                    encodeFloatElement(descriptor, 1, value.quantity)
                }
            }
        }
    }

    override fun deserialize(decoder: Decoder): Measurement = decoder.decodeStructure(descriptor) {
        var type: String? = null
        var value: Float? = null

        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> type = decodeStringElement(descriptor, 0)
                1 -> value = decodeFloatElement(descriptor, 1)
                CompositeDecoder.DECODE_DONE -> break
                else -> error("Unexpected index: $index")
            }
        }

        requireNotNull(type) { "Missing type field" }
        requireNotNull(value) { "Missing value field" }

        when (type) {
            "gram" -> Measurement.Gram(value)
            "milliliter" -> Measurement.Milliliter(value)
            "package" -> Measurement.Package(value)
            "serving" -> Measurement.Serving(value)
            else -> error("Unknown measurement type: $type")
        }
    }
}
