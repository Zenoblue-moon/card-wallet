package com.example.cardwallet.data

import kotlinx.coroutines.flow.Flow

class CardRepository(private val dao: CardDao) {

    fun observeAll(): Flow<List<CardEntity>> = dao.observeAll()

    fun observeById(id: Long): Flow<CardEntity?> = dao.observeById(id)

    suspend fun getById(id: Long): CardEntity? = dao.getById(id)

    suspend fun insert(card: CardEntity): Long = dao.insert(card)

    suspend fun update(card: CardEntity) = dao.update(card)

    suspend fun delete(card: CardEntity) = dao.delete(card)

    suspend fun nextSortOrder(): Int = dao.nextSortOrder()

    suspend fun reorder(orderedIds: List<Long>) = dao.reorder(orderedIds)

    suspend fun getAll(): List<CardEntity> = dao.getAll()

    suspend fun deleteAll() = dao.deleteAll()
}