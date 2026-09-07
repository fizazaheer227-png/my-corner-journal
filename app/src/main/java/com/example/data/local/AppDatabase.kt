package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.FutureCapsuleDao
import com.example.data.local.dao.HabitDao
import com.example.data.local.dao.JournalDao
import com.example.data.local.dao.LetterDao
import com.example.data.local.dao.LifeCapsuleDao
import com.example.data.local.dao.MemoryDao
import com.example.data.local.dao.MoodDao
import com.example.data.local.dao.PeriodDao
import com.example.data.local.dao.PersonDao
import com.example.data.local.dao.StickyNoteDao
import com.example.data.local.dao.UserSettingsDao
import com.example.data.local.dao.VisionBoardDao
import com.example.data.local.dao.WishlistDao
import com.example.data.local.entity.FutureCapsuleEntity
import com.example.data.local.entity.HabitEntity
import com.example.data.local.entity.JournalEntryEntity
import com.example.data.local.entity.LetterEntity
import com.example.data.local.entity.LifeCapsuleEntity
import com.example.data.local.entity.MemoryEntity
import com.example.data.local.entity.MoodLogEntity
import com.example.data.local.entity.PeriodLogEntity
import com.example.data.local.entity.PersonEntity
import com.example.data.local.entity.StickyNoteEntity
import com.example.data.local.entity.UserSettingsEntity
import com.example.data.local.entity.VisionBoardItemEntity
import com.example.data.local.entity.WishlistItemEntity

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        UserSettingsEntity::class,
        JournalEntryEntity::class,
        MoodLogEntity::class,
        PersonEntity::class,
        MemoryEntity::class,
        StickyNoteEntity::class,
        FutureCapsuleEntity::class,
        WishlistItemEntity::class,
        VisionBoardItemEntity::class,
        HabitEntity::class,
        PeriodLogEntity::class,
        LetterEntity::class,
        LifeCapsuleEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userSettingsDao(): UserSettingsDao
    abstract fun journalDao(): JournalDao
    abstract fun moodDao(): MoodDao
    abstract fun personDao(): PersonDao
    abstract fun memoryDao(): MemoryDao
    abstract fun stickyNoteDao(): StickyNoteDao
    abstract fun futureCapsuleDao(): FutureCapsuleDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun visionBoardDao(): VisionBoardDao
    abstract fun habitDao(): HabitDao
    abstract fun periodDao(): PeriodDao
    abstract fun letterDao(): LetterDao
    abstract fun lifeCapsuleDao(): LifeCapsuleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE journal_entries ADD COLUMN videoUri TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE memories ADD COLUMN videoUri TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE memories ADD COLUMN voiceNoteUri TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "my_corner_journal.db"
                ).addMigrations(MIGRATION_1_2).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
