package com.example.cardwallet.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class UpdateInfo(
    val tagName: String,
    val body: String,
    val htmlUrl: String
)

object UpdateChecker {

    private const val LATEST_RELEASE_URL =
        "https://api.github.com/repos/Zenoblue-moon/card-wallet/releases/latest"

    suspend fun checkLatestRelease(): UpdateInfo? = withContext(Dispatchers.IO) {
        try {
            val connection = URL(LATEST_RELEASE_URL).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "card-wallet")
            connection.setRequestProperty("Accept", "application/vnd.github+json")
            connection.connectTimeout = 10_000
            connection.readTimeout = 10_000
            val code = connection.responseCode
            val stream = if (code in 200..299) connection.inputStream else connection.errorStream
            val text = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
            connection.disconnect()
            if (code !in 200..299 || text.isBlank()) return@withContext null
            val json = JSONObject(text)
            UpdateInfo(
                tagName = json.optString("tag_name"),
                body = json.optString("body"),
                htmlUrl = json.optString("html_url")
            ).takeIf { it.tagName.isNotBlank() && it.htmlUrl.isNotBlank() }
        } catch (e: Exception) {
            null
        }
    }

    fun hasUpdate(latestTag: String, currentVersion: String): Boolean {
        val latest = normalize(latestTag)
        val current = normalize(currentVersion)
        if (latest.isEmpty() || current.isEmpty()) return false
        val size = maxOf(latest.size, current.size)
        for (i in 0 until size) {
            val l = latest.getOrElse(i) { 0 }
            val c = current.getOrElse(i) { 0 }
            if (l > c) return true
            if (l < c) return false
        }
        return false
    }

    private fun normalize(version: String): List<Int> =
        version.removePrefix("v").removePrefix("V")
            .split('.')
            .mapNotNull { it.toIntOrNull() }
}