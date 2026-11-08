package com.example.cardwallet.ui.edit

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardwallet.CardWalletApplication
import com.example.cardwallet.R
import com.example.cardwallet.data.CardEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class EditViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as CardWalletApplication).repository
    private val context = application

    var editingId: Long = -1L
        private set

    val imagePath = mutableStateOf<String?>(null)
    val bankName = mutableStateOf("")
    val cardholderName = mutableStateOf("")
    val cardNumber = mutableStateOf("")
    val expiry = mutableStateOf("")
    val cvv = mutableStateOf("")
    val notes = mutableStateOf("")
    val numberColor = mutableStateOf<Int?>(null)

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var original: CardEntity? = null

    fun load(cardId: Long) {
        editingId = cardId
        if (cardId <= 0) return
        viewModelScope.launch {
            repository.getById(cardId)?.let { card ->
                original = card
                imagePath.value = card.imagePath
                bankName.value = card.bankName
                cardholderName.value = card.cardholderName
                cardNumber.value = card.cardNumber
                expiry.value = card.expiryMonth + card.expiryYear
                cvv.value = card.cvv
                notes.value = card.notes
                numberColor.value = card.numberColor
            }
        }
    }

    fun saveImage(uri: Uri) {
        viewModelScope.launch {
            imagePath.value = copyToInternal(uri)
        }
    }

    fun clearImage() {
        imagePath.value = null
    }

    private fun copyToInternal(uri: Uri): String? {
        return try {
            val dir = File(getApplication<Application>().filesDir, "card_images").apply { mkdirs() }
            val file = File(dir, "card_${System.currentTimeMillis()}.jpg")
            getApplication<Application>().contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output -> input.copyTo(output) }
            }
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    fun clearError() {
        _error.value = null
    }

    suspend fun save(): Boolean {
        val number = cardNumber.value
        val exp = expiry.value

        if (number.length !in 12..19) {
            _error.value = context.getString(R.string.error_card_number_length)
            return false
        }
        if (exp.length != 4) {
            _error.value = context.getString(R.string.error_expiry_incomplete)
            return false
        }
        val month = exp.substring(0, 2).toIntOrNull() ?: 0
        if (month !in 1..12) {
            _error.value = context.getString(R.string.error_expiry_month_invalid)
            return false
        }

        val sortOrder = if (editingId > 0) original?.sortOrder ?: 0 else repository.nextSortOrder()

        val card = CardEntity(
            id = if (editingId > 0) editingId else 0,
            sortOrder = sortOrder,
            imagePath = imagePath.value,
            bankName = bankName.value.trim(),
            cardholderName = cardholderName.value.trim(),
            cardNumber = number,
            expiryMonth = exp.substring(0, 2),
            expiryYear = exp.substring(2, 4),
            cvv = cvv.value,
            notes = notes.value.trim(),
            numberColor = numberColor.value,
            createdAt = original?.createdAt ?: System.currentTimeMillis()
        )

        if (editingId > 0) {
            repository.update(card)
        } else {
            repository.insert(card)
        }
        return true
    }
}