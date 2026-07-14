package com.example.quizapp.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.animation.core.Animatable
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.quizapp.ui.components.OptionButton
import com.example.quizapp.ui.components.OptionState
import com.example.quizapp.ui.components.QuizTopBar
import com.example.quizapp.ui.components.StreakBadge
import com.example.quizapp.ui.state.QuizState
import com.example.quizapp.ui.viewmodel.QuizViewModel

/**
 * Main quiz screen matching the Stitch "Quiz Challenge Screen" design.
 *
 * Features:
 * - Top app bar with question counter and fire icon
 * - Animated progress bar
 * - Streak badge with glow animation at ≥3
 * - Question card with question chip and text
 * - 4 answer options with correct/incorrect color reveal
 * - Swipe gesture to skip
 * - Skip button and FAB for navigation
 * - AnimatedContent for smooth question transitions
 */
@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onQuizComplete: () -> Unit
) {
    val state by viewModel.quizState.collectAsState()

    // Navigate to results when quiz completes
    if (state.isQuizComplete) {
        onQuizComplete()
        return
    }

    val currentQuestion = state.currentQuestion ?: return

    // Swipe detection
    var swipeOffset by remember { mutableFloatStateOf(0f) }
    var showEndDialog by remember { mutableStateOf(false) }

    if (showEndDialog) {
        AlertDialog(
            onDismissRequest = { showEndDialog = false },
            title = { Text(text = "End Quiz?") },
            text = { Text(text = "Are you sure you want to end the quiz early? Your current progress will be shown.") },
            confirmButton = {
                TextButton(onClick = {
                    showEndDialog = false
                    onQuizComplete()
                }) {
                    Text("End Quiz")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            Column {
                QuizTopBar(
                    questionIndicator = "${state.currentQuestionIndex + 1}/${state.totalQuestions}",
                    isStreakOnFire = state.isStreakOnFire,
                    timeLeftMs = state.timeLeftMs,
                    timerProgressFraction = state.timerProgressFraction,
                    onEndQuizClick = { showEndDialog = true }
                )
                // Progress bar
                val animatedProgress by animateFloatAsState(
                    targetValue = state.progressFraction,
                    animationSpec = tween(600),
                    label = "progress"
                )
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    strokeCap = StrokeCap.Round
                )
            }
        },
        bottomBar = {
            // Bottom action bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Skip button
                OutlinedButton(
                    onClick = { viewModel.skipQuestion() },
                    enabled = !state.isAnswerRevealed,
                    shape = CircleShape,
                    border = ButtonDefaults.outlinedButtonBorder(enabled = !state.isAnswerRevealed),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        text = "Skip",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (!state.isAnswerRevealed) {
                            MaterialTheme.colorScheme.outline
                        } else {
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        }
                    )
                }

                // Next FAB
                FloatingActionButton(
                    onClick = { /* Auto-advance handles this */ },
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(24.dp),
                    containerColor = MaterialTheme.colorScheme.primary.copy(
                        alpha = if (state.isAnswerRevealed) 1f else 0.5f
                    ),
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(
                        text = "➔",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        // Swipe gesture wrapper
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pointerInput(state.currentQuestionIndex) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (swipeOffset < -100f && !state.isAnswerRevealed) {
                                viewModel.skipQuestion()
                            }
                            swipeOffset = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            swipeOffset += dragAmount
                        }
                    )
                }
        ) {
            AnimatedContent(
                targetState = state.currentQuestionIndex,
                transitionSpec = {
                    (slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) + fadeIn(animationSpec = tween(300)))
                        .togetherWith(
                            slideOutHorizontally(
                                targetOffsetX = { fullWidth -> -fullWidth },
                                animationSpec = tween(300)
                            ) + fadeOut(animationSpec = tween(200))
                        )
                },
                label = "question_transition"
            ) { questionIndex ->
                QuestionContent(
                    state = state,
                    questionIndex = questionIndex,
                    onOptionSelected = { viewModel.selectAnswer(it) }
                )
            }
        }
    }
}

@Composable
private fun QuestionContent(
    state: QuizState,
    questionIndex: Int,
    onOptionSelected: (Int) -> Unit
) {
    val question = state.questions.getOrNull(questionIndex) ?: return
    val scrollState = rememberScrollState()
    val optionLabels = listOf("A", "B", "C", "D")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Streak Badge
        StreakBadge(
            streak = state.currentStreak,
            isOnFire = state.isStreakOnFire
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Question Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                )
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .padding(24.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Question number chip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Question ${questionIndex + 1}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    Text(
                        text = "❔",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                // Question text
                Text(
                    text = question.question,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Answer Options
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            question.options.forEachIndexed { index, option ->
                val optionState = when {
                    !state.isAnswerRevealed -> OptionState.DEFAULT
                    index == state.selectedOptionIndex && index == question.correctOptionIndex -> OptionState.SELECTED_CORRECT
                    index == state.selectedOptionIndex && index != question.correctOptionIndex -> OptionState.SELECTED_INCORRECT
                    index == question.correctOptionIndex -> OptionState.REVEALED_CORRECT
                    else -> OptionState.DEFAULT
                }

                val alphaAnim = remember(questionIndex) { Animatable(0f) }
                val slideAnim = remember(questionIndex) { Animatable(50f) }

                LaunchedEffect(questionIndex) {
                    delay(index * 100L)
                    launch { alphaAnim.animateTo(1f, tween(300)) }
                    launch { slideAnim.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy)) }
                }

                OptionButton(
                    text = option,
                    optionLabel = optionLabels[index],
                    state = optionState,
                    onClick = { onOptionSelected(index) },
                    enabled = !state.isAnswerRevealed,
                    modifier = Modifier.graphicsLayer {
                        alpha = alphaAnim.value
                        translationY = slideAnim.value
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Swipe hint
        if (!state.isAnswerRevealed) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "👈",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .alpha(0.4f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Swipe left to skip",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}


