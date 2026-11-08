package com.example.cardwallet.ui.pass

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.cardwallet.R
import com.example.cardwallet.data.PassEntity
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassDetailScreen(
    passId: Long,
    onBack: () -> Unit,
    viewModel: PassDetailViewModel = viewModel()
) {
    val pass by viewModel.observe(passId).collectAsStateWithLifecycle(initialValue = null)
    var showDelete by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.pass_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = { showDelete = true }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.delete),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { padding ->
        val current = pass
        if (current == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.loading), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            PassLarge(current)

            if (current.barcodeMessage.isNotBlank()) {
                Text(
                    text = stringResource(R.string.barcode_qr),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
                )
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BarcodeImage(format = current.barcodeFormat, message = current.barcodeMessage)
                }
            }

            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    InfoRow(stringResource(R.string.field_title), current.title.ifBlank { stringResource(R.string.not_set) })
                    InfoRow(stringResource(R.string.organization), current.organizationName.ifBlank { stringResource(R.string.not_set) })
                    InfoRow(stringResource(R.string.description), current.subtitle.ifBlank { stringResource(R.string.not_set) })
                    if (current.barcodeMessage.isNotBlank()) {
                        InfoRow(stringResource(R.string.barcode_content), current.barcodeMessage)
                    }
                }
            }
        }
    }

    if (showDelete) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            title = { Text(stringResource(R.string.delete_pass_title)) },
            text = { Text(stringResource(R.string.delete_pass_message)) },
            confirmButton = {
                TextButton(onClick = {
                    pass?.let { viewModel.delete(it) }
                    onBack()
                }) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { showDelete = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

@Composable
private fun PassLarge(pass: PassEntity) {
    val bg = Color(pass.backgroundColor)
    val fg = Color(pass.foregroundColor)
    val label = Color(pass.labelColor)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.5f)
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(24.dp)
    ) {
        if (pass.iconPath != null) {
            AsyncImage(
                model = pass.iconPath,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(56.dp)
            )
        }
        Spacer(Modifier.weight(1f))
        Text(
            text = pass.title.ifBlank { pass.organizationName }.ifBlank { stringResource(R.string.untitled_pass) },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = fg,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = pass.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun BarcodeImage(format: String, message: String) {
    val bitmap by produceState<Bitmap?>(initialValue = null, format, message) {
        value = withContext(Dispatchers.Default) { generateBarcode(format, message) }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = stringResource(R.string.barcode),
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                color = Color.Black
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(96.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}

private fun generateBarcode(format: String, message: String): Bitmap? = try {
    val f = when (format) {
        "PKBarcodeFormatCode128" -> BarcodeFormat.CODE_128
        "PKBarcodeFormatPDF417" -> BarcodeFormat.PDF_417
        "PKBarcodeFormatAztec" -> BarcodeFormat.AZTEC
        "PKBarcodeFormatEAN13" -> BarcodeFormat.EAN_13
        "PKBarcodeFormatEAN8" -> BarcodeFormat.EAN_8
        "PKBarcodeFormatCode39" -> BarcodeFormat.CODE_39
        else -> BarcodeFormat.QR_CODE
    }
    val matrix = MultiFormatWriter().encode(
        message,
        f,
        if (f == BarcodeFormat.QR_CODE) 512 else 900,
        if (f == BarcodeFormat.QR_CODE) 512 else 220
    )
    val w = matrix.width
    val h = matrix.height
    val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    for (x in 0 until w) {
        for (y in 0 until h) {
            bmp.setPixel(x, y, if (matrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
        }
    }
    bmp
} catch (e: Exception) {
    null
}