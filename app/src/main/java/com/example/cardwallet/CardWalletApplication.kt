package com.example.cardwallet

import android.app.Application
import com.example.cardwallet.data.CardDatabase
import com.example.cardwallet.data.CardRepository
import com.example.cardwallet.data.PassRepository
import com.example.cardwallet.data.SettingsRepository

class CardWalletApplication : Application() {

    val database: CardDatabase by lazy { CardDatabase.getInstance(this) }
    val repository: CardRepository by lazy { CardRepository(database.cardDao()) }
    val passRepository: PassRepository by lazy { PassRepository(database.passDao()) }
    val settingsRepository: SettingsRepository by lazy { SettingsRepository(this) }
}