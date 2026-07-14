package com.example.quizapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Data class representing a single confetti particle.
 */
private data class ConfettiParticle(
    var x: Float,
    var y: Float,
    val size: Float,
    val weight: Float,
    val speedX: Float,
    val color: Color,
    var rotation: Float,
    val rotationSpeed: Float
)

/**
 * Full-screen confetti canvas animation for the Results screen.
 * 80 particles with random HSL colors, rotation, and falling motion —
 * matching the Stitch confetti exactly.
 */
@Composable
fun ConfettiCanvas(
    modifier: Modifier = Modifier,
    particleCount: Int = 80
) {
    val particles = remember { mutableStateListOf<ConfettiParticle>() }
    val canvasWidth = remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    val canvasHeight = remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    val frameCount = remember { androidx.compose.runtime.mutableIntStateOf(0) }

    // Initialize particles once canvas size is known
    LaunchedEffect(Unit) {
        // Wait a tiny bit for canvas to be measured
        delay(50)
        if (particles.isEmpty()) {
            repeat(particleCount) {
                particles.add(createParticle(canvasWidth.floatValue, canvasHeight.floatValue))
            }
        }
        // Animation loop at ~60fps
        while (true) {
            delay(16L) // ~60fps
            val snapshot = particles.toList()
            particles.clear()
            particles.addAll(snapshot.map { p ->
                val newY = p.y + p.weight
                val newX = p.x + p.speedX
                val newRotation = p.rotation + p.rotationSpeed
                if (newY > canvasHeight.floatValue) {
                    p.copy(
                        y = -10f,
                        x = Random.nextFloat() * canvasWidth.floatValue,
                        rotation = newRotation
                    )
                } else {
                    p.copy(x = newX, y = newY, rotation = newRotation)
                }
            })
            frameCount.intValue++
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        canvasWidth.floatValue = size.width
        canvasHeight.floatValue = size.height

        // Re-initialize particles if they were created before canvas was measured
        if (particles.isNotEmpty() && particles[0].x == 0f && particles[0].y == 0f) {
            val newParticles = List(particleCount) { createParticle(size.width, size.height) }
            particles.clear()
            particles.addAll(newParticles)
        }

        // Force recomposition by reading frameCount
        @Suppress("UNUSED_VARIABLE")
        val frame = frameCount.intValue

        particles.forEach { particle ->
            drawConfettiParticle(particle)
        }
    }
}

private fun createParticle(width: Float, height: Float): ConfettiParticle {
    val safeWidth = if (width > 0f) width else 400f
    val safeHeight = if (height > 0f) height else 800f
    return ConfettiParticle(
        x = Random.nextFloat() * safeWidth,
        y = Random.nextFloat() * safeHeight - safeHeight,
        size = Random.nextFloat() * 8f + 4f,
        weight = Random.nextFloat() * 2f + 1f,
        speedX = Random.nextFloat() * 3f - 1.5f,
        color = Color.hsl(
            hue = Random.nextFloat() * 360f,
            saturation = 0.7f,
            lightness = 0.6f
        ),
        rotation = Random.nextFloat() * 360f,
        rotationSpeed = Random.nextFloat() * 5f - 2.5f
    )
}

private fun DrawScope.drawConfettiParticle(particle: ConfettiParticle) {
    rotate(
        degrees = particle.rotation,
        pivot = Offset(particle.x, particle.y)
    ) {
        drawRect(
            color = particle.color,
            topLeft = Offset(
                particle.x - particle.size / 2,
                particle.y - particle.size / 2
            ),
            size = Size(particle.size, particle.size)
        )
    }
}
