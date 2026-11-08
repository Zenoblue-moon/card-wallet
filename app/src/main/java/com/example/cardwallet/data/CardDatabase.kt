package com.example.cardwallet.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [CardEntity::class, PassEntity::class], version = 5, exportSchema = false)
abstract class CardDatabase : RoomDatabase() {

    abstract fun cardDao(): CardDao
    abstract fun passDao(): PassDao

    companion object {
        @Volatile
        private var INSTANCE: CardDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE cards ADD COLUMN sortOrder INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS passes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "title TEXT NOT NULL, " +
                        "subtitle TEXT NOT NULL, " +
                        "organizationName TEXT NOT NULL, " +
                        "backgroundColor INTEGER NOT NULL, " +
                        "foregroundColor INTEGER NOT NULL, " +
                        "labelColor INTEGER NOT NULL, " +
                        "barcodeMessage TEXT NOT NULL, " +
                        "iconPath TEXT, " +
                        "createdAt INTEGER NOT NULL)"
                )
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE passes ADD COLUMN barcodeFormat TEXT NOT NULL DEFAULT ''")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE cards ADD COLUMN numberColor INTEGER")
            }
        }

        fun getInstance(context: Context): CardDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    CardDatabase::class.java,
                    "card_wallet.db"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5).build().also { INSTANCE = it }
            }
    }
}