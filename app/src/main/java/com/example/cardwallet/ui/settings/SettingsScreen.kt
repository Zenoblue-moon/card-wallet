package com.example.cardwallet.ui.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cardwallet.R
import com.example.cardwallet.ui.navigation.transitionLabel
import com.example.cardwallet.ui.navigation.transitionOptions
import com.example.cardwallet.ui.theme.AppPalette
import com.example.cardwallet.ui.theme.appPalettes
import com.example.cardwallet.ui.theme.paletteLabel
import com.example.cardwallet.util.CardUtils

private val currencyOptions = listOf("¥", "$", "€", "£")

@Composable
private fun currencyLabel(symbol: String): String = when (symbol) {
    "$" -> stringResource(R.string.currency_usd)
    "€" -> stringResource(R.string.currency_eur)
    "£" -> stringResource(R.string.currency_gbp)
    else -> stringResource(R.string.currency_cny)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenTapToPay: () -> Unit,
    onSortCards: () -> Unit,
    onAbout: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val themeColor by viewModel.themeColor.collectAsStateWithLifecycle(initialValue = "purple")
    val enabled by viewModel.contactlessEnabled.collectAsStateWithLifecycle(initialValue = false)
    val amount by viewModel.paymentAmount.collectAsStateWithLifecycle(initialValue = "")
    val merchant by viewModel.merchantName.collectAsStateWithLifecycle(initialValue = "")
    val currency by viewModel.currency.collectAsStateWithLifecycle(initialValue = "¥")
    val paymentCardId by viewModel.paymentCardId.collectAsStateWithLifecycle(initialValue = -1L)
    val showLast4 by viewModel.showLast4OnHome.collectAsStateWithLifecycle(initialValue = false)
    val paymentStatus by viewModel.paymentStatus.collectAsStateWithLifecycle(initialValue = "success")
    val dynamicColor by viewModel.dynamicColor.collectAsStateWithLifecycle(initialValue = false)
    val transition by viewModel.transition.collectAsStateWithLifecycle(initialValue = "fade")
    val haptic by viewModel.haptic.collectAsStateWithLifecycle(initialValue = true)
    val tapHintShown by viewModel.tapHintShown.collectAsStateWithLifecycle(initialValue = false)
    val cards by viewModel.cards.collectAsStateWithLifecycle(initialValue = emptyList())

    var showAmountDialog by remember { mutableStateOf(false) }
    var showMerchantDialog by remember { mutableStateOf(false) }
    var showCardDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }
    var showTransitionDialog by remember { mutableStateOf(false) }
    var showTapHint by remember { mutableStateOf(false) }

    val selectedCard = cards.firstOrNull { it.id == paymentCardId }

    val context = LocalContext.current
    val backupMessage by viewModel.backupMessage.collectAsStateWithLifecycle(initialValue = null)

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/zip")
    ) { uri -> if (uri != null) viewModel.exportBackup(uri) }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri -> if (uri != null) viewModel.importBackup(uri) }

    LaunchedEffect(backupMessage) {
        backupMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.consumeBackupMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            SectionTitle(stringResource(R.string.appearance))
            Text(
                text = stringResource(R.string.app_theme),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                appPalettes.forEach { palette ->
                    PaletteDot(
                        palette = palette,
                        selected = palette.key == themeColor,
                        onClick = { viewModel.setThemeColor(palette.key) }
                    )
                }
            }
            ToggleRow(
                title = stringResource(R.string.dynamic_color),
                checked = dynamicColor,
                onCheckedChange = { viewModel.setDynamicColor(it) }
            )
            SettingRow(
                title = stringResource(R.string.page_transition_animation),
                subtitle = transitionLabel(transition),
                onClick = { showTransitionDialog = true }
            )
            ToggleRow(
                title = stringResource(R.string.haptic_feedback),
                checked = haptic,
                onCheckedChange = { viewModel.setHaptic(it) }
            )

            HorizontalDivider(Modifier.padding(vertical = 12.dp))

            SectionTitle(stringResource(R.string.contactless_payment_demo))
            ToggleRow(
                title = stringResource(R.string.contactless_payment),
                checked = enabled,
                onCheckedChange = { viewModel.setContactlessEnabled(it) }
            )
            if (enabled) {
                SettingRow(
                    title = stringResource(R.string.select_card),
                    subtitle = selectedCard?.let { CardUtils.cardLabel(it.bankName, it.cardNumber, stringResource(R.string.untitled_card)) }
                        ?: stringResource(R.string.auto_first_card),
                    onClick = { showCardDialog = true }
                )
                SettingRow(
                    title = stringResource(R.string.payment_amount),
                    subtitle = if (amount.isBlank()) stringResource(R.string.not_set) else "$currency $amount",
                    onClick = { showAmountDialog = true }
                )
                SettingRow(
                    title = stringResource(R.string.currency),
                    subtitle = currency,
                    onClick = { showCurrencyDialog = true }
                )
                SettingRow(
                    title = stringResource(R.string.merchant_name),
                    subtitle = merchant.ifBlank { stringResource(R.string.not_set) },
                    onClick = { showMerchantDialog = true }
                )
                SettingRow(
                    title = stringResource(R.string.payment_status),
                    subtitle = if (paymentStatus == "fail") stringResource(R.string.payment_failed) else stringResource(R.string.payment_success),
                    onClick = { showStatusDialog = true }
                )
                SettingRow(
                    title = stringResource(R.string.open_payment_screen),
                    subtitle = "",
                    onClick = {
                        if (tapHintShown) onOpenTapToPay() else showTapHint = true
                    }
                )
            }

            HorizontalDivider(Modifier.padding(vertical = 12.dp))

            SectionTitle(stringResource(R.string.home_section))
            ToggleRow(
                title = stringResource(R.string.show_last4_on_home),
                checked = showLast4,
                onCheckedChange = { viewModel.setShowLast4OnHome(it) }
            )
            SettingRow(
                title = stringResource(R.string.sort_cards),
                subtitle = stringResource(R.string.manual_sort_cards),
                onClick = onSortCards
            )

            HorizontalDivider(Modifier.padding(vertical = 12.dp))

            SectionTitle(stringResource(R.string.backup_restore))
            SettingRow(
                title = stringResource(R.string.backup_data),
                subtitle = "",
                onClick = {
                    exportLauncher.launch("cardwallet_backup_${System.currentTimeMillis()}.zip")
                }
            )
            SettingRow(
                title = stringResource(R.string.restore_data),
                subtitle = "",
                onClick = {
                    importLauncher.launch(arrayOf("application/zip", "application/octet-stream", "*/*"))
                }
            )

            HorizontalDivider(Modifier.padding(vertical = 12.dp))

            SectionTitle(stringResource(R.string.about))
            SettingRow(
                title = stringResource(R.string.about),
                subtitle = "",
                onClick = onAbout
            )
        }
    }

    if (showAmountDialog) {
        TextInputDialog(
            title = stringResource(R.string.payment_amount),
            initial = amount,
            onDismiss = { showAmountDialog = false },
            onConfirm = {
                viewModel.setPaymentAmount(it.trim())
                showAmountDialog = false
            }
        )
    }

    if (showMerchantDialog) {
        TextInputDialog(
            title = stringResource(R.string.merchant_name),
            initial = merchant,
            onDismiss = { showMerchantDialog = false },
            onConfirm = {
                viewModel.setMerchantName(it.trim())
                showMerchantDialog = false
            }
        )
    }

    if (showCardDialog) {
        AlertDialog(
            onDismissRequest = { showCardDialog = false },
            title = { Text(stringResource(R.string.select_card)) },
            text = {
                Column {
                    SelectOptionRow(
                        selected = selectedCard == null,
                        text = stringResource(R.string.auto_first_card),
                        onClick = {
                            viewModel.setPaymentCardId(-1L)
                            showCardDialog = false
                        }
                    )
                    cards.forEach { card ->
                        SelectOptionRow(
                            selected = card.id == paymentCardId,
                            text = CardUtils.cardLabel(card.bankName, card.cardNumber, stringResource(R.string.untitled_card)),
                            onClick = {
                                viewModel.setPaymentCardId(card.id)
                                showCardDialog = false
                            }
                        )
                    }
                    if (cards.isEmpty()) {
                        Text(
                            text = stringResource(R.string.no_cards_add_prompt),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCardDialog = false }) { Text(stringResource(R.string.close)) }
            }
        )
    }

    if (showCurrencyDialog) {
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = { Text(stringResource(R.string.select_currency)) },
            text = {
                Column {
                    currencyOptions.forEach { symbol ->
                        SelectOptionRow(
                            selected = symbol == currency,
                            text = currencyLabel(symbol),
                            onClick = {
                                viewModel.setCurrency(symbol)
                                showCurrencyDialog = false
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCurrencyDialog = false }) { Text(stringResource(R.string.close)) }
            }
        )
    }

    if (showStatusDialog) {
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text(stringResource(R.string.payment_status)) },
            text = {
                Column {
                    SelectOptionRow(
                        selected = paymentStatus == "success",
                        text = stringResource(R.string.payment_success_with_sound),
                        onClick = {
                            viewModel.setPaymentStatus("success")
                            showStatusDialog = false
                        }
                    )
                    SelectOptionRow(
                        selected = paymentStatus == "fail",
                        text = stringResource(R.string.payment_failed),
                        onClick = {
                            viewModel.setPaymentStatus("fail")
                            showStatusDialog = false
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showStatusDialog = false }) { Text(stringResource(R.string.close)) }
            }
        )
    }

    if (showTransitionDialog) {
        AlertDialog(
            onDismissRequest = { showTransitionDialog = false },
            title = { Text(stringResource(R.string.page_transition_animation)) },
            text = {
                Column {
                    transitionOptions.forEach { option ->
                        SelectOptionRow(
                            selected = option.key == transition,
                            text = transitionLabel(option.key),
                            onClick = {
                                viewModel.setTransition(option.key)
                                showTransitionDialog = false
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTransitionDialog = false }) { Text(stringResource(R.string.close)) }
            }
        )
    }

    if (showTapHint) {
        AlertDialog(
            onDismissRequest = { showTapHint = false },
            title = { Text(stringResource(R.string.tip_title)) },
            text = { Text(stringResource(R.string.tip_message)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setTapHintShown(true)
                    showTapHint = false
                    onOpenTapToPay()
                }) { Text(stringResource(R.string.got_it)) }
            }
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
    )
}

@Composable
private fun ToggleRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingRow(title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        if (subtitle.isNotBlank()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AboutRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value)
    }
}

@Composable
private fun PaletteDot(palette: AppPalette, selected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .then(
                    if (selected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(50))
                    else Modifier
                )
        ) {
            PaletteSegment(palette.lightPrimary)
            PaletteSegment(palette.lightSecondary)
            PaletteSegment(palette.lightTertiary)
        }
        Text(
            text = paletteLabel(palette.key),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun PaletteSegment(color: Color) {
    Box(
        modifier = Modifier
            .size(width = 16.dp, height = 28.dp)
            .background(color)
    )
}

@Composable
private fun SelectOptionRow(selected: Boolean, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        if (selected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun TextInputDialog(
    title: String,
    initial: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var value by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(value) }) { Text(stringResource(R.string.ok)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}