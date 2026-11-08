package com.example.cardwallet.ui.payment

import android.media.MediaPlayer
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cardwallet.R
import com.example.cardwallet.ui.components.CardFace
import com.example.cardwallet.util.CardUtils

@Composable
fun TapToPayScreen(
    onClose: () -> Unit,
    viewModel: TapToPayViewModel = viewModel()
) {
    val amount by viewModel.amount.collectAsStateWithLifecycle()
    val currency by viewModel.currency.collectAsStateWithLifecycle()
    val merchant by viewModel.merchant.collectAsStateWithLifecycle()
    val card by viewModel.card.collectAsStateWithLifecycle()
    val status by viewModel.paymentStatus.collectAsStateWithLifecycle(initialValue = "success")

    val context = LocalContext.current
    var paid by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    fun pay() {
        if (paid || failed) return
        if (status == "success") {
            MediaPlayer.create(context, R.raw.pay)?.apply {
                setOnCompletionListener { it.release() }
                start()
            }
            paid = true
        } else {
            failed = true
        }
    }

    val infinite = rememberInfiniteTransition(label = "inf")
    val pulse by infinite.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "pulse"
    )
    val bgT by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000), RepeatMode.Reverse),
        label = "bg"
    )

    val primary = MaterialTheme.colorScheme.primary
    val background = Brush.verticalGradient(
        listOf(
            lerp(Color(0xFF0A0A0F), Color(0xFF141B2D), bgT),
            lerp(primary, Color(0xFF341671), bgT)
        )
    )

    val checkProgress by animateFloatAsState(
        targetValue = if (paid) 1f else 0f,
        animationSpec = tween(500),
        label = "check"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyUp && event.key == Key.VolumeDown) {
                    pay()
                    true
                } else {
                    false
                }
            }
            .background(background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.close), tint = Color.White)
                }
            }

            Spacer(Modifier.weight(0.4f))

            Text(
                text = merchant.ifBlank { stringResource(R.string.merchant) },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(Modifier.height(12.dp))

            if (card != null) {
                CardFace(
                    card = card!!,
                    showInfo = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.586f)
                        .shadow(16.dp, RoundedCornerShape(20.dp))
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = CardUtils.cardLabel(card!!.bankName, card!!.cardNumber, stringResource(R.string.untitled_card)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
            } else {
                Text(
                    text = stringResource(R.string.no_card_selected),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            Spacer(Modifier.weight(0.8f))

            Text(
                text = "$currency $amount",
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(Modifier.weight(0.8f))

            Surface(
                shape = CircleShape,
                color = when {
                    paid -> Color(0xFF2E7D32)
                    failed -> Color(0xFFC62828)
                    else -> Color.White.copy(alpha = 0.12f)
                },
                modifier = Modifier
                    .size(104.dp)
                    .scale(if (paid || failed) 1f else pulse)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    when {
                        paid -> Checkmark(
                            progress = checkProgress,
                            color = Color.White,
                            modifier = Modifier.size(64.dp)
                        )
                        failed -> Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(60.dp)
                        )
                        else -> Icon(
                            imageVector = Icons.Filled.Contactless,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = when {
                    paid -> stringResource(R.string.pay_success)
                    failed -> stringResource(R.string.pay_failed)
                    else -> stringResource(R.string.tap_hold_near_reader)
                },
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

            if (paid) {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onClose,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(stringResource(R.string.done))
                }
            } else if (failed) {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { failed = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(stringResource(R.string.retry))
                }
            }

            Spacer(Modifier.weight(0.8f))
        }
    }
}

@Composable
private fun Checkmark(progress: Float, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val s = size.minDimension
        val start = Offset(s * 0.22f, s * 0.52f)
        val mid = Offset(s * 0.44f, s * 0.72f)
        val end = Offset(s * 0.80f, s * 0.34f)
        val path = Path().apply {
            moveTo(start.x, start.y)
            lineTo(mid.x, mid.y)
            lineTo(end.x, end.y)
        }
        val measure = PathMeasure().apply { setPath(path, false) }
        val length = measure.length
        val rendered = Path()
        measure.getSegment(0f, length * progress, rendered, true)
        drawPath(
            path = rendered,
            color = color,
            style = Stroke(width = s * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}