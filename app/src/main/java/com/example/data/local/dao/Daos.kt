package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSettingsDao {
    @Query("SELECT * FROM user_settings WHERE id = 1")
    fun getUserSettings(): Flow<UserSettingsEntity?>

    @Query("SELECT * FROM user_settings WHERE id = 1")
    suspend fun getUserSettingsDirect(): UserSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: UserSettingsEntity)
}

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries ORDER BY dateMillis DESC")
    fun getAllEntries(): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): JournalEntryEntity?

    @Query("SELECT * FROM journal_entries WHERE isFavorite = 1 ORDER BY dateMillis DESC")
    fun getFavoriteEntries(): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' ORDER BY dateMillis DESC")
    fun searchEntries(query: String): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE taggedPersonName = :personName ORDER BY dateMillis DESC")
    fun getEntriesForPerson(personName: String): Flow<List<JournalEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: JournalEntryEntity): Long

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteEntry(id: Long)
}

@Dao
interface MoodDao {
    @Query("SELECT * FROM mood_logs ORDER BY dateString DESC")
    fun getAllMoodLogs(): Flow<List<MoodLogEntity>>

    @Query("SELECT * FROM mood_logs WHERE dateString = :dateString LIMIT 1")
    suspend fun getMoodForDate(dateString: String): MoodLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMood(mood: MoodLogEntity)

    @Query("DELETE FROM mood_logs WHERE id = :id")
    suspend fun deleteMood(id: Long)
}

@Dao
interface PersonDao {
    @Query("SELECT * FROM people ORDER BY createdAt DESC")
    fun getAllPeople(): Flow<List<PersonEntity>>

    @Query("SELECT * FROM people WHERE id = :id")
    suspend fun getPersonById(id: Long): PersonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person: PersonEntity): Long

    @Query("DELETE FROM people WHERE id = :id")
    suspend fun deletePerson(id: Long)
}

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memories ORDER BY dateMillis DESC")
    fun getAllMemories(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE inJar = 1")
    suspend fun getJarMemories(): List<MemoryEntity>

    @Query("SELECT * FROM memories WHERE taggedPersonName = :personName ORDER BY dateMillis DESC")
    fun getMemoriesForPerson(personName: String): Flow<List<MemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity): Long

    @Query("DELETE FROM memories WHERE id = :id")
    suspend fun deleteMemory(id: Long)
}

@Dao
interface StickyNoteDao {
    @Query("SELECT * FROM sticky_notes ORDER BY createdAt ASC")
    fun getAllNotes(): Flow<List<StickyNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: StickyNoteEntity): Long

    @Update
    suspend fun updateNote(note: StickyNoteEntity)

    @Query("DELETE FROM sticky_notes WHERE id = :id")
    suspend fun deleteNote(id: Long)
}

@Dao
interface FutureCapsuleDao {
    @Query("SELECT * FROM future_capsules ORDER BY unlockDateMillis ASC")
    fun getAllCapsules(): Flow<List<FutureCapsuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCapsule(capsule: FutureCapsuleEntity): Long

    @Update
    suspend fun updateCapsule(capsule: FutureCapsuleEntity)

    @Query("DELETE FROM future_capsules WHERE id = :id")
    suspend fun deleteCapsule(id: Long)
}

@Dao
interface WishlistDao {
    @Query("SELECT * FROM wishlist_items ORDER BY createdAt DESC")
    fun getAllItems(): Flow<List<WishlistItemEntity>>

    @Query("SELECT * FROM wishlist_items WHERE board = :board ORDER BY createdAt DESC")
    fun getItemsByBoard(board: String): Flow<List<WishlistItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: WishlistItemEntity): Long

    @Update
    suspend fun updateItem(item: WishlistItemEntity)

    @Query("DELETE FROM wishlist_items WHERE id = :id")
    suspend fun deleteItem(id: Long)
}

@Dao
interface VisionBoardDao {
    @Query("SELECT * FROM vision_board_items WHERE boardCategory = :category")
    fun getItemsByCategory(category: String): Flow<List<VisionBoardItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: VisionBoardItemEntity): Long

    @Query("DELETE FROM vision_board_items WHERE id = :id")
    suspend fun deleteItem(id: Long)

    @Query("DELETE FROM vision_board_items WHERE boardCategory = :category")
    suspend fun clearCategory(category: String)
}

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits ORDER BY createdAt ASC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun deleteHabit(id: Long)
}

@Dao
interface PeriodDao {
    @Query("SELECT * FROM period_logs ORDER BY startDate DESC")
    fun getAllLogs(): Flow<List<PeriodLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: PeriodLogEntity): Long

    @Query("DELETE FROM period_logs WHERE id = :id")
    suspend fun deleteLog(id: Long)
}

@Dao
interface LetterDao {
    @Query("SELECT * FROM letters ORDER BY dateMillis DESC")
    fun getAllLetters(): Flow<List<LetterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLetter(letter: LetterEntity): Long

    @Query("DELETE FROM letters WHERE id = :id")
    suspend fun deleteLetter(id: Long)
}

@Dao
interface LifeCapsuleDao {
    @Query("SELECT * FROM life_capsules ORDER BY createdAt DESC")
    fun getAllCapsules(): Flow<List<LifeCapsuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCapsule(capsule: LifeCapsuleEntity): Long

    @Query("DELETE FROM life_capsules WHERE id = :id")
    suspend fun deleteCapsule(id: Long)
}
