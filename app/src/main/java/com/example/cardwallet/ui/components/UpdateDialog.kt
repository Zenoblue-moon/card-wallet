package com.example.cardwallet.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.cardwallet.R
import com.example.cardwallet.data.UpdateInfo

@Composable
fun UpdateAvailableDialog(
    info: UpdateInfo,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.update_title, info.tagName)) },
        text = { Text(info.body) },
        confirmButton = {
            TextButton(onClick = {
                runCatching {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(info.htmlUrl)))
                }
                onDismiss()
            }) { Text(stringResource(R.string.update_button)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.update_later)) }
        }
    )
}