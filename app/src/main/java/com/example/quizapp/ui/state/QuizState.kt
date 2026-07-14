package com.example.quizapp.ui.state

import com.example.quizapp.data.model.Question

/**
 * Represents the result of answering a single question.
 */
enum class AnswerResult {
    CORRECT,
    INCORRECT,
    SKIPPED,
    UNANSWERED
}

/**
 * Immutable state for the entire quiz session.
 * Managed exclusively by [com.example.quizapp.ui.viewmodel.QuizViewModel].
 */
data class QuizState(
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isAnswerRevealed: Boolean = false,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val skippedCount: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val isQuizComplete: Boolean = false,
    val answerHistory: List<AnswerResult> = emptyList(),
    val timeLeftMs: Long = 60_000L
) {
    val totalQuestions: Int get() = questions.size

    val currentQuestion: Question?
        get() = questions.getOrNull(currentQuestionIndex)

    val progressFraction: Float
        get() = if (totalQuestions > 0) {
            (currentQuestionIndex + 1).toFloat() / totalQuestions
        } else 0f

    val accuracyPercent: Int
        get() {
            val answered = correctCount + incorrectCount
            return if (answered > 0) ((correctCount.toFloat() / answered) * 100).toInt() else 0
        }

    val isStreakOnFire: Boolean get() = currentStreak >= 3

    val timerProgressFraction: Float
        get() = (timeLeftMs.toFloat() / 60_000f).coerceIn(0f, 1f)

    val performanceLabel: String
        get() = when {
            correctCount >= 9 -> "Outstanding"
            correctCount >= 7 -> "Excellent"
            correctCount >= 5 -> "Good Job"
            correctCount >= 3 -> "Keep Trying"
            else -> "Practice More"
        }
}
