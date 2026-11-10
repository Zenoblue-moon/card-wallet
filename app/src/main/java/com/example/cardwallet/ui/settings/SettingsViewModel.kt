package com.example.cardwallet.ui.settings

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardwallet.CardWalletApplication
import com.example.cardwallet.R
import com.example.cardwallet.data.CardEntity
import com.example.cardwallet.data.PassEntity
import com.example.cardwallet.data.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as CardWalletApplication
    private val repository: SettingsRepository = app.settingsRepository

    val themeColor: Flow<String> = repository.themeColor
    val contactlessEnabled: Flow<Boolean> = repository.contactlessEnabled
    val paymentAmount: Flow<String> = repository.paymentAmount
    val merchantName: Flow<String> = repository.merchantName
    val paymentCardId: Flow<Long> = repository.paymentCardId
    val currency: Flow<String> = repository.currency
    val showLast4OnHome: Flow<Boolean> = repository.showLast4OnHome
    val paymentStatus: Flow<String> = repository.paymentStatus
    val dynamicColor: Flow<Boolean> = repository.dynamicColor
    val transition: Flow<String> = repository.transition
    val haptic: Flow<Boolean> = repository.haptic
    val tapHintShown: Flow<Boolean> = repository.tapHintShown
    val audioUri: Flow<String?> = repository.audioUri
    val autoUpdateCheck: Flow<Boolean> = repository.autoUpdateCheck
    val payKey: Flow<String> = repository.payKey
    val predictiveBack: Flow<Boolean> = repository.predictiveBack
    val cards: Flow<List<CardEntity>> = app.repository.observeAll()

    private val _backupMessage = MutableStateFlow<String?>(null)
    val backupMessage: StateFlow<String?> = _backupMessage

    fun setThemeColor(value: String) {
        viewModelScope.launch { repository.setThemeColor(value) }
    }

    fun setContactlessEnabled(value: Boolean) {
        viewModelScope.launch { repository.setContactlessEnabled(value) }
    }

    fun setPaymentAmount(value: String) {
        viewModelScope.launch { repository.setPaymentAmount(value) }
    }

    fun setMerchantName(value: String) {
        viewModelScope.launch { repository.setMerchantName(value) }
    }

    fun setPaymentCardId(value: Long) {
        viewModelScope.launch { repository.setPaymentCardId(value) }
    }

    fun setCurrency(value: String) {
        viewModelScope.launch { repository.setCurrency(value) }
    }

    fun setShowLast4OnHome(value: Boolean) {
        viewModelScope.launch { repository.setShowLast4OnHome(value) }
    }

    fun setPaymentStatus(value: String) {
        viewModelScope.launch { repository.setPaymentStatus(value) }
    }

    fun setDynamicColor(value: Boolean) {
        viewModelScope.launch { repository.setDynamicColor(value) }
    }

    fun setTransition(value: String) {
        viewModelScope.launch { repository.setTransition(value) }
    }

    fun setHaptic(value: Boolean) {
        viewModelScope.launch { repository.setHaptic(value) }
    }

    fun setTapHintShown(value: Boolean) {
        viewModelScope.launch { repository.setTapHintShown(value) }
    }

    fun setAudioUri(value: String?) {
        viewModelScope.launch { repository.setAudioUri(value) }
    }

    fun setAutoUpdateCheck(value: Boolean) {
        viewModelScope.launch { repository.setAutoUpdateCheck(value) }
    }

    fun setPayKey(value: String) {
        viewModelScope.launch { repository.setPayKey(value) }
    }

    fun setPredictiveBack(value: Boolean) {
        viewModelScope.launch { repository.setPredictiveBack(value) }
    }

    fun exportBackup(uri: Uri) {
        viewModelScope.launch {
            val ok = withContext(Dispatchers.IO) {
                try {
                    val context = getApplication<Application>()
                    val cardsArr = JSONArray()
                    val passesArr = JSONArray()

                    val out = context.contentResolver.openOutputStream(uri) ?: return@withContext false
                    ZipOutputStream(out).use { zip ->
                        app.repository.getAll().forEachIndexed { i, c ->
                            val o = JSONObject()
                            o.put("bankName", c.bankName)
                            o.put("cardholderName", c.cardholderName)
                            o.put("cardNumber", c.cardNumber)
                            o.put("expiryMonth", c.expiryMonth)
                            o.put("expiryYear", c.expiryYear)
                            o.put("cvv", c.cvv)
                            o.put("notes", c.notes)
                            o.put("sortOrder", c.sortOrder)
                            o.put("createdAt", c.createdAt)
                            if (c.numberColor != null) o.put("numberColor", c.numberColor) else o.put("numberColor", JSONObject.NULL)
                            fileToBytes(c.imagePath)?.let { bytes ->
                                val name = "images/card_$i${extensionOf(c.imagePath)}"
                                zip.putNextEntry(ZipEntry(name))
                                zip.write(bytes)
                                zip.closeEntry()
                                o.put("imageName", name)
                            }
                            cardsArr.put(o)
                        }

                        app.passRepository.getAll().forEachIndexed { i, p ->
                            val o = JSONObject()
                            o.put("title", p.title)
                            o.put("subtitle", p.subtitle)
                            o.put("organizationName", p.organizationName)
                            o.put("backgroundColor", p.backgroundColor)
                            o.put("foregroundColor", p.foregroundColor)
                            o.put("labelColor", p.labelColor)
                            o.put("barcodeMessage", p.barcodeMessage)
                            o.put("barcodeFormat", p.barcodeFormat)
                            o.put("createdAt", p.createdAt)
                            fileToBytes(p.iconPath)?.let { bytes ->
                                val name = "passes/pass_$i${extensionOf(p.iconPath)}"
                                zip.putNextEntry(ZipEntry(name))
                                zip.write(bytes)
                                zip.closeEntry()
                                o.put("iconName", name)
                            }
                            passesArr.put(o)
                        }

                        val root = JSONObject()
                        root.put("cards", cardsArr)
                        root.put("passes", passesArr)
                        zip.putNextEntry(ZipEntry("data.json"))
                        zip.write(root.toString().toByteArray(Charsets.UTF_8))
                        zip.closeEntry()
                    }
                    true
                } catch (e: Exception) {
                    false
                }
            }
            _backupMessage.value = if (ok) getApplication<Application>().getString(R.string.backup_exported)
            else getApplication<Application>().getString(R.string.backup_export_failed)
        }
    }

    fun importBackup(uri: Uri) {
        viewModelScope.launch {
            val message = withContext(Dispatchers.IO) {
                try {
                    val context = getApplication<Application>()
                    val input = context.contentResolver.openInputStream(uri) ?: return@withContext context.getString(R.string.import_failed)
                    val tmp = File.createTempFile("backup", ".zip", context.cacheDir)
                    tmp.outputStream().use { input.copyTo(it) }

                    val zip = ZipFile(tmp)
                    val entry = zip.getEntry("data.json") ?: run {
                        zip.close()
                        return@withContext context.getString(R.string.import_failed_invalid_backup)
                    }
                    val root = JSONObject(zip.getInputStream(entry).bufferedReader().use { it.readText() })

                    app.repository.deleteAll()
                    app.passRepository.deleteAll()

                    val cardsArr = root.optJSONArray("cards") ?: JSONArray()
                    for (i in 0 until cardsArr.length()) {
                        val o = cardsArr.getJSONObject(i)
                        val numberColor: Int? = when {
                            !o.has("numberColor") -> null
                            o.isNull("numberColor") -> null
                            else -> o.optInt("numberColor")
                        }
                        app.repository.insert(
                            CardEntity(
                                imagePath = zipEntryToFile(zip, o.optString("imageName", ""), "card_images"),
                                bankName = o.optString("bankName"),
                                cardholderName = o.optString("cardholderName"),
                                cardNumber = o.optString("cardNumber"),
                                expiryMonth = o.optString("expiryMonth"),
                                expiryYear = o.optString("expiryYear"),
                                cvv = o.optString("cvv"),
                                notes = o.optString("notes"),
                                numberColor = numberColor,
                                sortOrder = o.optInt("sortOrder"),
                                createdAt = o.optLong("createdAt", System.currentTimeMillis())
                            )
                        )
                    }

                    val passesArr = root.optJSONArray("passes") ?: JSONArray()
                    for (i in 0 until passesArr.length()) {
                        val o = passesArr.getJSONObject(i)
                        app.passRepository.insert(
                            PassEntity(
                                title = o.optString("title"),
                                subtitle = o.optString("subtitle"),
                                organizationName = o.optString("organizationName"),
                                backgroundColor = o.optInt("backgroundColor", 0xFF1B1B1F.toInt()),
                                foregroundColor = o.optInt("foregroundColor", 0xFFFFFFFF.toInt()),
                                labelColor = o.optInt("labelColor", 0xFF999999.toInt()),
                                barcodeMessage = o.optString("barcodeMessage"),
                                barcodeFormat = o.optString("barcodeFormat"),
                                iconPath = zipEntryToFile(zip, o.optString("iconName", ""), "pass_icons"),
                                createdAt = o.optLong("createdAt", System.currentTimeMillis())
                            )
                        )
                    }
                    zip.close()
                    tmp.delete()
                    context.getString(R.string.restore_completed)
                } catch (e: Exception) {
                    getApplication<Application>().getString(R.string.import_failed)
                }
            }
            _backupMessage.value = message
        }
    }

    fun consumeBackupMessage() {
        _backupMessage.value = null
    }

    private fun fileToBytes(path: String?): ByteArray? = try {
        path?.let { File(it).takeIf { f -> f.exists() }?.readBytes() }
    } catch (e: Exception) {
        null
    }

    private fun extensionOf(path: String?): String {
        val ext = path?.substringAfterLast('.', "") ?: ""
        return if (ext.isNotEmpty()) ".$ext" else ".png"
    }

    private fun zipEntryToFile(zip: ZipFile, entryName: String?, subdir: String): String? = try {
        if (entryName.isNullOrBlank()) {
            null
        } else {
            val bytes = zip.getInputStream(zip.getEntry(entryName)).use { it.readBytes() }
            val dir = File(getApplication<Application>().filesDir, subdir).apply { mkdirs() }
            val ext = entryName.substringAfterLast('.', "png")
            val out = File(dir, "${System.currentTimeMillis()}.$ext")
            out.writeBytes(bytes)
            out.absolutePath
        }
    } catch (e: Exception) {
        null
    }
}