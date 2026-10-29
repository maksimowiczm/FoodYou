package com.maksimowiczm.foodyou.swissfoodcomposition.infrastructure.room

import androidx.room3.*
import com.maksimowiczm.foodyou.common.infrastructure.room.FoodNameEntity

@Entity(tableName = "SwissFoodComposition")
data class SwissFoodCompositionEntity(
    @PrimaryKey val id: Long,
    @Embedded(prefix = "name_") val name: FoodNameEntity,
    val density: Double?,
    @Embedded(prefix = "energy_kj_") val energyKj: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "energy_kcal_") val energyKcal: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "fat_total_") val fatTotal: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "fatty_acids_sat_")
    val fattyAcidsSaturated: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "fatty_acids_mono_")
    val fattyAcidsMonounsaturated: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "fatty_acids_poly_")
    val fattyAcidsPolyunsaturated: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "linoleic_acid_") val linoleicAcid: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "alpha_linolenic_")
    val alphaLinolenicAcid: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "epa_") val eicosapentaenoicAcidEpa: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "dha_") val docosahexaenoicAcidDha: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "cholesterol_") val cholesterol: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "carbs_avail_")
    val carbohydratesAvailable: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "sugars_") val sugars: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "starch_") val starch: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "dietary_fibres_") val dietaryFibres: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "protein_") val protein: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "salt_nacl_") val saltNacl: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "alcohol_") val alcohol: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "water_") val water: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "vitamin_a_re_") val vitaminARe: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "vitamin_a_rae_") val vitaminARae: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "retinol_") val retinol: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "beta_carotene_act_")
    val betaCaroteneActivity: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "beta_carotene_") val betaCarotene: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "vit_b1_") val vitaminB1Thiamine: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "vit_b2_") val vitaminB2Riboflavin: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "vit_b6_") val vitaminB6Pyridoxine: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "vit_b12_") val vitaminB12Cobalamin: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "niacin_eq_") val niacinEquivalent: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "niacin_") val niacin: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "folate_") val folate: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "pantothenic_") val pantothenicAcid: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "vit_c_") val vitaminCAscorbicAcid: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "vit_d_") val vitaminDCalciferol: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "vit_e_") val vitaminETocopherol: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "potassium_") val potassium: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "sodium_") val sodium: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "chloride_") val chloride: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "calcium_") val calcium: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "magnesium_") val magnesium: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "phosphorus_") val phosphorus: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "iron_") val iron: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "iodide_") val iodide: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "zinc_") val zinc: SwissFoodNutrientComponentEntity?,
    @Embedded(prefix = "selenium_") val selenium: SwissFoodNutrientComponentEntity?,
)
