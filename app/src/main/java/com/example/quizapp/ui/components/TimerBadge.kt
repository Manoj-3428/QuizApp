package com.example.quizapp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Animated timer badge that shows remaining time and a circular progress indicator.
 * Pulses red when time is running low (<= 10 seconds).
 */
@Composable
fun TimerBadge(
    timeLeftMs: Long,
    progressFraction: Float,
    modifier: Modifier = Modifier
) {
    val isRunningLow = timeLeftMs <= 10_000L && timeLeftMs > 0
    val secondsLeft = (timeLeftMs / 1000).toInt()
    val minutes = secondsLeft / 60
    val seconds = secondsLeft % 60
    val timeString = String.format("%02d:%02d", minutes, seconds)

    val color by animateColorAsState(
        targetValue = if (isRunningLow) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        animationSpec = tween(300),
        label = "timer_color"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "timer_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRunningLow) 1.15f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "timer_scale"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = tween(100), // Fast updates to match 100ms timer ticks
        label = "timer_progress"
    )

    Box(
        modifier = modifier
            .padding(8.dp)
            .scale(pulseScale),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.size(48.dp),
            color = color,
            trackColor = Color.LightGray.copy(alpha = 0.3f),
            strokeWidth = 3.dp
        )
        Text(
            text = timeString,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
