package com.example.note_app_kotllin.ui.screens.aichat.componenets

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.note_app_kotllin.R
import com.example.note_app_kotllin.core.constants.IconSizes
import kotlinx.coroutines.delay
@Composable
fun AiChatInputBar(
    modifier: Modifier = Modifier,
    inputText: String,
    isLoading: Boolean,
    onInputChanged: (String) -> Unit,
    onSendClicked: () -> Unit,
    onStopClicked: () -> Unit = {}
) {
    val barHeight = 56.dp
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var isIntroActive by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(6000)
        isIntroActive = false
    }

    val shouldAnimate = isIntroActive || isLoading

    val handleSend = {
        if (inputText.isNotBlank() && !isLoading) {
            onSendClicked()
            focusManager.clearFocus()
            keyboardController?.hide()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight),
            shape = RoundedCornerShape(50),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = inputText,
                    onValueChange = onInputChanged,
                    modifier = Modifier
                        .weight(1f)
                        .height(barHeight),
                    placeholder = { Text(stringResource(R.string.ai_chat_placeholder)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = { handleSend() }
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )

                FilledIconButton(
                    onClick = {
                        if (isLoading) {
                            onStopClicked()
                        } else {
                            handleSend()
                        }
                    },
                    modifier = Modifier.size(44.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors()
                ) {
                    Icon(
                        painter = if (isLoading) painterResource(R.drawable.stop) else painterResource(
                            R.drawable.sent
                        ),
                        contentDescription = if (isLoading) {
                            stringResource(R.string.ai_chat_stop)
                        } else {
                            stringResource(R.string.ai_chat_send)
                        },
                        modifier = Modifier.size(IconSizes.LargePlus),
                        tint = Color.White
                    )
                }
            }
        }

        if (shouldAnimate) {
            AnimatedBorderUltraGlow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(barHeight),
                cornerRadius = barHeight / 2,
                enableGlow = true
            )
        }
    }
}
@Composable
fun AnimatedBorderUltraGlow(
    modifier: Modifier = Modifier,
    cornerRadius: Dp,
    strokeWidth: Dp = 7.dp,
    enableGlow: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "travelingBorder")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotation"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.tertiary

    Box(modifier = modifier) {
        if (enableGlow) {
            Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .blur(20.dp)
                    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }) {
                rotate(rotation) {
                    val diameter = (size.width.coerceAtLeast(size.height)) * 2f
                    val topLeft = Offset(center.x - diameter / 2f, center.y - diameter / 2f)
                    drawRect(
                        brush = Brush.sweepGradient(
                            colorStops = arrayOf(
                                0.0f to Color.Transparent,
                                0.2f to primaryColor,
                                0.5f to secondaryColor,
                                0.7f to primaryColor.copy(alpha = 0.5f),
                                1.0f to Color.Transparent
                            ), center = center
                        ),
                        topLeft = topLeft,
                        size = Size(diameter, diameter),
                        blendMode = BlendMode.SrcIn
                    )
                }
            }
        }

        Canvas(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }) {
            val strokeWidthPx = strokeWidth.toPx()
            val radiusPx = cornerRadius.toPx()

            drawRoundRect(
                color = primaryColor.copy(alpha = 0.08f),
                cornerRadius = CornerRadius(radiusPx, radiusPx),
                style = Stroke(width = strokeWidthPx)
            )

            rotate(rotation) {
                val diameter = (size.width.coerceAtLeast(size.height)) * 2f
                val topLeft = Offset(center.x - diameter / 2f, center.y - diameter / 2f)

                drawRect(
                    brush = Brush.sweepGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Transparent,
                            0.15f to primaryColor,
                            0.5f to secondaryColor,
                            0.75f to primaryColor.copy(alpha = 0.3f),
                            1.0f to Color.Transparent
                        ), center = center
                    ),
                    topLeft = topLeft,
                    size = Size(diameter, diameter),
                    blendMode = BlendMode.SrcIn
                )
            }
        }
    }
}