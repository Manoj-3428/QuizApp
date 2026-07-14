package com.example.quizapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quizapp.ui.screen.OnboardingScreen
import com.example.quizapp.ui.screen.QuizScreen
import com.example.quizapp.ui.screen.ResultScreen
import com.example.quizapp.ui.screen.SplashScreen
import com.example.quizapp.ui.viewmodel.QuizViewModel

/**
 * Navigation routes for the Quiz App.
 */
object QuizRoutes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val QUIZ = "quiz"
    const val RESULT = "result"
}

/**
 * Main navigation graph for the Quiz App.
 *
 * Routes:
 *   splash → quiz → result
 *                      ↓
 *                    quiz (restart)
 *
 * The [QuizViewModel] is scoped to the NavGraph so it survives navigation
 * between Quiz and Result screens, but restarts fresh on "Restart Quiz".
 */
@Composable
fun QuizNavGraph() {
    val navController = rememberNavController()

    // Shared ViewModel scoped to the navigation graph
    val quizViewModel: QuizViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = QuizRoutes.SPLASH
    ) {
        composable(QuizRoutes.SPLASH) {
            SplashScreen(
                onNavigateToQuiz = {
                    navController.navigate(QuizRoutes.ONBOARDING) {
                        popUpTo(QuizRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(QuizRoutes.ONBOARDING) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(QuizRoutes.QUIZ) {
                        popUpTo(QuizRoutes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(QuizRoutes.QUIZ) {
            QuizScreen(
                viewModel = quizViewModel,
                onQuizComplete = {
                    navController.navigate(QuizRoutes.RESULT) {
                        popUpTo(QuizRoutes.QUIZ) { inclusive = true }
                    }
                }
            )
        }

        composable(QuizRoutes.RESULT) {
            ResultScreen(
                viewModel = quizViewModel,
                onRestart = {
                    navController.navigate(QuizRoutes.QUIZ) {
                        popUpTo(QuizRoutes.RESULT) { inclusive = true }
                    }
                },
                onExit = {
                    // Navigate back to splash or finish activity
                    navController.navigate(QuizRoutes.SPLASH) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
