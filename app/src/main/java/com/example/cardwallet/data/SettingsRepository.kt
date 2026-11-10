package com.example.cardwallet.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val THEME_COLOR = stringPreferencesKey("theme_color")
        val CONTACTLESS_ENABLED = booleanPreferencesKey("contactless_enabled")
        val PAYMENT_AMOUNT = stringPreferencesKey("payment_amount")
        val MERCHANT_NAME = stringPreferencesKey("merchant_name")
        val PAYMENT_CARD_ID = longPreferencesKey("payment_card_id")
        val CURRENCY = stringPreferencesKey("currency")
        val SHOW_LAST4_ON_HOME = booleanPreferencesKey("show_last4_on_home")
        val AVATAR_PATH = stringPreferencesKey("avatar_path")
        val PAYMENT_STATUS = stringPreferencesKey("payment_status")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val TRANSITION = stringPreferencesKey("transition")
        val HAPTIC = booleanPreferencesKey("haptic")
        val TAP_HINT_SHOWN = booleanPreferencesKey("tap_hint_shown")
        val AUDIO_URI = stringPreferencesKey("audio_uri")
        val AUTO_UPDATE_CHECK = booleanPreferencesKey("auto_update_check")
        val PAY_KEY = stringPreferencesKey("pay_key")
        val PREDICTIVE_BACK = booleanPreferencesKey("predictive_back")
    }

    val themeColor: Flow<String> =
        context.dataStore.data.map { it[Keys.THEME_COLOR] ?: "purple" }

    val contactlessEnabled: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.CONTACTLESS_ENABLED] ?: false }

    val paymentAmount: Flow<String> =
        context.dataStore.data.map { it[Keys.PAYMENT_AMOUNT] ?: "0.01" }

    val merchantName: Flow<String> =
        context.dataStore.data.map { it[Keys.MERCHANT_NAME] ?: "" }

    val paymentCardId: Flow<Long> =
        context.dataStore.data.map { it[Keys.PAYMENT_CARD_ID] ?: -1L }

    val currency: Flow<String> =
        context.dataStore.data.map { it[Keys.CURRENCY] ?: "¥" }

    val showLast4OnHome: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.SHOW_LAST4_ON_HOME] ?: false }

    val avatarPath: Flow<String> =
        context.dataStore.data.map { it[Keys.AVATAR_PATH] ?: "" }

    val paymentStatus: Flow<String> =
        context.dataStore.data.map { it[Keys.PAYMENT_STATUS] ?: "success" }

    val dynamicColor: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.DYNAMIC_COLOR] ?: false }

    val transition: Flow<String> =
        context.dataStore.data.map { it[Keys.TRANSITION] ?: "fade" }

    val haptic: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.HAPTIC] ?: true }

    val tapHintShown: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.TAP_HINT_SHOWN] ?: false }

    val audioUri: Flow<String?> =
        context.dataStore.data.map { it[Keys.AUDIO_URI] }

    val autoUpdateCheck: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.AUTO_UPDATE_CHECK] ?: true }

    val payKey: Flow<String> =
        context.dataStore.data.map { it[Keys.PAY_KEY] ?: "volume_down" }

    val predictiveBack: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.PREDICTIVE_BACK] ?: true }

    suspend fun setThemeColor(value: String) {
        context.dataStore.edit { it[Keys.THEME_COLOR] = value }
    }

    suspend fun setContactlessEnabled(value: Boolean) {
        context.dataStore.edit { it[Keys.CONTACTLESS_ENABLED] = value }
    }

    suspend fun setPaymentAmount(value: String) {
        context.dataStore.edit { it[Keys.PAYMENT_AMOUNT] = value }
    }

    suspend fun setMerchantName(value: String) {
        context.dataStore.edit { it[Keys.MERCHANT_NAME] = value }
    }

    suspend fun setPaymentCardId(value: Long) {
        context.dataStore.edit { it[Keys.PAYMENT_CARD_ID] = value }
    }

    suspend fun setCurrency(value: String) {
        context.dataStore.edit { it[Keys.CURRENCY] = value }
    }

    suspend fun setShowLast4OnHome(value: Boolean) {
        context.dataStore.edit { it[Keys.SHOW_LAST4_ON_HOME] = value }
    }

    suspend fun setAvatarPath(value: String) {
        context.dataStore.edit { it[Keys.AVATAR_PATH] = value }
    }

    suspend fun setPaymentStatus(value: String) {
        context.dataStore.edit { it[Keys.PAYMENT_STATUS] = value }
    }

    suspend fun setDynamicColor(value: Boolean) {
        context.dataStore.edit { it[Keys.DYNAMIC_COLOR] = value }
    }

    suspend fun setTransition(value: String) {
        context.dataStore.edit { it[Keys.TRANSITION] = value }
    }

    suspend fun setHaptic(value: Boolean) {
        context.dataStore.edit { it[Keys.HAPTIC] = value }
    }

    suspend fun setTapHintShown(value: Boolean) {
        context.dataStore.edit { it[Keys.TAP_HINT_SHOWN] = value }
    }

    suspend fun setAudioUri(value: String?) {
        context.dataStore.edit {
            if (value == null) it.remove(Keys.AUDIO_URI) else it[Keys.AUDIO_URI] = value
        }
    }

    suspend fun setAutoUpdateCheck(value: Boolean) {
        context.dataStore.edit { it[Keys.AUTO_UPDATE_CHECK] = value }
    }

    suspend fun setPayKey(value: String) {
        context.dataStore.edit { it[Keys.PAY_KEY] = value }
    }

    suspend fun setPredictiveBack(value: Boolean) {
        context.dataStore.edit { it[Keys.PREDICTIVE_BACK] = value }
    }
}