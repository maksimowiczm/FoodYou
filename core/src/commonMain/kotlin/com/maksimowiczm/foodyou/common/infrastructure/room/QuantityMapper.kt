package com.maksimowiczm.foodyou.common.infrastructure.room

import com.maksimowiczm.foodyou.common.domain.VolumeUnit
import com.maksimowiczm.foodyou.common.domain.WeightUnit
import com.maksimowiczm.foodyou.common.domain.fluidOunces
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.micrograms
import com.maksimowiczm.foodyou.common.domain.milligrams
import com.maksimowiczm.foodyou.common.domain.milliliters
import com.maksimowiczm.foodyou.common.domain.ounces

object QuantityMapper {
    fun toQuantityEntity(quantity: AbsoluteQuantity): QuantityEntity =
        when (quantity) {
            is AbsoluteQuantity.Weight ->
                when (quantity.weight.unit) {
                    WeightUnit.Micrograms ->
                        QuantityEntity(
                            QuantityType.Weight,
                            quantity.weight.micrograms,
                            MeasurementUnit.Micrograms,
                        )

                    WeightUnit.Milligrams ->
                        QuantityEntity(
                            QuantityType.Weight,
                            quantity.weight.milligrams,
                            MeasurementUnit.Milligrams,
                        )

                    WeightUnit.Grams ->
                        QuantityEntity(
                            QuantityType.Weight,
                            quantity.weight.grams,
                            MeasurementUnit.Grams,
                        )

                    WeightUnit.Ounces ->
                        QuantityEntity(
                            QuantityType.Weight,
                            quantity.weight.ounces,
                            MeasurementUnit.Ounces,
                        )
                }

            is AbsoluteQuantity.Volume ->
                when (quantity.volume.unit) {
                    VolumeUnit.Milliliters ->
                        QuantityEntity(
                            QuantityType.Volume,
                            quantity.volume.milliliters,
                            MeasurementUnit.Milliliters,
                        )

                    VolumeUnit.FluidOunces ->
                        QuantityEntity(
                            QuantityType.Volume,
                            quantity.volume.fluidOunces,
                            MeasurementUnit.FluidOunces,
                        )
                }
        }

    fun toAbsoluteQuantity(entity: QuantityEntity): AbsoluteQuantity =
        with(entity) {
            when (type) {
                QuantityType.Weight ->
                    AbsoluteQuantity.Weight(
                        weight =
                            when (unit) {
                                MeasurementUnit.Micrograms -> amount.micrograms
                                MeasurementUnit.Grams -> amount.grams
                                MeasurementUnit.Ounces -> amount.ounces
                                else -> error("Unexpected unit $unit for weight")
                            }
                    )

                QuantityType.Volume ->
                    AbsoluteQuantity.Volume(
                        volume =
                            when (unit) {
                                MeasurementUnit.Milliliters -> amount.milliliters
                                MeasurementUnit.FluidOunces -> amount.fluidOunces
                                else -> error("Unexpected unit $unit for volume")
                            }
                    )
            }
        }
}
