package com.example.quizapp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.quizapp.ui.theme.CorrectGreen
import com.example.quizapp.ui.theme.IncorrectRed

/**
 * State of an answer option button.
 */
enum class OptionState {
    DEFAULT,
    SELECTED_CORRECT,
    SELECTED_INCORRECT,
    REVEALED_CORRECT // Not selected, but revealed as the correct answer
}

/**
 * Reusable answer option button matching the Stitch quiz screen design.
 * Supports default, correct, incorrect, and revealed states with animated color transitions.
 *
 * @param text The option text
 * @param optionLabel The letter label (A, B, C, D)
 * @param state Current visual state of the option
 * @param onClick Callback when the option is tapped
 * @param enabled Whether the option is clickable
 */
@Composable
fun OptionButton(
    text: String,
    optionLabel: String,
    state: OptionState,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = when (state) {
            OptionState.DEFAULT -> MaterialTheme.colorScheme.surfaceContainerLowest
            OptionState.SELECTED_CORRECT, OptionState.REVEALED_CORRECT -> CorrectGreen
            OptionState.SELECTED_INCORRECT -> IncorrectRed
        },
        animationSpec = spring(),
        label = "option_bg"
    )

    val contentColor by animateColorAsState(
        targetValue = when (state) {
            OptionState.DEFAULT -> MaterialTheme.colorScheme.onSurface
            else -> Color.White
        },
        animationSpec = spring(),
        label = "option_content"
    )

    val borderColor by animateColorAsState(
        targetValue = when (state) {
            OptionState.DEFAULT -> MaterialTheme.colorScheme.outlineVariant
            OptionState.SELECTED_CORRECT, OptionState.REVEALED_CORRECT -> CorrectGreen
            OptionState.SELECTED_INCORRECT -> IncorrectRed
        },
        animationSpec = spring(),
        label = "option_border"
    )

    val iconText = when (state) {
        OptionState.SELECTED_CORRECT, OptionState.REVEALED_CORRECT -> "✓"
        OptionState.SELECTED_INCORRECT -> "✕"
        else -> optionLabel
    }

    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Option label circle
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = when (state) {
                    OptionState.DEFAULT -> MaterialTheme.colorScheme.surfaceContainerHigh
                    OptionState.SELECTED_CORRECT, OptionState.REVEALED_CORRECT -> Color.White.copy(alpha = 0.2f)
                    OptionState.SELECTED_INCORRECT -> Color.White.copy(alpha = 0.2f)
                },
                modifier = Modifier.padding(end = 4.dp)
            ) {
                Text(
                    text = iconText,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
