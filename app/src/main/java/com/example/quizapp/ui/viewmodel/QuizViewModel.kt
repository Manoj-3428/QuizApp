package com.example.quizapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.repository.QuizRepository
import com.example.quizapp.ui.state.AnswerResult
import com.example.quizapp.ui.state.QuizState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel managing the quiz business logic.
 * Exposes an immutable [QuizState] via [StateFlow] for reactive UI updates.
 */
class QuizViewModel(
    private val repository: QuizRepository = QuizRepository()
) : ViewModel() {

    private val _quizState = MutableStateFlow(QuizState())
    val quizState: StateFlow<QuizState> = _quizState.asStateFlow()

    private var autoAdvanceJob: Job? = null
    private var timerJob: Job? = null

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        val questions = repository.getQuestions()
        _quizState.update { state ->
            state.copy(
                questions = questions,
                answerHistory = List(questions.size) { AnswerResult.UNANSWERED },
                timeLeftMs = 60_000L
            )
        }
        startTimer()
    }
    
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(100)
                val currentState = _quizState.value
                if (currentState.isQuizComplete || currentState.isAnswerRevealed) {
                    break
                }
                
                val newTime = currentState.timeLeftMs - 100
                if (newTime <= 0) {
                    _quizState.update { it.copy(timeLeftMs = 0) }
                    skipQuestion() // Auto-skip on timeout
                    break
                } else {
                    _quizState.update { it.copy(timeLeftMs = newTime) }
                }
            }
        }
    }

    /**
     * Called when the user taps an answer option.
     * Reveals the correct answer, updates streak, and auto-advances after 2 seconds.
     */
    fun selectAnswer(optionIndex: Int) {
        val currentState = _quizState.value
        if (currentState.isAnswerRevealed || currentState.isQuizComplete) return

        timerJob?.cancel()
        val question = currentState.currentQuestion ?: return
        val isCorrect = optionIndex == question.correctOptionIndex

        val newStreak = if (isCorrect) currentState.currentStreak + 1 else 0
        val newLongestStreak = maxOf(currentState.longestStreak, newStreak)
        val result = if (isCorrect) AnswerResult.CORRECT else AnswerResult.INCORRECT

        val updatedHistory = currentState.answerHistory.toMutableList().also {
            it[currentState.currentQuestionIndex] = result
        }

        _quizState.update { state ->
            state.copy(
                selectedOptionIndex = optionIndex,
                isAnswerRevealed = true,
                correctCount = if (isCorrect) state.correctCount + 1 else state.correctCount,
                incorrectCount = if (!isCorrect) state.incorrectCount + 1 else state.incorrectCount,
                currentStreak = newStreak,
                longestStreak = newLongestStreak,
                answerHistory = updatedHistory
            )
        }

        // Auto-advance after 2 seconds
        autoAdvanceJob?.cancel()
        autoAdvanceJob = viewModelScope.launch {
            delay(2000)
            moveToNextQuestion()
        }
    }

    /**
     * Called when the user taps "Skip".
     * Immediately advances to the next question without waiting.
     */
    fun skipQuestion() {
        val currentState = _quizState.value
        if (currentState.isAnswerRevealed || currentState.isQuizComplete) return

        timerJob?.cancel()
        autoAdvanceJob?.cancel()

        val updatedHistory = currentState.answerHistory.toMutableList().also {
            it[currentState.currentQuestionIndex] = AnswerResult.SKIPPED
        }

        _quizState.update { state ->
            state.copy(
                skippedCount = state.skippedCount + 1,
                currentStreak = 0, // Skip breaks the streak
                answerHistory = updatedHistory
            )
        }

        moveToNextQuestion()
    }

    private fun moveToNextQuestion() {
        _quizState.update { state ->
            val nextIndex = state.currentQuestionIndex + 1
            if (nextIndex >= state.totalQuestions) {
                state.copy(isQuizComplete = true)
            } else {
                state.copy(
                    currentQuestionIndex = nextIndex,
                    selectedOptionIndex = null,
                    isAnswerRevealed = false,
                    timeLeftMs = 60_000L
                )
            }
        }
        if (!_quizState.value.isQuizComplete) {
            startTimer()
        }
    }

    /**
     * Resets all quiz state to start fresh from Question 1.
     */
    fun restartQuiz() {
        autoAdvanceJob?.cancel()
        timerJob?.cancel()
        val questions = _quizState.value.questions
        _quizState.value = QuizState(
            questions = questions,
            answerHistory = List(questions.size) { AnswerResult.UNANSWERED },
            timeLeftMs = 60_000L
        )
        startTimer()
    }
}
