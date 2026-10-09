package com.maksimowiczm.foodyou.foodyou3.infrastructure

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.maksimowiczm.foodyou.foodyou3.application.FoodYou3DataScope
import com.maksimowiczm.foodyou.foodyou3.application.FoodYou3DataScopeFactory
import com.maksimowiczm.foodyou.foodyou3.infrastructure.food.FoodYou3SqliteFoodSearchRepository
import com.maksimowiczm.foodyou.foodyou3.infrastructure.food.SqliteFoodYou3FoodSearchDao
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.write
import kotlin.time.Clock

internal class SqliteFoodYou3DataScopeFactory : FoodYou3DataScopeFactory {
    override suspend fun create(file: PlatformFile): FoodYou3DataScope {
        val tempFileName = "foodyou3_viewer_${Clock.System.now().toEpochMilliseconds()}.db"
        val tempFile = FileKit.filesDir / tempFileName
        tempFile.write(file.readBytes())

        try {
            val connection = BundledSQLiteDriver().open(tempFile.absolutePath())

            return object : FoodYou3DataScope {
                override val foodSearchRepository =
                    FoodYou3SqliteFoodSearchRepository(SqliteFoodYou3FoodSearchDao(connection))

                override suspend fun close() =
                    try {
                        connection.close()
                    } finally {
                        tempFile.delete(mustExist = false)
                    }
            }
        } catch (e: Exception) {
            tempFile.delete(mustExist = false)
            throw e
        }
    }
}
