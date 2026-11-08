package com.example.cardwallet.ui.sort

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cardwallet.R
import com.example.cardwallet.ui.components.CardFace

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortCardsScreen(
    onBack: () -> Unit,
    viewModel: SortCardsViewModel = viewModel()
) {
    val cards by viewModel.cards.collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.sort_cards)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        if (cards.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(stringResource(R.string.no_cards), style = MaterialTheme.typography.titleMedium)
                Text(
                    text = stringResource(R.string.add_card_first),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(cards, key = { _, c -> c.id }) { index, card ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CardFace(
                        card = card,
                        showInfo = false,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.586f)
                    )
                    Column {
                        IconButton(
                            enabled = index > 0,
                            onClick = { viewModel.move(index, index - 1) }
                        ) {
                            Icon(Icons.Filled.ArrowUpward, contentDescription = stringResource(R.string.move_up))
                        }
                        IconButton(
                            enabled = index < cards.lastIndex,
                            onClick = { viewModel.move(index, index + 1) }
                        ) {
                            Icon(Icons.Filled.ArrowDownward, contentDescription = stringResource(R.string.move_down))
                        }
                    }
                }
            }
        }
    }
}