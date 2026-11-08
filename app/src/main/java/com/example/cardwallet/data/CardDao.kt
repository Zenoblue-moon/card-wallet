package com.example.cardwallet.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {

    @Query("SELECT * FROM cards ORDER BY sortOrder ASC")
    fun observeAll(): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE id = :id")
    fun observeById(id: Long): Flow<CardEntity?>

    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun getById(id: Long): CardEntity?

    @Query("SELECT COALESCE(MAX(sortOrder), -1) + 1 FROM cards")
    suspend fun nextSortOrder(): Int

    @Query("SELECT * FROM cards ORDER BY sortOrder ASC")
    suspend fun getAll(): List<CardEntity>

    @Query("DELETE FROM cards")
    suspend fun deleteAll()

    @Insert
    suspend fun insert(card: CardEntity): Long

    @Update
    suspend fun update(card: CardEntity)

    @Query("UPDATE cards SET sortOrder = :sortOrder WHERE id = :id")
    suspend fun updateOrder(id: Long, sortOrder: Int)

    @Delete
    suspend fun delete(card: CardEntity)

    @Transaction
    suspend fun reorder(orderedIds: List<Long>) {
        orderedIds.forEachIndexed { index, id -> updateOrder(id, index) }
    }
}