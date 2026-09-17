package com.example.ui.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Glassmorphic style modifier providing a translucent frosted glass surface
 * framed by a subtle glowing gradient border.
 */
fun Modifier.glassmorphic(
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = Color(0x1AFFFFFF),
    borderColor: Color = Color(0x3300F5D4),
    secondaryBorderColor: Color = Color(0x228B5CF6),
    borderWidth: Dp = 1.dp
): Modifier = this
    .clip(shape)
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(
                backgroundColor.copy(alpha = (backgroundColor.alpha * 1.3f).coerceAtMost(1f)),
                backgroundColor.copy(alpha = (backgroundColor.alpha * 0.7f).coerceAtLeast(0f))
            )
        ),
        shape = shape
    )
    .border(
        width = borderWidth,
        brush = Brush.linearGradient(
            colors = listOf(
                borderColor,
                secondaryBorderColor,
                Color.Transparent
            ),
            start = Offset(0f, 0f),
            end = Offset(400f, 400f)
        ),
        shape = shape
    )

/**
 * Advanced iOS-grade glassmorphic reader surface modifier.
 * Features:
 * - Multi-stop translucent vertical gradient overlay with a top specular sheen
 * - Delicate 1.dp hairline border with refraction gradient (white highlight descending into subtle accent)
 * - Layered ambient and specular shadow depth
 * - Inner specular reflection hairline at top edge
 */
fun Modifier.iosReaderGlassSurface(
    shape: Shape = RoundedCornerShape(22.dp),
    backgroundColor: Color = Color(0x14FFFFFF),
    accentColor: Color = PortalCyan,
    isLight: Boolean = false,
    borderWidth: Dp = 1.dp
): Modifier = this
    .shadow(
        elevation = if (isLight) 8.dp else 16.dp,
        shape = shape,
        ambientColor = if (isLight) Color(0x15000000) else Color(0x66000000),
        spotColor = if (isLight) Color(0x0C000000) else accentColor.copy(alpha = 0.16f)
    )
    .clip(shape)
    .background(
        brush = Brush.verticalGradient(
            colors = if (isLight) {
                listOf(
                    Color(0xFFFFFFFF).copy(alpha = 0.94f),
                    Color(0xFFF8FAFC).copy(alpha = 0.82f),
                    Color(0xFFF1F5F9).copy(alpha = 0.88f)
                )
            } else {
                listOf(
                    backgroundColor.copy(alpha = (backgroundColor.alpha * 1.6f).coerceIn(0.20f, 0.90f)),
                    backgroundColor.copy(alpha = (backgroundColor.alpha * 0.95f).coerceIn(0.12f, 0.70f)),
                    backgroundColor.copy(alpha = (backgroundColor.alpha * 1.35f).coerceIn(0.18f, 0.85f))
                )
            }
        ),
        shape = shape
    )
    .drawBehind {
        // Specular top highlight line (characteristic of iOS frosted glass slabs)
        val strokeWidth = 1.2.dp.toPx()
        val topHighlight = if (isLight) Color.White.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.28f)
        drawLine(
            color = topHighlight,
            start = Offset(16.dp.toPx(), 0f),
            end = Offset(size.width - 16.dp.toPx(), 0f),
            strokeWidth = strokeWidth
        )
    }
    .border(
        width = borderWidth,
        brush = Brush.linearGradient(
            colors = if (isLight) {
                listOf(
                    Color.White.copy(alpha = 0.95f),
                    Color.White.copy(alpha = 0.45f),
                    accentColor.copy(alpha = 0.28f),
                    Color.Transparent
                )
            } else {
                listOf(
                    Color.White.copy(alpha = 0.38f),
                    accentColor.copy(alpha = 0.40f),
                    Color.White.copy(alpha = 0.06f),
                    Color.Transparent
                )
            },
            start = Offset(0f, 0f),
            end = Offset(450f, 700f)
        ),
        shape = shape
    )

/**
 * Floating iOS Frosted Glass HUD (Header Bar & Navigation Pill Dock).
 * Delivers deep translucency, specular border reflection, and floating depth.
 */
