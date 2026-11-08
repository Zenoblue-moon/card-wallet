package com.example.cardwallet.ui.pass

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardwallet.CardWalletApplication
import com.example.cardwallet.data.PassEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PassDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as CardWalletApplication).passRepository

    fun observe(id: Long): Flow<PassEntity?> = repository.observeById(id)

    fun delete(pass: PassEntity) {
        viewModelScope.launch { repository.delete(pass) }
    }
}