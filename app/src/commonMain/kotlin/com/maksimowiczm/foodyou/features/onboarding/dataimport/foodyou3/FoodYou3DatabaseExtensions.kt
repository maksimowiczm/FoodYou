package com.maksimowiczm.foodyou.features.onboarding.dataimport.foodyou3

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteStatement
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.maksimowiczm.foodyou.common.Err
import com.maksimowiczm.foodyou.common.Ok
import com.maksimowiczm.foodyou.common.Result
import com.maksimowiczm.foodyou.common.domain.food.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.kilocalories
import com.maksimowiczm.foodyou.common.domain.micrograms
import com.maksimowiczm.foodyou.common.domain.milligrams
import com.maksimowiczm.foodyou.common.map
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.write
import kotlin.time.Clock

internal sealed interface OpenFoodYou3DatabaseFilePickerError {
    data class ReadFileFailed(val cause: Throwable? = null) : OpenFoodYou3DatabaseFilePickerError

    data class InvalidSqliteDatabase(val cause: Throwable? = null) :
        OpenFoodYou3DatabaseFilePickerError

    data class InvalidFoodYou3Database(
        val actualHash: String? = null,
        val cause: Throwable? = null,
    ) : OpenFoodYou3DatabaseFilePickerError
}

internal suspend fun FileKit.openFoodYou3DatabaseFilePicker():
    Result<PlatformFile?, OpenFoodYou3DatabaseFilePickerError> {
    val file =
        try {
            openFilePicker(type = FileKitType.File(".sqlite", ".db", ".sqlite3")) ?: return Ok(null)
        } catch (e: Throwable) {
            return Err(OpenFoodYou3DatabaseFilePickerError.ReadFileFailed(cause = e))
        }

    val tempFileName = "foodyou3_viewer_${Clock.System.now().toEpochMilliseconds()}.db"
    val tempFile = FileKit.filesDir / tempFileName

    try {
        tempFile.write(file.readBytes())
    } catch (e: Throwable) {
        return Err(OpenFoodYou3DatabaseFilePickerError.ReadFileFailed(cause = e))
    }

    return try {
        tempFile.verifyFoodYou3IdentityHash().map { file }
    } finally {
        tempFile.delete(mustExist = false)
    }
}

private const val FOODYOU3_IDENTITY_HASH = "ca27fee35bbdfeaaa21e6d7770b95444"

internal fun PlatformFile.verifyFoodYou3IdentityHash():
    Result<Unit, OpenFoodYou3DatabaseFilePickerError> {
    val connection =
        try {
            BundledSQLiteDriver().open(absolutePath())
        } catch (e: Throwable) {
            return Err(OpenFoodYou3DatabaseFilePickerError.InvalidSqliteDatabase(cause = e))
        }

    return try {
        connection.use { conn ->
            conn.prepare("SELECT identity_hash FROM room_master_table WHERE id = 42").use {
                statement ->
                if (!statement.step()) {
                    return Err(
                        OpenFoodYou3DatabaseFilePickerError.InvalidFoodYou3Database(
                            actualHash = null,
                            cause = null,
                        )
                    )
                }
                val hash = statement.getText(0)
                if (hash == FOODYOU3_IDENTITY_HASH) {
                    Ok()
                } else {
                    Err(
                        OpenFoodYou3DatabaseFilePickerError.InvalidFoodYou3Database(
                            actualHash = hash,
                            cause = null,
                        )
                    )
                }
            }
        }
    } catch (e: Throwable) {
        Err(
            OpenFoodYou3DatabaseFilePickerError.InvalidFoodYou3Database(
                actualHash = null,
                cause = e,
            )
        )
    }
}

internal class F3DatabaseRow(private val stmt: SQLiteStatement) {
    private val columns: Map<String, Int> =
        (0 until stmt.getColumnCount()).associateBy { stmt.getColumnName(it) }

    private fun index(name: String) = columns.getValue(name)

    private inline fun <T> nullable(name: String, read: (Int) -> T): T? =
        columns[name]?.takeUnless { stmt.isNull(it) }?.let(read)

    fun long(name: String): Long = stmt.getLong(index(name))

