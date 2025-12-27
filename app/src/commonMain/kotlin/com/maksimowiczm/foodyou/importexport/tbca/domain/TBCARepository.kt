package com.maksimowiczm.foodyou.importexport.tbca.domain

import com.maksimowiczm.foodyou.food.domain.entity.RemoteProduct

/**
 * Repository for accessing TBCA (Brazilian Food Composition Table) data.
 *
 * TBCA is maintained by the University of São Paulo (USP) and contains
 * nutritional information for over 5,600 Brazilian foods.
 */
interface TBCARepository {
    /**
     * Reads and parses all foods from the TBCA JSON file.
     *
     * @return List of RemoteProducts parsed from TBCA data
     * @throws Exception if file cannot be read or parsed
     */
    suspend fun readAllFoods(): List<RemoteProduct>
}
