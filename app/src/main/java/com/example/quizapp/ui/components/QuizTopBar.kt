package com.example.quizapp.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Shared Top App Bar matching the Stitch design.
 * Used on both Quiz and Result screens.
 *
 * @param questionIndicator Optional "X/Y" text shown on the quiz screen
 * @param isStreakOnFire Whether the fire icon should be filled (streak ≥ 3)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizTopBar(
    modifier: Modifier = Modifier,
    questionIndicator: String? = null,
    isStreakOnFire: Boolean = false,
    timeLeftMs: Long? = null,
    timerProgressFraction: Float? = null,
    onEndQuizClick: (() -> Unit)? = null
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = "Quiz Challenge",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        },
        actions = {
            if (questionIndicator != null) {
                Text(
                    text = questionIndicator,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            
            if (timeLeftMs != null && timerProgressFraction != null) {
                TimerBadge(
                    timeLeftMs = timeLeftMs,
                    progressFraction = timerProgressFraction
                )
            }
            
            if (onEndQuizClick != null) {
                androidx.compose.material3.TextButton(
                    onClick = onEndQuizClick,
                    colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(text = "End Quiz", fontWeight = FontWeight.Bold)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}
