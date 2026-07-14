package com.example.quizapp.data.repository

import com.example.quizapp.data.model.Question
import com.example.quizapp.data.source.QuizDataSource

/**
 * Repository that acts as the single source of truth for quiz data.
 * Abstracts the data source from the ViewModel layer.
 */
class QuizRepository {

    fun getQuestions(): List<Question> {
        return QuizDataSource.getQuestions()
    }
}
