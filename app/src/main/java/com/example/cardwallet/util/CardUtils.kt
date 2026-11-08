package com.example.cardwallet.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.cardwallet.R

enum class CardBrand(val label: String) {
    VISA("VISA"),
    MASTERCARD("Mastercard"),
    AMEX("American Express"),
    UNIONPAY("UnionPay"),
    JCB("JCB"),
    DISCOVER("Discover"),
    UNKNOWN("Bank card")
}

@Composable
fun CardBrand.localizedLabel(): String = when (this) {
    CardBrand.UNIONPAY -> stringResource(R.string.brand_unionpay)
    CardBrand.UNKNOWN -> stringResource(R.string.brand_unknown)
    else -> label
}

object CardUtils {

    fun detectBrand(number: String): CardBrand {
        val n = digitsOnly(number)
        return when {
            n.startsWith("4") -> CardBrand.VISA
            n.startsWith("34") || n.startsWith("37") -> CardBrand.AMEX
            n.startsWith("51") || n.startsWith("52") || n.startsWith("53") ||
                n.startsWith("54") || n.startsWith("55") || n.startsWith("2221") ||
                n.startsWith("2720") -> CardBrand.MASTERCARD
            n.startsWith("62") -> CardBrand.UNIONPAY
            n.startsWith("35") -> CardBrand.JCB
            n.startsWith("6011") || n.startsWith("65") -> CardBrand.DISCOVER
            else -> CardBrand.UNKNOWN
        }
    }

    fun digitsOnly(input: String): String = input.filter { it.isDigit() }

    fun formatCardNumber(raw: String): String {
        val digits = digitsOnly(raw)
        val groups = if (detectBrand(digits) == CardBrand.AMEX) listOf(4, 6, 5) else listOf(4, 4, 4, 4)
        val sb = StringBuilder()
        var idx = 0
        var gi = 0
        while (idx < digits.length && gi < groups.size) {
            val end = minOf(idx + groups[gi], digits.length)
            if (sb.isNotEmpty()) sb.append(' ')
            sb.append(digits, idx, end)
            idx = end
            gi++
        }
        if (idx < digits.length) {
            if (sb.isNotEmpty()) sb.append(' ')
            sb.append(digits.substring(idx))
        }
        return sb.toString()
    }

    fun maskedNumber(raw: String): String {
        val digits = digitsOnly(raw)
        if (digits.length <= 4) return digits
        return "•••• •••• •••• ${digits.takeLast(4)}"
    }

    fun last4(raw: String): String {
        val d = digitsOnly(raw)
        return if (d.length >= 4) d.takeLast(4) else d
    }

    fun cardLabel(name: String, number: String, untitledFallback: String): String =
        (name.ifBlank { untitledFallback }) + " · ·" + last4(number)

    fun formatExpiry(month: String, year: String): String {
        val m = month.padStart(2, '0').take(2)
        val y = digitsOnly(year).takeLast(2)
        return when {
            m == "00" && y.isBlank() -> ""
            y.isBlank() -> m
            else -> "$m/$y"
        }
    }

    fun gradientColors(brand: CardBrand): List<Color> = when (brand) {
        CardBrand.VISA -> listOf(Color(0xFF1A1F71), Color(0xFF3B6FD4))
        CardBrand.MASTERCARD -> listOf(Color(0xFFEB001B), Color(0xFFF79E1B))
        CardBrand.AMEX -> listOf(Color(0xFF2E8B8B), Color(0xFF1B6F8F))
        CardBrand.UNIONPAY -> listOf(Color(0xFF005BAC), Color(0xFF00A3DA))
        CardBrand.JCB -> listOf(Color(0xFF0E4C92), Color(0xFF2F80C0))
        CardBrand.DISCOVER -> listOf(Color(0xFFF97316), Color(0xFFEF4444))
        CardBrand.UNKNOWN -> listOf(Color(0xFF4F378B), Color(0xFF7A5AF8))
    }

    fun isLightColor(color: Color): Boolean {
        val r = color.red
        val g = color.green
        val b = color.blue
        return (0.299f * r + 0.587f * g + 0.114f * b) > 0.6f
    }
}