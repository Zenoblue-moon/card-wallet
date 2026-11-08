package com.example.cardwallet.ui.payment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardwallet.CardWalletApplication
import com.example.cardwallet.data.CardEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class TapToPayViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as CardWalletApplication
    private val settings = app.settingsRepository

    val amount: StateFlow<String> = settings.paymentAmount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "0.01")

    val currency: StateFlow<String> = settings.currency
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "¥")

    val merchant: StateFlow<String> = settings.merchantName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val paymentStatus: StateFlow<String> = settings.paymentStatus
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "success")

    val card: StateFlow<CardEntity?> = combine(
        app.repository.observeAll(),
        settings.paymentCardId
    ) { cards, id ->
        if (id > 0) cards.firstOrNull { it.id == id } else cards.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}