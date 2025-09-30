package com.maksimowiczm.foodyou.app.infrastructure.room.migration

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

object FoodSearchFtsMigration : Migration(30, 31) {
    override fun migrate(connection: SQLiteConnection) {
        // Create ProductFts virtual table
        connection.execSQL(
            """
                CREATE VIRTUAL TABLE IF NOT EXISTS `ProductFts` 
                USING FTS4(
                    `name` TEXT NOT NULL, 
                    `brand` TEXT, 
                    `note` TEXT, 
                    content=`Product`
                )
            """
                .trimIndent()
        )

        // Create sync triggers for ProductFts
        connection.execSQL(
            """
                CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_ProductFts_BEFORE_UPDATE 
                BEFORE UPDATE ON `Product` 
                BEGIN 
                    DELETE FROM `ProductFts` WHERE `docid`=OLD.`rowid`; 
                END
            """
                .trimIndent()
        )

        connection.execSQL(
            """
                CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_ProductFts_BEFORE_DELETE 
                BEFORE DELETE ON `Product` 
                BEGIN 
                    DELETE FROM `ProductFts` WHERE `docid`=OLD.`rowid`; 
                END
            """
                .trimIndent()
        )

        connection.execSQL(
            """
                CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_ProductFts_AFTER_UPDATE 
                AFTER UPDATE ON `Product` 
                BEGIN 
                    INSERT INTO `ProductFts`(`docid`, `name`, `brand`, `note`) 
                    VALUES (NEW.`rowid`, NEW.`name`, NEW.`brand`, NEW.`note`); 
                END
            """
                .trimIndent()
        )

        connection.execSQL(
            """
                CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_ProductFts_AFTER_INSERT 
                AFTER INSERT ON `Product` 
                BEGIN 
                    INSERT INTO `ProductFts`(`docid`, `name`, `brand`, `note`) 
                    VALUES (NEW.`rowid`, NEW.`name`, NEW.`brand`, NEW.`note`); 
                END
            """
                .trimIndent()
        )

        // Create RecipeFts virtual table
        connection.execSQL(
            """
                CREATE VIRTUAL TABLE IF NOT EXISTS `RecipeFts` 
                USING FTS4(
                    `name` TEXT NOT NULL, 
                    `note` TEXT, 
                    content=`Recipe`
                )
            """
                .trimIndent()
        )

        // Create sync triggers for RecipeFts
        connection.execSQL(
            """
                CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_RecipeFts_BEFORE_UPDATE 
                BEFORE UPDATE ON `Recipe` 
                BEGIN 
                    DELETE FROM `RecipeFts` WHERE `docid`=OLD.`rowid`; 
                END
            """
                .trimIndent()
        )

        connection.execSQL(
            """
                CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_RecipeFts_BEFORE_DELETE 
                BEFORE DELETE ON `Recipe` 
                BEGIN 
                    DELETE FROM `RecipeFts` WHERE `docid`=OLD.`rowid`; 
                END
            """
                .trimIndent()
        )

        connection.execSQL(
            """
                CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_RecipeFts_AFTER_UPDATE 
                AFTER UPDATE ON `Recipe` 
                BEGIN 
                    INSERT INTO `RecipeFts`(`docid`, `name`, `note`) 
                    VALUES (NEW.`rowid`, NEW.`name`, NEW.`note`); 
                END
            """
                .trimIndent()
        )

        connection.execSQL(
            """
                CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_RecipeFts_AFTER_INSERT 
                AFTER INSERT ON `Recipe` 
                BEGIN 
                    INSERT INTO `RecipeFts`(`docid`, `name`, `note`) 
                    VALUES (NEW.`rowid`, NEW.`name`, NEW.`note`); 
                END
            """
                .trimIndent()
        )

        // Populate FTS tables with existing data
        connection.execSQL(
            """
                INSERT INTO `ProductFts`(`docid`, `name`, `brand`, `note`)
                SELECT `rowid`, `name`, `brand`, `note` FROM `Product`
            """
                .trimIndent()
        )

        connection.execSQL(
            """
                INSERT INTO `RecipeFts`(`docid`, `name`, `note`)
                SELECT `rowid`, `name`, `note` FROM `Recipe`
            """
                .trimIndent()
        )
    }
}
