package com.maksimowiczm.foodyou.swissfoodcomposition.infrastructure.room

import androidx.paging.PagingSource
import androidx.room3.*
import androidx.room3.paging.PagingSourceDaoReturnTypeConverter
import com.maksimowiczm.foodyou.common.infrastructure.room.SIMPLE_NAME_SELECT
import kotlinx.coroutines.flow.Flow

@Dao
@DaoReturnTypeConverters(PagingSourceDaoReturnTypeConverter::class)
abstract class SwissFoodCompositionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: SwissFoodCompositionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(entities: List<SwissFoodCompositionEntity>)

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT s.*, $SIMPLE_NAME_SELECT as simpleName
        FROM SwissFoodComposition s
        ORDER BY simpleName
        """
    )
    abstract fun getPagingSource(
        languageCode: String
    ): PagingSource<Int, SwissFoodCompositionEntity>

    @Query("SELECT COUNT(*) FROM SwissFoodComposition") abstract fun observeCount(): Flow<Int>

    @Query("SELECT s.* FROM SwissFoodComposition s WHERE id = :id")
    abstract fun observe(id: Long): Flow<SwissFoodCompositionEntity?>

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT s.*, $SIMPLE_NAME_SELECT as simpleName
        FROM SwissFoodComposition s
        JOIN SwissFoodCompositionFts fts ON s.rowid = fts.rowid
        WHERE SwissFoodCompositionFts MATCH :query || '*'
        ORDER BY simpleName
        """
    )
    abstract fun getPagingSourceByQuery(
        query: String,
        languageCode: String,
    ): PagingSource<Int, SwissFoodCompositionEntity>

    @Query(
        """
        SELECT COUNT(*)
        FROM SwissFoodComposition s
        JOIN SwissFoodCompositionFts fts ON s.rowid = fts.rowid
        WHERE SwissFoodCompositionFts MATCH :query || '*'
        """
    )
    abstract fun observeCountByQuery(query: String): Flow<Int>
}
