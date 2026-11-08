package com.example.cardwallet.ui.edit

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cardwallet.data.CardEntity
import com.example.cardwallet.R
import com.example.cardwallet.ui.components.CardFace
import com.example.cardwallet.util.CardUtils
import com.example.cardwallet.util.localizedLabel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCardScreen(
    cardId: Long,
    onDone: () -> Unit,
    onBack: () -> Unit,
    viewModel: EditViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val error by viewModel.error.collectAsStateWithLifecycle()
    var cvvVisible by remember { mutableStateOf(false) }

    LaunchedEffect(cardId) { viewModel.load(cardId) }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) viewModel.saveImage(uri)
    }

    val preview = CardEntity(
        id = viewModel.editingId,
        imagePath = viewModel.imagePath.value,
        bankName = viewModel.bankName.value,
        cardholderName = viewModel.cardholderName.value,
        cardNumber = viewModel.cardNumber.value,
        expiryMonth = viewModel.expiry.value.take(2),
        expiryYear = viewModel.expiry.value.drop(2),
        cvv = viewModel.cvv.value,
        notes = viewModel.notes.value,
        numberColor = viewModel.numberColor.value
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (cardId > 0) stringResource(R.string.edit_card) else stringResource(R.string.add_card)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch { if (viewModel.save()) onDone() }
                    }) {
                        Icon(Icons.Filled.Check, contentDescription = stringResource(R.string.save))
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Card preview / upload
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.large)
                    .clickable {
                        pickImage.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
            ) {
                CardFace(
                    card = preview,
                    showInfo = false,
                    solidBackground = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Surface(
                    color = Color.Black.copy(alpha = 0.45f),
                    contentColor = Color.White,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                ) {
                    Text(
                        text = if (viewModel.imagePath.value == null) stringResource(R.string.upload_card_face) else stringResource(R.string.change_card_face),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            if (viewModel.imagePath.value != null) {
                TextButton(
                    onClick = { viewModel.clearImage() },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(Icons.Filled.Close, contentDescription = null)
                    Text(" " + stringResource(R.string.remove_card_face))
                }
            }

            Text(
                text = stringResource(R.string.number_color),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                NumberColorOption(stringResource(R.string.color_auto), null, viewModel.numberColor.value == null) {
                    viewModel.numberColor.value = null
                }
                NumberColorOption(stringResource(R.string.color_white), 0xFFFFFFFF.toInt(), viewModel.numberColor.value == 0xFFFFFFFF.toInt()) {
                    viewModel.numberColor.value = 0xFFFFFFFF.toInt()
                }
                NumberColorOption(stringResource(R.string.color_black), 0xFF1C1B1F.toInt(), viewModel.numberColor.value == 0xFF1C1B1F.toInt()) {
                    viewModel.numberColor.value = 0xFF1C1B1F.toInt()
                }
                NumberColorOption(stringResource(R.string.color_gray), 0xFF757575.toInt(), viewModel.numberColor.value == 0xFF757575.toInt()) {
                    viewModel.numberColor.value = 0xFF757575.toInt()
                }
            }

            OutlinedTextField(
                value = viewModel.bankName.value,
                onValueChange = { viewModel.bankName.value = it },
                label = { Text(stringResource(R.string.card_name_label)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.cardholderName.value,
                onValueChange = { viewModel.cardholderName.value = it },
                label = { Text(stringResource(R.string.cardholder_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = CardUtils.formatCardNumber(viewModel.cardNumber.value),
                onValueChange = { new ->
                    viewModel.cardNumber.value = new.filter { it.isDigit() }.take(19)
                },
                label = { Text(stringResource(R.string.card_number_label)) },
                supportingText = {
                    val digits = viewModel.cardNumber.value.length
                    val brand = CardUtils.detectBrand(viewModel.cardNumber.value).localizedLabel()
                    Text(stringResource(R.string.card_number_supporting, brand, digits))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = formatExpiryField(viewModel.expiry.value),
                onValueChange = { new ->
                    viewModel.expiry.value = new.filter { it.isDigit() }.take(4)
                },
                label = { Text(stringResource(R.string.expiry_label)) },
                placeholder = { Text("MM/YY") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.cvv.value,
                onValueChange = { new ->
                    viewModel.cvv.value = new.filter { it.isDigit() }.take(4)
                },
                label = { Text(stringResource(R.string.security_code_label)) },
                visualTransformation = if (cvvVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { cvvVisible = !cvvVisible }) {
                        Icon(
                            imageVector = if (cvvVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = stringResource(if (cvvVisible) R.string.hide else R.string.show)
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.notes.value,
                onValueChange = { viewModel.notes.value = it },
                label = { Text(stringResource(R.string.notes_label)) },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { scope.launch { if (viewModel.save()) onDone() } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}

private fun formatExpiryField(digits: String): String {
    return when {
        digits.isEmpty() -> ""
        digits.length <= 2 -> digits
        else -> digits.substring(0, 2) + "/" + digits.substring(2)
    }
}

@Composable
private fun NumberColorOption(label: String, color: Int?, selected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .then(
                    if (color == null) Modifier.background(Color(0xFFBDBDBD))
                    else Modifier.background(Color(color))
                )
                .border(
                    width = if (selected) 3.dp else 1.dp,
                    color = if (selected) MaterialTheme.colorScheme.primary else Color(0xFFC9CDD3),
                    shape = CircleShape
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (color == null) {
                Text("A", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            } else if (selected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = if (CardUtils.isLightColor(Color(color))) Color(0xFF1C1B1F) else Color.White
                )
            }
        }
        Text(label, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
    }
}