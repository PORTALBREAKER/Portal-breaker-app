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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PortalCrimson
import com.example.ui.theme.PortalGold
import com.example.ui.theme.PortalRedLightning
import com.example.ui.theme.PortalRuby
import com.example.ui.theme.VoidCard
import com.example.ui.theme.VoidDark

/**
 * Premium iOS-grade "P" with integrated electric Red Lightning (⚡) Logo & Dimensional Rift.
 * Red Lightning Edition: Designed with geometric precision, frosted obsidian glass, and crimson plasma glow.
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
            .shadow(elevation = 10.dp, shape = shape, spotColor = PortalRedLightning.copy(alpha = 0.5f))
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFF1E070D),
                        Color(0xFF120508),
                        Color(0xFF070305)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        PortalRedLightning.copy(alpha = 0.95f),
                        PortalCrimson.copy(alpha = 0.5f),
                        Color(0x33FF6B81)
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

            // Ambient electric aura - Red Lightning
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        PortalRedLightning.copy(alpha = 0.35f * pulse),
                        PortalCrimson.copy(alpha = 0.15f * pulse),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.5f, h * 0.5f),
                    radius = w * 0.70f
                )
            )

            // Outer dimensional rift arc ring
            drawArc(
                color = PortalRedLightning.copy(alpha = 0.35f),
                startAngle = 30f,
                sweepAngle = 260f,
                useCenter = false,
                topLeft = Offset(w * 0.08f, h * 0.08f),
                size = Size(w * 0.84f, h * 0.84f),
                style = Stroke(width = 1.dp.toPx(), cap = StrokeCap.Round)
            )

            // 1. Stylized Geometric Letter "P" Path with Chamfered Precision
            val pPath = Path().apply {
                // Vertical stem of "P"
                addRoundRect(
                    RoundRect(
                        left = w * 0.22f,
                        top = h * 0.18f,
                        right = w * 0.36f,
                        bottom = h * 0.82f,
                        cornerRadius = CornerRadius(w * 0.04f, w * 0.04f)
                    )
                )
                // Upper Loop of "P"
                moveTo(w * 0.34f, h * 0.18f)
                lineTo(w * 0.60f, h * 0.18f)
                cubicTo(
                    w * 0.79f, h * 0.18f,
                    w * 0.79f, h * 0.52f,
                    w * 0.60f, h * 0.52f
                )
                lineTo(w * 0.34f, h * 0.52f)
                close()
            }

            // Draw "P" body with platinum metallic gradient
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
                lineTo(w * 0.55f, h * 0.28f)
                cubicTo(
                    w * 0.66f, h * 0.28f,
                    w * 0.66f, h * 0.42f,
                    w * 0.55f, h * 0.42f
                )
                lineTo(w * 0.36f, h * 0.42f)
                close()
            }
            drawPath(
                path = pInnerHole,
                color = Color(0xFF0C0407),
                style = Fill
            )

            // 2. High-Voltage Red Thunderbolt (⚡) slicing diagonally through P
            val boltGlowPath = Path().apply {
                moveTo(w * 0.68f, h * 0.12f)  // Top lightning apex
                lineTo(w * 0.44f, h * 0.46f)  // Inward strike
                lineTo(w * 0.58f, h * 0.46f)  // Step outward
                lineTo(w * 0.36f, h * 0.88f)  // Bottom lightning spear
                lineTo(w * 0.52f, h * 0.52f)  // Step return
                lineTo(w * 0.40f, h * 0.52f)  // Inward step
                close()
            }

            // Thunderbolt outer neon plasma stroke
            drawPath(
                path = boltGlowPath,
                color = PortalRedLightning.copy(alpha = 0.85f * pulse),
                style = Stroke(
                    width = 2.5.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Thunderbolt core fill (Electric Red Core with White-Hot Accent)
            drawPath(
                path = boltGlowPath,
                brush = Brush.linearGradient(
                    listOf(
                        Color.White,
                        Color(0xFFFF7A8F),
                        PortalRedLightning,
                        PortalCrimson
                    ),
                    start = Offset(w * 0.68f, h * 0.12f),
                    end = Offset(w * 0.36f, h * 0.88f)
                ),
                style = Fill
            )

            // Micro energetic spark
            drawCircle(
                color = Color.White.copy(alpha = 0.9f * pulse),
                radius = 1.2.dp.toPx(),
                center = Offset(w * 0.36f, h * 0.88f)
            )
        }
    }
}

/**
 * Custom-crafted PORTAL BREAKER wordmark with geometric letter cuts,
 * metallic silver tones, and electric Red Lightning fracture accent.
 */
@Composable
fun PortalBreakerWordmark(
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    val typography = MaterialTheme.typography
    val titleStyle = if (isCompact) typography.titleMedium else typography.titleLarge

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PORTAL",
                style = titleStyle.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.5.sp
                )
            )
            Spacer(modifier = Modifier.size(if (isCompact) 4.dp else 6.dp))
            Text(
                text = "BREAKER",
                style = titleStyle.copy(
                    fontWeight = FontWeight.Black,
                    brush = Brush.horizontalGradient(
                        listOf(
                            PortalRedLightning,
                            Color(0xFFFF4D6D),
                            PortalCrimson
                        )
                    ),
                    letterSpacing = 2.sp
                )
            )
        }
        // Subtitle hairline
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(width = 12.dp, height = 1.5.dp)
                    .background(PortalRedLightning)
            )
            Text(
                text = "ORIGINAL WEBNOVEL ARCHIVE",
                style = typography.labelSmall.copy(
                    color = Color(0xFF94A3B8),
                    fontSize = if (isCompact) 8.sp else 9.sp,
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}
