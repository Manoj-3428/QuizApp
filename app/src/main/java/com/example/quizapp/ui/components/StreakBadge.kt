package com.example.quizapp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.quizapp.ui.theme.StreakOrange
import com.example.quizapp.ui.theme.StreakOrangeBg
import com.example.quizapp.ui.theme.StreakOrangeBorder

/**
 * Streak badge chip that displays the current streak count.
 * Activates a glowing orange fire effect when streak reaches 3 or more.
 */
@Composable
fun StreakBadge(
    streak: Int,
    isOnFire: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isOnFire) StreakOrangeBg else MaterialTheme.colorScheme.surfaceContainerLow,
        animationSpec = spring(),
        label = "streak_bg"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isOnFire) StreakOrangeBorder else MaterialTheme.colorScheme.outlineVariant,
        animationSpec = spring(),
        label = "streak_border"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "streak_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isOnFire) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "streak_scale"
    )

    Surface(
        modifier = modifier
            .scale(pulseScale)
            .then(
                if (isOnFire) {
                    Modifier.shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                        ambientColor = StreakOrange.copy(alpha = 0.4f),
                        spotColor = StreakOrange.copy(alpha = 0.4f)
                    )
                } else Modifier
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = CircleShape
            ),
        shape = CircleShape,
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "🔥",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Streak: $streak",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (isOnFire) {
                Text(
                    text = "On Fire!",
                    style = MaterialTheme.typography.labelLarge,
                    color = StreakOrange,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