fun Modifier.iosGlassHud(
    shape: Shape = RoundedCornerShape(22.dp),
    containerColor: Color = VoidDark,
    accentColor: Color = PortalCyan,
    isLight: Boolean = false
): Modifier = this
    .shadow(
        elevation = 20.dp,
        shape = shape,
        ambientColor = if (isLight) Color(0x1F000000) else Color(0x80000000),
        spotColor = if (isLight) Color(0x12000000) else accentColor.copy(alpha = 0.28f)
    )
    .clip(shape)
    .background(
        brush = Brush.verticalGradient(
            colors = if (isLight) {
                listOf(
                    Color.White.copy(alpha = 0.94f),
                    Color(0xFFF8FAFC).copy(alpha = 0.88f)
                )
            } else {
                listOf(
                    containerColor.copy(alpha = 0.92f),
                    containerColor.copy(alpha = 0.80f)
                )
            }
        ),
        shape = shape
    )
    .drawBehind {
        // Specular highlight line
        val strokeWidth = 1.dp.toPx()
        val highlightColor = if (isLight) Color.White else Color.White.copy(alpha = 0.35f)
        drawLine(
            color = highlightColor,
            start = Offset(14.dp.toPx(), 0f),
            end = Offset(size.width - 14.dp.toPx(), 0f),
            strokeWidth = strokeWidth
        )
    }
    .border(
        width = 1.dp,
        brush = Brush.linearGradient(
            colors = if (isLight) {
                listOf(
                    Color.White,
                    Color.White.copy(alpha = 0.5f),
                    accentColor.copy(alpha = 0.32f),
                    Color.Transparent
                )
            } else {
                listOf(
                    Color.White.copy(alpha = 0.42f),
                    accentColor.copy(alpha = 0.38f),
                    Color.White.copy(alpha = 0.08f),
                    Color.Transparent
                )
            },
            start = Offset(0f, 0f),
            end = Offset(300f, 300f)
        ),
        shape = shape
    )

/**
 * Neomorphic surface styling:
 * Provides soft dual-depth illumination (top-left highlight + bottom-right shadow)
 * characteristic of modern tactile neomorphism.
 */
fun Modifier.neomorphic(
    shape: Shape = RoundedCornerShape(16.dp),
    elevation: Dp = 6.dp,
    surfaceColor: Color = VoidCard,
    isDark: Boolean = true
): Modifier = this
    .shadow(
        elevation = elevation,
        shape = shape,
        ambientColor = if (isDark) Color(0xFF020408) else Color(0xFFB0C0D0),
        spotColor = if (isDark) Color(0xFF010204) else Color(0xFF8A9BA8)
    )
    .clip(shape)
    .background(surfaceColor, shape = shape)
    .drawBehind {
        val strokeWidth = 1.5.dp.toPx()
        // Top-left soft highlight edge
        val highlightColor = if (isDark) Color(0x1FFFFFFF) else Color(0x80FFFFFF)
        drawLine(
            color = highlightColor,
            start = Offset(0f, size.height * 0.7f),
            end = Offset(0f, 0f),
            strokeWidth = strokeWidth
        )
        drawLine(
            color = highlightColor,
            start = Offset(0f, 0f),
            end = Offset(size.width * 0.7f, 0f),
            strokeWidth = strokeWidth
        )
        // Bottom-right soft shadow edge
        val shadowColor = if (isDark) Color(0x40000000) else Color(0x1F000000)
        drawLine(
            color = shadowColor,
            start = Offset(size.width, size.height * 0.3f),
            end = Offset(size.width, size.height),
            strokeWidth = strokeWidth
        )
        drawLine(
            color = shadowColor,
            start = Offset(size.width * 0.3f, size.height),
            end = Offset(size.width, size.height),
            strokeWidth = strokeWidth
        )
    }

/**
 * Tactile Neomorphic Button with ripple feedback and subtle inset/outset depth.
 */
@Composable
fun NeomorphicButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(14.dp),
    backgroundColor: Color = VoidSurfaceVariant,
    isDark: Boolean = true,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .neomorphic(shape = shape, elevation = 4.dp, surfaceColor = backgroundColor, isDark = isDark)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = PortalCyan),
                onClick = onClick
            ),
        propagateMinConstraints = false
    ) {
        content()
    }
}

/**
 * Animated Dimensional Portal Background with pulsating cosmic energy rings
 */
@Composable
fun PortalCosmicBackground(
    modifier: Modifier = Modifier,
    isDark: Boolean = true,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "portalPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    val rotateShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotateShift"
    )

    Box(
        modifier = modifier
            .background(if (isDark) VoidDark else LightCanvas)
            .drawBehind {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Radial cosmic glow from top-right corner
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            PortalPurple.copy(alpha = pulseAlpha),
                            PortalSky.copy(alpha = pulseAlpha * 0.4f),
                            Color.Transparent
                        ),
                        center = Offset(canvasWidth * 0.85f + rotateShift, canvasHeight * 0.15f),
                        radius = canvasWidth * 0.9f
                    )
                )

                // Secondary cyan fracture glow from bottom-left corner
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            PortalCyan.copy(alpha = pulseAlpha * 0.6f),
                            Color.Transparent
                        ),
                        center = Offset(canvasWidth * 0.1f, canvasHeight * 0.75f),
                        radius = canvasWidth * 0.7f
                    )
                )
            }
    ) {
        content()
    }
}
