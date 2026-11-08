package com.example.cardwallet.data

import kotlinx.coroutines.flow.Flow

class PassRepository(private val dao: PassDao) {

    fun observeAll(): Flow<List<PassEntity>> = dao.observeAll()

    fun observeById(id: Long): Flow<PassEntity?> = dao.observeById(id)

    suspend fun insert(pass: PassEntity): Long = dao.insert(pass)

    suspend fun delete(pass: PassEntity) = dao.delete(pass)

    suspend fun getAll(): List<PassEntity> = dao.getAll()

    suspend fun deleteAll() = dao.deleteAll()
}