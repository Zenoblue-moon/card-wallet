package com.example.cardwallet.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cardwallet.R
import com.example.cardwallet.data.CardEntity
import com.example.cardwallet.util.CardUtils
import com.example.cardwallet.util.localizedLabel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun CardFace(
    card: CardEntity,
    modifier: Modifier = Modifier,
    showInfo: Boolean = true,
    showFullNumber: Boolean = false,
    solidBackground: Boolean = false,
    showLast4: Boolean = false
) {
    val brand = remember(card) { CardUtils.detectBrand(card.cardNumber) }
    val gradient = remember(card, brand) { CardUtils.gradientColors(brand) }
    val shape = RoundedCornerShape(20.dp)

    val imagePath = card.imagePath
    val hasImage = imagePath != null && File(imagePath).exists()

    val regionColor by produceState(Color.White, imagePath, gradient, hasImage) {
        value = if (hasImage) {
            withContext(Dispatchers.IO) { sampleImageColor(imagePath!!) }
        } else {
            lerp(gradient.first(), gradient.last(), 0.45f)
        }
    }
    val onFace = if (CardUtils.isLightColor(regionColor)) Color(0xFF1C1B1F) else Color.White
    val numberColor = card.numberColor?.let { Color(it) } ?: onFace

    Box(
        modifier
            .clip(shape)
            .background(
                if (solidBackground) SolidColor(gradient.first())
                else Brush.linearGradient(gradient)
            )
    ) {
        if (hasImage) {
            AsyncImage(
                model = File(imagePath!!),
                contentDescription = stringResource(R.string.card_face),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            if (showInfo) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                0f to Color.Transparent,
                                1f to Color.Black.copy(alpha = 0.35f)
                            )
                        )
                )
            }
        }

        if (showInfo) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.issuing_bank),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = card.bankName.ifBlank { stringResource(R.string.untitled_card_name) },
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = brand.localizedLabel().take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = if (showFullNumber)
                        CardUtils.formatCardNumber(card.cardNumber).ifBlank { "•••• •••• •••• ••••" }
                    else
                        CardUtils.maskedNumber(card.cardNumber).ifBlank { "•••• •••• •••• ••••" },
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    ),
                    color = numberColor,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.cardholder),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = card.cardholderName.ifBlank { stringResource(R.string.cardholder_name) },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = stringResource(R.string.expiry),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = CardUtils.formatExpiry(card.expiryMonth, card.expiryYear).ifBlank { "MM/YY" },
                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        if (!showInfo && showLast4) {
            Text(
                text = "· ·  ${CardUtils.last4(card.cardNumber)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                ),
                color = numberColor,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            )
        }
    }
}

private fun sampleImageColor(path: String): Color {
    return try {
        val opts = BitmapFactory.Options().apply { inSampleSize = 8 }
        val bmp = BitmapFactory.decodeFile(path, opts) ?: return Color.White
        val x = (bmp.width * 0.08f).toInt().coerceIn(0, bmp.width - 1)
        val y = (bmp.height * 0.80f).toInt().coerceIn(0, bmp.height - 1)
        val argb = bmp.getPixel(x, y)
        bmp.recycle()
        Color(argb)
    } catch (e: Exception) {
        Color.White
    }
}