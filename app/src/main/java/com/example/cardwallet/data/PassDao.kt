package com.example.cardwallet.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PassDao {

    @Query("SELECT * FROM passes ORDER BY createdAt ASC")
    fun observeAll(): Flow<List<PassEntity>>

    @Query("SELECT * FROM passes WHERE id = :id")
    fun observeById(id: Long): Flow<PassEntity?>

    @Insert
    suspend fun insert(pass: PassEntity): Long

    @Delete
    suspend fun delete(pass: PassEntity)

    @Query("SELECT * FROM passes ORDER BY createdAt ASC")
    suspend fun getAll(): List<PassEntity>

    @Query("DELETE FROM passes")
    suspend fun deleteAll()
}