package com.example.cardwallet.ui.home

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardwallet.CardWalletApplication
import com.example.cardwallet.R
import com.example.cardwallet.data.CardEntity
import com.example.cardwallet.data.PassEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.util.zip.ZipFile

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as CardWalletApplication
    private val repository = app.repository
    private val passRepository = app.passRepository

    private val _cards = MutableStateFlow<List<CardEntity>>(emptyList())
    val cards: StateFlow<List<CardEntity>> = _cards

    val showLast4OnHome: StateFlow<Boolean> = app.settingsRepository.showLast4OnHome
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val avatarPath: StateFlow<String> = app.settingsRepository.avatarPath
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val haptic: StateFlow<Boolean> = app.settingsRepository.haptic
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val passes: StateFlow<List<PassEntity>> = passRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private var persistJob: Job? = null

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
        schedulePersist()
    }

    private fun schedulePersist() {
        persistJob?.cancel()
        persistJob = viewModelScope.launch {
            delay(250)
            repository.reorder(_cards.value.map { it.id })
        }
    }

    fun importPass(uri: Uri) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { parsePass(uri) }
            if (result == null) {
                _message.value = app.getString(R.string.import_failed_pkpass)
            } else {
                passRepository.insert(result)
                val fallback = app.getString(R.string.untitled_pass)
                _message.value = app.getString(R.string.imported_pass, result.title.ifBlank { fallback })
            }
        }
    }

    fun consumeMessage() {
        _message.value = null
    }

    fun setAvatar(uri: Uri) {
        viewModelScope.launch {
            val path = withContext(Dispatchers.IO) {
                try {
                    val dir = File(getApplication<Application>().filesDir, "avatars").apply { mkdirs() }
                    val out = File(dir, "avatar.png")
                    getApplication<Application>().contentResolver.openInputStream(uri)?.use { input ->
                        out.outputStream().use { output -> input.copyTo(output) }
                    }
                    out.absolutePath
                } catch (e: Exception) {
                    null
                }
            }
            if (path != null) app.settingsRepository.setAvatarPath(path)
        }
    }

    private fun parsePass(uri: Uri): PassEntity? = try {
        val ctx = getApplication<Application>()
        val input = ctx.contentResolver.openInputStream(uri) ?: return null
        val tmp = File.createTempFile("pass", ".pkpass", ctx.cacheDir)
        tmp.outputStream().use { input.copyTo(it) }

        val result = ZipFile(tmp).use { zip ->
            val entry = zip.getEntry("pass.json") ?: return@use null
            val json = zip.getInputStream(entry).bufferedReader().use { it.readText() }
            val obj = JSONObject(json)
            val title = obj.optString("description")
            val org = obj.optString("organizationName")
            val logoText = obj.optString("logoText")
            PassEntity(
                title = title,
                subtitle = logoText,
                organizationName = org,
                backgroundColor = parseRgb(obj.optString("backgroundColor")),
                foregroundColor = parseRgb(obj.optString("foregroundColor")),
                labelColor = parseRgb(obj.optString("labelColor")),
                barcodeMessage = obj.optJSONObject("barcode")?.optString("message") ?: "",
                barcodeFormat = obj.optJSONObject("barcode")?.optString("format") ?: "",
                iconPath = extractIcon(zip)
            )
        }
        tmp.delete()
        result
    } catch (e: Exception) {
        null
    }

    private fun extractIcon(zip: ZipFile): String? {
        val name = listOf("icon.png", "icon@2x.png").firstOrNull { zip.getEntry(it) != null }
            ?: return null
        val entry = zip.getEntry(name)
        val dir = File(getApplication<Application>().filesDir, "pass_icons").apply { mkdirs() }
        val out = File(dir, "${System.currentTimeMillis()}.png")
        zip.getInputStream(entry).use { input ->
            out.outputStream().use { output -> input.copyTo(output) }
        }
        return out.absolutePath
    }

    private fun parseRgb(s: String): Int {
        val m = Regex("rgb\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\)").find(s)
            ?: return 0xFF1B1B1F.toInt()
        val r = m.groupValues[1].toInt().coerceIn(0, 255)
        val g = m.groupValues[2].toInt().coerceIn(0, 255)
        val b = m.groupValues[3].toInt().coerceIn(0, 255)
        return (0xFF000000.toInt()) or (r shl 16) or (g shl 8) or b
    }
}