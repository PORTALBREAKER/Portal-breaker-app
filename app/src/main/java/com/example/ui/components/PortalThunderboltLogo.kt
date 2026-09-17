package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.PortalCyan
import com.example.ui.theme.PortalGold
import com.example.ui.theme.PortalPurple
import com.example.ui.theme.VoidCard
import com.example.ui.theme.VoidDark

/**
 * Premium iOS-grade "P" with integrated electric Thunderbolt (⚡) Logo.
 * Designed with geometric precision, frosted obsidian glass, and electric plasma glow.
 */
@Composable
fun PortalThunderboltLogo(
    modifier: Modifier = Modifier,
    size: Dp = 42.dp,
    onClick: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "thunderGlow")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val shape = RoundedCornerShape(12.dp)

    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null, // Completely silent for stealth author activation
            onClick = onClick
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .size(size)
            .shadow(elevation = 8.dp, shape = shape, spotColor = PortalCyan.copy(alpha = 0.35f))
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFF161E30),
                        Color(0xFF090D18),
                        Color(0xFF04060B)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        PortalCyan.copy(alpha = 0.85f),
                        PortalPurple.copy(alpha = 0.5f),
                        Color(0x22FFFFFF)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(100f, 100f)
                ),
                shape = shape
            )
            .then(clickModifier),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            // Ambient electric aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        PortalCyan.copy(alpha = 0.25f * pulse),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.5f, h * 0.5f),
                    radius = w * 0.6f
                )
            )

            // 1. Stylized Letter "P" Path
            val pPath = Path().apply {
                // Vertical stem of "P"
                addRoundRect(
                    RoundRect(
                        left = w * 0.22f,
                        top = h * 0.18f,
                        right = w * 0.36f,
                        bottom = h * 0.82f,
                        cornerRadius = CornerRadius(w * 0.05f, w * 0.05f)
                    )
                )
                // Upper Loop of "P"
                moveTo(w * 0.34f, h * 0.18f)
                lineTo(w * 0.58f, h * 0.18f)
                cubicTo(
                    w * 0.78f, h * 0.18f,
                    w * 0.78f, h * 0.52f,
                    w * 0.58f, h * 0.52f
                )
                lineTo(w * 0.34f, h * 0.52f)
                close()
            }

            // Draw "P" body with platinum-cyan gradient
            drawPath(
                path = pPath,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White,
                        Color(0xFFE2E8F0),
                        Color(0xFF94A3B8)
                    )
                ),
                style = Fill
            )

            // Inner negative space cutout of the P loop
            val pInnerHole = Path().apply {
                moveTo(w * 0.36f, h * 0.28f)
                lineTo(w * 0.54f, h * 0.28f)
                cubicTo(
                    w * 0.65f, h * 0.28f,
                    w * 0.65f, h * 0.42f,
                    w * 0.54f, h * 0.42f
                )
                lineTo(w * 0.36f, h * 0.42f)
                close()
            }
            drawPath(
                path = pInnerHole,
                color = Color(0xFF0C101A),
                style = Fill
            )

            // 2. High-Voltage Thunderbolt (⚡) slicing diagonally through P
            val boltGlowPath = Path().apply {
                moveTo(w * 0.66f, h * 0.12f)  // Top lightning apex
                lineTo(w * 0.44f, h * 0.46f)  // Inward strike
                lineTo(w * 0.58f, h * 0.46f)  // Step outward
                lineTo(w * 0.38f, h * 0.86f)  // Bottom lightning spear
                lineTo(w * 0.52f, h * 0.52f)  // Step return
                lineTo(w * 0.40f, h * 0.52f)  // Inward step
                close()
            }

            // Thunderbolt outer neon plasma stroke
            drawPath(
                path = boltGlowPath,
                color = PortalCyan.copy(alpha = 0.6f * pulse),
                style = Stroke(
                    width = 2.5.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Miter
                )
            )

            // Thunderbolt core fill (Electric Gold & Cyan)
            drawPath(
                path = boltGlowPath,
                brush = Brush.linearGradient(
                    listOf(
                        Color(0xFFFFF066),
                        PortalGold,
                        PortalCyan
                    ),
                    start = Offset(w * 0.66f, h * 0.12f),
                    end = Offset(w * 0.38f, h * 0.86f)
                ),
                style = Fill
            )
        }
    }
}