    fun int(name: String): Int = long(name).toInt()

    fun double(name: String): Double = stmt.getDouble(index(name))

    fun text(name: String): String = stmt.getText(index(name))

    fun boolean(name: String): Boolean = stmt.getBoolean(index(name))

    fun longOrNull(name: String): Long? = nullable(name) { stmt.getLong(it) }

    fun doubleOrNull(name: String): Double? = nullable(name) { stmt.getDouble(it) }

    fun textOrNull(name: String): String? = nullable(name) { stmt.getText(it) }

    fun nonBlankText(name: String): String? = textOrNull(name)?.takeIf { it.isNotBlank() }

    private fun gramsOf(name: String) = doubleOrNull(name)?.grams.toNutrientValue()

    private fun milligramsOf(name: String) = doubleOrNull(name)?.milligrams.toNutrientValue()

    private fun microgramsOf(name: String) = doubleOrNull(name)?.micrograms.toNutrientValue()

    private fun kcalOf(name: String) = doubleOrNull(name)?.kilocalories.toNutrientValue()

    fun nutritionFacts(): NutritionFacts =
        NutritionFacts.requireAll(
            proteins = gramsOf("proteins"),
            carbohydrates = gramsOf("carbohydrates"),
            energy = kcalOf("energy"),
            fats = gramsOf("fats"),
            saturatedFats = gramsOf("saturatedFats"),
            transFats = gramsOf("transFats"),
            monounsaturatedFats = gramsOf("monounsaturatedFats"),
            polyunsaturatedFats = gramsOf("polyunsaturatedFats"),
            omega3 = gramsOf("omega3"),
            omega6 = gramsOf("omega6"),
            sugars = gramsOf("sugars"),
            addedSugars = gramsOf("addedSugars"),
            dietaryFiber = gramsOf("dietaryFiber"),
            solubleFiber = gramsOf("solubleFiber"),
            insolubleFiber = gramsOf("insolubleFiber"),
            salt = gramsOf("salt"),
            cholesterol = milligramsOf("cholesterolMilli"),
            caffeine = milligramsOf("caffeineMilli"),
            vitaminA = microgramsOf("vitaminAMicro"),
            vitaminB1 = milligramsOf("vitaminB1Milli"),
            vitaminB2 = milligramsOf("vitaminB2Milli"),
            vitaminB3 = milligramsOf("vitaminB3Milli"),
            vitaminB5 = milligramsOf("vitaminB5Milli"),
            vitaminB6 = milligramsOf("vitaminB6Milli"),
            vitaminB7 = microgramsOf("vitaminB7Micro"),
            vitaminB9 = microgramsOf("vitaminB9Micro"),
            vitaminB12 = microgramsOf("vitaminB12Micro"),
            vitaminC = milligramsOf("vitaminCMilli"),
            vitaminD = microgramsOf("vitaminDMicro"),
            vitaminE = milligramsOf("vitaminEMilli"),
            vitaminK = microgramsOf("vitaminKMicro"),
            manganese = milligramsOf("manganeseMilli"),
            magnesium = milligramsOf("magnesiumMilli"),
            potassium = milligramsOf("potassiumMilli"),
            calcium = milligramsOf("calciumMilli"),
            copper = milligramsOf("copperMilli"),
            zinc = milligramsOf("zincMilli"),
            sodium = milligramsOf("sodiumMilli"),
            iron = milligramsOf("ironMilli"),
            phosphorus = milligramsOf("phosphorusMilli"),
            selenium = microgramsOf("seleniumMicro"),
            iodine = microgramsOf("iodineMicro"),
            chromium = microgramsOf("chromiumMicro"),
        )
}

/** Lazily runs [sql] and maps every row. The statement is closed when the sequence completes. */
internal fun <T> SQLiteConnection.query(sql: String, map: F3DatabaseRow.() -> T): Sequence<T> =
    sequence {
        prepare(sql).use { stmt ->
            val row = F3DatabaseRow(stmt)
            while (stmt.step()) yield(row.map())
        }
    }

internal fun SQLiteConnection.count(sql: String): Int =
    prepare(sql).use { if (it.step()) it.getLong(0).toInt() else 0 }
