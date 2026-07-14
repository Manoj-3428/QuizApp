package com.example.quizapp.data.model

/**
 * Represents a single quiz question with its options and correct answer.
 */
data class Question(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctOptionIndex: Int
)
