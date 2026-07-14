package com.example.quizapp.ui.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.quizapp.ui.components.LoadingDots
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Splash screen matching the Stitch "Luminous Quiz - Splash" design.
 *
 * Features:
 * - Glassmorphism card with frosted white background
 * - Animated quiz icon with scale+fade entrance
 * - "Luminous Quiz" title with staggered fade-in
 * - 3-dot pulse loading indicator
 * - Auto-navigates to quiz after 2.5 seconds
 */
@Composable
fun SplashScreen(
    onNavigateToQuiz: () -> Unit
) {
    // ── Animation States ──
    val logoScale = remember { Animatable(0.5f) }
    val logoAlpha = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }
    val dotsAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Logo entrance animation (spring with overshoot)
        launch {
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        launch {
            logoAlpha.animateTo(1f, animationSpec = tween(800))
        }

        // Staggered fade-in for text elements
        delay(800)
        launch {
            titleAlpha.animateTo(1f, animationSpec = tween(600))
        }
        delay(200)
        launch {
            subtitleAlpha.animateTo(1f, animationSpec = tween(600))
        }
        delay(200)
        launch {
            dotsAlpha.animateTo(1f, animationSpec = tween(600))
        }

        // Auto-navigate after 2.5 seconds total
        delay(1300)
        onNavigateToQuiz()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Glassmorphism card
        Box(
            modifier = Modifier
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(40.dp),
                    ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                )
                .clip(RoundedCornerShape(40.dp))
                .background(Color.White.copy(alpha = 0.6f))
                .padding(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Quiz Icon with animation
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .scale(logoScale.value)
                        .alpha(logoAlpha.value)
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(28.dp),
                            ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        )
                        .clip(RoundedCornerShape(28.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "❓",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = MaterialTheme.typography.displayLarge.fontSize * 0.7f
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Title
                Text(
                    text = "Luminous Quiz",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.alpha(titleAlpha.value)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subtitle
                Text(
                    text = "Preparing your quiz...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.alpha(subtitleAlpha.value)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Loading dots
                LoadingDots(
                    modifier = Modifier.alpha(dotsAlpha.value)
                )
            }
        }
    }
}
