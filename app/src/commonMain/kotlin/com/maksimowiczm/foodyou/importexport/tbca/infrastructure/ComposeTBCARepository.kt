package com.maksimowiczm.foodyou.importexport.tbca.infrastructure

import com.maksimowiczm.foodyou.app.generated.resources.Res
import com.maksimowiczm.foodyou.food.domain.entity.RemoteProduct
import com.maksimowiczm.foodyou.importexport.tbca.domain.TBCARepository
import com.maksimowiczm.foodyou.importexport.tbca.dto.TBCAFoodDto
import kotlinx.serialization.json.Json

/**
 * Compose Multiplatform implementation of TBCARepository.
 *
 * Reads TBCA data from embedded JSON resource file and converts
 * it to RemoteProduct entities using TBCAMapper.
 */
internal class ComposeTBCARepository(
    private val mapper: TBCAMapper,
    private val json: Json
) : TBCARepository {

    override suspend fun readAllFoods(): List<RemoteProduct> {
        // Read JSON file from resources
        val bytes = Res.readBytes("files/tbca/tbca-pt-BR.json")
        val jsonString = bytes.decodeToString()

        // Parse JSON to DTOs
        // TBCA JSON is an array of food objects, one per line (NDJSON-like format)
        val foods = jsonString
            .trim()
            .lines()
            .filter { it.isNotBlank() }
            .mapNotNull { line ->
                try {
                    json.decodeFromString<TBCAFoodDto>(line)
                } catch (e: Exception) {
                    // Log and skip malformed entries
                    println("Warning: Failed to parse TBCA food: ${e.message}")
                    null
                }
            }

        // Convert DTOs to RemoteProducts
        return foods.mapNotNull { dto ->
            try {
                mapper.toRemoteProduct(dto)
            } catch (e: Exception) {
                // Log and skip foods that fail mapping
                println("Warning: Failed to map TBCA food '${dto.description}': ${e.message}")
                null
            }
        }
    }
}
