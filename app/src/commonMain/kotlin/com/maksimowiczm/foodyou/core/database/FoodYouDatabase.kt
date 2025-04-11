package com.maksimowiczm.foodyou.core.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.maksimowiczm.foodyou.core.database.goals.DiaryDayDao
import com.maksimowiczm.foodyou.core.database.goals.DiaryDayView
import com.maksimowiczm.foodyou.core.database.meal.InitializeMealsCallback
import com.maksimowiczm.foodyou.core.database.meal.MealDao
import com.maksimowiczm.foodyou.core.database.meal.MealEntity
import com.maksimowiczm.foodyou.core.database.measurement.MeasurementDao
import com.maksimowiczm.foodyou.core.database.measurement.MeasurementTypeConverter
import com.maksimowiczm.foodyou.core.database.measurement.ProductMeasurementEntity
import com.maksimowiczm.foodyou.core.database.openfoodfacts.OpenFoodFactsDao
import com.maksimowiczm.foodyou.core.database.openfoodfacts.OpenFoodFactsPagingKeyEntity
import com.maksimowiczm.foodyou.core.database.product.ProductDao
import com.maksimowiczm.foodyou.core.database.product.ProductEntity
import com.maksimowiczm.foodyou.core.database.product.ProductSourceConverter
import com.maksimowiczm.foodyou.core.database.search.SearchDao
import com.maksimowiczm.foodyou.core.database.search.SearchQueryEntity

@Database(
    entities = [
        MealEntity::class,
        ProductEntity::class,
        ProductMeasurementEntity::class,
        OpenFoodFactsPagingKeyEntity::class,
        SearchQueryEntity::class
    ],
    views = [
        DiaryDayView::class
    ],
    version = FoodYouDatabase.VERSION,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 3, to = 4)
    ]
)
@TypeConverters(
    ProductSourceConverter::class,
    MeasurementTypeConverter::class
)
abstract class FoodYouDatabase : RoomDatabase() {
    abstract val mealDao: MealDao
    abstract val measurementDao: MeasurementDao
    abstract val openFoodFactsDao: OpenFoodFactsDao
    abstract val productDao: ProductDao
    abstract val searchDao: SearchDao
    abstract val diaryDayDao: DiaryDayDao

    companion object {
        const val VERSION = 4

        private val migrations: List<Migration> = listOf(
            MIGRATION_1_2,
            MIGRATION_2_3
        )

        fun Builder<FoodYouDatabase>.buildDatabase(
            initializeMealsCallback: InitializeMealsCallback
        ): FoodYouDatabase {
            migrations.forEach(::addMigrations)
            addCallback(initializeMealsCallback)
            return build()
        }
    }
}

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            ALTER TABLE MealEntity 
            ADD COLUMN rank INTEGER NOT NULL DEFAULT -1
            """.trimIndent()
        )
        connection.execSQL(
            """
            UPDATE MealEntity 
            SET rank = id
            """.trimIndent()
        )
    }
}

// API < 30 lack support for ALTER TABLE commands so there is a lot of temp tables
private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SQLiteConnection) {
        // Change OpenFoodFactsPagingKey to OpenFoodFactsPagingKeyEntity
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS OpenFoodFactsPagingKeyEntity(
                queryString TEXT NOT NULL,
                country TEXT NOT NULL,
                fetchedCount INTEGER NOT NULL,
                totalCount INTEGER NOT NULL,
                PRIMARY KEY(queryString, country)
            )
            """.trimIndent()
        )
        database.execSQL(
            """
            INSERT INTO OpenFoodFactsPagingKeyEntity (queryString, country, fetchedCount, totalCount)
            SELECT queryString, country, fetchedCount, totalCount FROM OpenFoodFactsPagingKey
            """.trimIndent()
        )
        database.execSQL("DROP TABLE OpenFoodFactsPagingKey")

        // Create new ProductEntity structure
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS ProductEntity_temp (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                brand TEXT,
                barcode TEXT,
                packageWeight REAL,
                servingWeight REAL,
                productSource INTEGER NOT NULL,
                calories REAL NOT NULL,
                proteins REAL NOT NULL,
                carbohydrates REAL NOT NULL,
                sugars REAL,
                fats REAL NOT NULL,
                saturatedFats REAL,
                salt REAL,
                sodium REAL,
                fiber REAL
            )
            """.trimIndent()
        )

        // Move data to temp table
        database.execSQL(
            """
            INSERT INTO ProductEntity_temp (
                id, name, brand, barcode,
                packageWeight, servingWeight, productSource,
                calories, proteins, carbohydrates, sugars, fats, saturatedFats,
                salt, sodium, fiber
            )
            SELECT 
                id, name, brand, barcode,
                packageWeight, servingWeight, productSource,
                calories, proteins, carbohydrates, sugars, fats, saturatedFats,
                salt, sodium, fiber
            FROM ProductEntity
            """.trimIndent()
        )

        database.execSQL("DROP TABLE ProductEntity")
        database.execSQL("ALTER TABLE ProductEntity_temp RENAME TO ProductEntity")

        // Create ProductMeasurementEntity
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS ProductMeasurementEntity (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                mealId INTEGER NOT NULL,
                diaryEpochDay INTEGER NOT NULL,
                productId INTEGER NOT NULL,
                measurement INTEGER NOT NULL,
                quantity REAL NOT NULL,
                createdAt INTEGER NOT NULL,
                isDeleted INTEGER NOT NULL,
                FOREIGN KEY (productId) REFERENCES ProductEntity(id) ON DELETE CASCADE,
                FOREIGN KEY (mealId) REFERENCES MealEntity(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        // Create proper indices for ProductMeasurementEntity
        database.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ProductMeasurementEntity_productId 
            ON ProductMeasurementEntity (productId)
            """.trimIndent()
        )

        database.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ProductMeasurementEntity_isDeleted 
            ON ProductMeasurementEntity (isDeleted)
            """.trimIndent()
        )

        database.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ProductMeasurementEntity_mealId 
            ON ProductMeasurementEntity (mealId)
            """.trimIndent()
        )

        // Migrate data from WeightMeasurementEntity to ProductMeasurementEntity
        database.execSQL(
            """
            INSERT INTO ProductMeasurementEntity (
                id, mealId, diaryEpochDay, productId, measurement, quantity, createdAt, isDeleted
            )
            SELECT 
                id, mealId, diaryEpochDay, productId, measurement, quantity, createdAt, isDeleted
            FROM WeightMeasurementEntity
            """.trimIndent()
        )

        database.execSQL("DROP TABLE WeightMeasurementEntity")

        // Create SearchQueryEntity from ProductQueryEntity
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS SearchQueryEntity (
                query TEXT NOT NULL PRIMARY KEY,
                epochSeconds INTEGER NOT NULL
            )
            """.trimIndent()
        )

        // Migrate data from ProductQueryEntity to SearchQueryEntity
        database.execSQL(
            """
            INSERT INTO SearchQueryEntity (query, epochSeconds)
            SELECT query, date FROM ProductQueryEntity
            """.trimIndent()
        )

        database.execSQL("DROP TABLE ProductQueryEntity")
    }
}
