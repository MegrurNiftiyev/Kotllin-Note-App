package com.example.note_app_kotllin.ui.screens.aichat.componenets

import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.note_app_kotllin.R
import com.example.note_app_kotllin.core.constants.AppDurations
import com.example.note_app_kotllin.core.constants.Paddings
import com.example.note_app_kotllin.core.constants.Spaces

@Composable
fun AiTypingBubble() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Paddings.Mini),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 0.dp,
                topEnd = Paddings.Medium,
                bottomStart = Paddings.Medium,
                bottomEnd = Paddings.Medium,
            ), color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = Paddings.Medium, vertical = Paddings.Small
                ), verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = stringResource(R.string.ai_chat_typing),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.width(Spaces.ExtraSmall))
                ThreeDotsAnimation()

            }
        }
    }
}

@Composable
private fun ThreeDotsAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "ThreeDotsTransition")

    val dot1Offset = animateDotOffset(infiniteTransition, delay = 0)
    val dot2Offset = animateDotOffset(infiniteTransition, delay = 150)
    val dot3Offset = animateDotOffset(infiniteTransition, delay = 300)

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Dot(modifier = Modifier.graphicsLayer { translationY = dot1Offset.value })
        Dot(modifier = Modifier.graphicsLayer { translationY = dot2Offset.value })
        Dot(modifier = Modifier.graphicsLayer { translationY = dot3Offset.value })
    }
}

@Composable
private fun animateDotOffset(
    infiniteTransition: InfiniteTransition, delay: Int
): androidx.compose.runtime.State<Float> {
    val totalDuration = AppDurations.Medium.toInt()

    return infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = totalDuration
                0f at delay using LinearEasing
                -10f at delay + 200 using LinearEasing
                0f at delay + 400 using LinearEasing
                0f at totalDuration using LinearEasing
            }),
        label = "DotOffset"
    )
}

@Composable
private fun Dot(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
    )
}