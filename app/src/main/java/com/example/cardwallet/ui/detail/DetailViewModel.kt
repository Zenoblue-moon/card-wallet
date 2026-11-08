package com.example.cardwallet.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardwallet.CardWalletApplication
import com.example.cardwallet.data.CardEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as CardWalletApplication).repository

    private val _card = MutableStateFlow<CardEntity?>(null)
    val card: StateFlow<CardEntity?> = _card

    private var loadJob: Job? = null

    fun load(cardId: Long) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            repository.observeById(cardId).collect { _card.value = it }
        }
    }

    fun delete(onDeleted: () -> Unit) {
        val current = _card.value ?: return
        viewModelScope.launch {
            repository.delete(current)
            onDeleted()
        }
    }
}