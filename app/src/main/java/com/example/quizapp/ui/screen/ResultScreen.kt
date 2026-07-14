package com.example.quizapp.ui.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizapp.ui.components.ConfettiCanvas
import com.example.quizapp.ui.components.QuizTopBar
import com.example.quizapp.ui.components.StatCard
import com.example.quizapp.ui.theme.GoldBadge
import com.example.quizapp.ui.theme.GoldBadgeBg
import com.example.quizapp.ui.theme.GoldBadgeText
import com.example.quizapp.ui.viewmodel.QuizViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Results screen matching the Stitch "Quiz Results" design.
 *
 * Features:
 * - Confetti canvas animation (80 particles)
 * - Top app bar (reused QuizTopBar component)
 * - Hero celebration with "Quiz Complete!" heading
 * - Performance badge with gold glow (Excellent/Good/etc.)
 * - Bento-style 2x2 statistics grid (Score, Accuracy, Streak, Skipped)
 * - Restart and Exit action buttons
 * - Slide-up staggered animations for each section
 */
@Composable
fun ResultScreen(
    viewModel: QuizViewModel,
    onRestart: () -> Unit,
    onExit: () -> Unit
) {
    val state by viewModel.quizState.collectAsState()

    // ── Slide-up Animations ──
    val heroAlpha = remember { Animatable(0f) }
    val heroTranslateY = remember { Animatable(40f) }
    val badgeAlpha = remember { Animatable(0f) }
    val badgeTranslateY = remember { Animatable(40f) }
    val statsAlpha = remember { Animatable(0f) }
    val statsTranslateY = remember { Animatable(40f) }
    val buttonsAlpha = remember { Animatable(0f) }
    val buttonsTranslateY = remember { Animatable(40f) }

    LaunchedEffect(Unit) {
        // Staggered slide-up animations matching Stitch delays
        delay(200)
        launch {
            heroAlpha.animateTo(1f, tween(600))
        }
        launch {
            heroTranslateY.animateTo(0f, tween(800))
        }

        delay(200)
        launch {
            badgeAlpha.animateTo(1f, tween(600))
        }
        launch {
            badgeTranslateY.animateTo(0f, tween(800))
        }

        delay(200)
        launch {
            statsAlpha.animateTo(1f, tween(600))
        }
        launch {
            statsTranslateY.animateTo(0f, tween(800))
        }

        delay(200)
        launch {
            buttonsAlpha.animateTo(1f, tween(600))
        }
        launch {
            buttonsTranslateY.animateTo(0f, tween(800))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Confetti layer (behind everything)
        ConfettiCanvas()

        Scaffold(
            topBar = {
                QuizTopBar(isStreakOnFire = state.longestStreak >= 3)
            },
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // ── Hero Celebration ──
                Column(
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = heroAlpha.value
                            translationY = heroTranslateY.value
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Trophy emoji
                    Text(
                        text = "🏆",
                        fontSize = 80.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Quiz Complete!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Great job finishing the quiz.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── Performance Badge ──
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = badgeAlpha.value
                            translationY = badgeTranslateY.value
                        }
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            ambientColor = GoldBadge.copy(alpha = 0.3f),
                            spotColor = GoldBadge.copy(alpha = 0.3f)
                        )
                        .clip(CircleShape)
                        .background(GoldBadgeBg)
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "🏅", fontSize = 20.sp)
                        Text(
                            text = state.performanceLabel.uppercase(),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = GoldBadgeText,
                            letterSpacing = 2.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // ── Statistics Bento Grid ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            alpha = statsAlpha.value
                            translationY = statsTranslateY.value
                        }
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(32.dp),
                            ambientColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                        )
                        .clip(RoundedCornerShape(32.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                        .padding(24.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Score
                            StatCard(
                                icon = "📋",
                                label = "Score",
                                value = "${state.correctCount}/${state.totalQuestions}",
                                backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                                iconColor = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f)
                            )
                            // Accuracy
                            StatCard(
                                icon = "🎯",
                                label = "Accuracy",
                                value = "${state.accuracyPercent}%",
                                backgroundColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.15f),
                                iconColor = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Longest Streak
                            StatCard(
                                icon = "⚡",
                                label = "Longest Streak",
                                value = "${state.longestStreak}",
                                backgroundColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.15f),
                                iconColor = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.weight(1f)
                            )
                            // Skipped
                            StatCard(
                                icon = "⏭️",
                                label = "Skipped",
                                value = "${state.skippedCount}",
                                backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // ── Action Buttons ──
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            alpha = buttonsAlpha.value
                            translationY = buttonsTranslateY.value
                        },
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Restart Quiz
                    Button(
                        onClick = {
                            viewModel.restartQuiz()
                            onRestart()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text(text = "🔄", modifier = Modifier.padding(end = 8.dp))
                        Text(
                            text = "Restart Quiz",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Exit
                    OutlinedButton(
                        onClick = onExit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = CircleShape,
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Text(text = "🚪", modifier = Modifier.padding(end = 8.dp))
                        Text(
                            text = "Exit",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
