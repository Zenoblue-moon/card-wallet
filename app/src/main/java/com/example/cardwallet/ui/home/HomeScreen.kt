package com.example.cardwallet.ui.home

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.cardwallet.R
import com.example.cardwallet.data.CardEntity
import com.example.cardwallet.data.PassEntity
import com.example.cardwallet.ui.components.CardFace
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddCard: () -> Unit,
    onCardClick: (Long) -> Unit,
    onPassClick: (Long) -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val cards by viewModel.cards.collectAsStateWithLifecycle()
    val showLast4 by viewModel.showLast4OnHome.collectAsStateWithLifecycle()
    val hapticEnabled by viewModel.haptic.collectAsStateWithLifecycle()
    val passes by viewModel.passes.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    val hapticFeedback = LocalHapticFeedback.current
    fun tapCard(id: Long) {
        if (hapticEnabled) hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        onCardClick(id)
    }
    fun tapPass(id: Long) {
        if (hapticEnabled) hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        onPassClick(id)
    }
    val sheetState = rememberModalBottomSheetState()

    val openDocument = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) viewModel.importPass(uri)
    }

    val avatar by viewModel.avatarPath.collectAsStateWithLifecycle(initialValue = "")

    val avatarPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) viewModel.setAvatar(uri)
    }

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.consumeMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = { onOpenSettings() },
                                    onLongPress = {
                                        avatarPicker.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (avatar.isNotBlank()) {
                            AsyncImage(
                                model = avatar,
                                contentDescription = stringResource(R.string.avatar),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = stringResource(R.string.settings),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showMenu = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add))
            }
        }
    ) { padding ->
        if (cards.isEmpty() && passes.isEmpty()) {
            EmptyState(modifier = Modifier.fillMaxSize().padding(padding))
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (cards.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.card_count, cards.size),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }

            items(cards, key = { it.id }) { card ->
                CardFace(
                    card = card,
                    showInfo = false,
                    showLast4 = showLast4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.586f)
                        .clickable { tapCard(card.id) }
                )
            }

            if (passes.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        Text(
                            text = stringResource(R.string.passes),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 14.dp)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            passes.chunked(2).forEach { rowPasses ->
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    rowPasses.forEach { pass ->
                                        PassCard(
                                            pass = pass,
                                            onClick = { tapPass(pass.id) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    if (rowPasses.size == 1) {
                                        Spacer(Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showMenu) {
        ModalBottomSheet(
            onDismissRequest = { showMenu = false },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.padding(bottom = 24.dp)) {
                SheetOption(text = stringResource(R.string.add_bank_card)) {
                    showMenu = false
                    onAddCard()
                }
                SheetOption(text = stringResource(R.string.import_pkpass)) {
                    showMenu = false
                    openDocument.launch(arrayOf("*/*"))
                }
            }
        }
    }
}

@Composable
private fun PassCard(pass: PassEntity, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val bg = Color(pass.backgroundColor)
    val fg = Color(pass.foregroundColor)
    val label = Color(pass.labelColor)
    Column(
        modifier = modifier
            .aspectRatio(1.5f)
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        if (pass.iconPath != null) {
            AsyncImage(
                model = pass.iconPath,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(Modifier.weight(1f))
        Text(
            text = pass.title.ifBlank { pass.organizationName }.ifBlank { stringResource(R.string.untitled_pass) },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = fg,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (pass.subtitle.isNotBlank()) {
            Text(
                text = pass.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (pass.barcodeMessage.isNotBlank()) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = pass.barcodeMessage,
                style = MaterialTheme.typography.bodySmall,
                color = label.copy(alpha = 0.8f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SheetOption(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.CreditCard,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(R.string.empty_state_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = stringResource(R.string.empty_state_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}