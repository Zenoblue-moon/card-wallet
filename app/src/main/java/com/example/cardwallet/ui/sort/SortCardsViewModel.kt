package com.example.cardwallet.ui.sort

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardwallet.CardWalletApplication
import com.example.cardwallet.data.CardEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SortCardsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as CardWalletApplication).repository

    private val _cards = MutableStateFlow<List<CardEntity>>(emptyList())
    val cards: StateFlow<List<CardEntity>> = _cards

    init {
        viewModelScope.launch {
            repository.observeAll().collect { _cards.value = it }
        }
    }

    fun move(from: Int, to: Int) {
        val list = _cards.value.toMutableList()
        if (from !in list.indices || to !in list.indices || from == to) return
        list.add(to, list.removeAt(from))
        _cards.value = list
        viewModelScope.launch { repository.reorder(list.map { it.id }) }
    }
}