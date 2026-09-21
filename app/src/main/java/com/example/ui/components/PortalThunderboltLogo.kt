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
 * Custom Portal Breaker "PB" Monogram Logo with integrated Red Lightning & Dimensional Rift.
 * Inspired by the metallic chiseled PB emblem: interlocking P & B, brushed titanium facets,
 * circular electric crimson portal ring, surface fracture cracks, and a high-voltage lightning rift.
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

            // 1. Ambient Electric Crimson Aura in background
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        PortalRedLightning.copy(alpha = 0.40f * pulse),
                        PortalCrimson.copy(alpha = 0.18f * pulse),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.5f, h * 0.5f),
                    radius = w * 0.70f
                )
            )

            // 2. Circular Red Lightning Ring / Portal Aura (Centered at 0.5, 0.5 with radius 0.42*w)
            val ringRadius = w * 0.40f
            // Wide outer red glow
            drawCircle(
                color = PortalRedLightning.copy(alpha = 0.25f * pulse),
                radius = ringRadius,
                center = Offset(w * 0.5f, h * 0.5f),
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )
            // Medium sharp red ring
            drawCircle(
                color = PortalRedLightning.copy(alpha = 0.75f * pulse),
                radius = ringRadius,
                center = Offset(w * 0.5f, h * 0.5f),
                style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
            )
            // Inner hot core arc
            drawArc(
                color = Color(0xFFFF5271),
                startAngle = -60f,
                sweepAngle = 280f,
                useCenter = false,
                topLeft = Offset(w * 0.5f - ringRadius, h * 0.5f - ringRadius),
                size = Size(ringRadius * 2f, ringRadius * 2f),
                style = Stroke(width = 0.8.dp.toPx(), cap = StrokeCap.Round)
            )

            // Radiating electric lightning tendrils from the circle
            val tendril1 = Path().apply {
                moveTo(w * 0.78f, h * 0.28f)
                lineTo(w * 0.88f, h * 0.20f)
                lineTo(w * 0.84f, h * 0.16f)
                lineTo(w * 0.94f, h * 0.10f)
            }
            drawPath(
                path = tendril1,
                color = PortalRedLightning.copy(alpha = 0.8f * pulse),
                style = Stroke(width = 1.dp.toPx(), cap = StrokeCap.Round)
            )

            val tendril2 = Path().apply {
                moveTo(w * 0.24f, h * 0.74f)
                lineTo(w * 0.14f, h * 0.82f)
                lineTo(w * 0.18f, h * 0.86f)
                lineTo(w * 0.08f, h * 0.92f)
            }
            drawPath(
                path = tendril2,
                color = PortalRedLightning.copy(alpha = 0.8f * pulse),
                style = Stroke(width = 1.dp.toPx(), cap = StrokeCap.Round)
            )

            // 3. The Intertwined "B" Monogram (Lower Right / Underlayer)
            // Base shadow & gunmetal backplate
            val bBasePath = Path().apply {
                moveTo(w * 0.44f, h * 0.28f)
                lineTo(w * 0.65f, h * 0.28f)
                cubicTo(w * 0.76f, h * 0.28f, w * 0.80f, h * 0.38f, w * 0.75f, h * 0.47f)
                cubicTo(w * 0.82f, h * 0.52f, w * 0.83f, h * 0.65f, w * 0.76f, h * 0.74f)
                cubicTo(w * 0.70f, h * 0.79f, w * 0.62f, h * 0.80f, w * 0.44f, h * 0.80f)
                lineTo(w * 0.40f, h * 0.73f)
                lineTo(w * 0.54f, h * 0.73f)
                cubicTo(w * 0.63f, h * 0.73f, w * 0.68f, h * 0.69f, w * 0.67f, h * 0.60f)
                cubicTo(w * 0.66f, h * 0.52f, w * 0.59f, h * 0.49f, w * 0.50f, h * 0.49f)
                lineTo(w * 0.44f, h * 0.49f)
                close()
            }
            drawPath(
                path = bBasePath,
                brush = Brush.linearGradient(
                    listOf(Color(0xFF334155), Color(0xFF1E293B), Color(0xFF0F172A)),
                    start = Offset(w * 0.44f, h * 0.28f),
                    end = Offset(w * 0.80f, h * 0.80f)
                ),
                style = Fill
            )

            // "B" Upper Loop Highlight (Metallic Titanium)
            val bUpperHighlight = Path().apply {
                moveTo(w * 0.48f, h * 0.32f)
                lineTo(w * 0.63f, h * 0.32f)
                cubicTo(w * 0.72f, h * 0.32f, w * 0.74f, h * 0.38f, w * 0.71f, h * 0.44f)
                cubicTo(w * 0.67f, h * 0.47f, w * 0.60f, h * 0.47f, w * 0.52f, h * 0.47f)
                lineTo(w * 0.48f, h * 0.47f)
                close()
            }
            drawPath(
                path = bUpperHighlight,
                brush = Brush.linearGradient(
                    listOf(Color(0xFFE2E8F0), Color(0xFF94A3B8), Color(0xFF475569)),
                    start = Offset(w * 0.48f, h * 0.32f),
                    end = Offset(w * 0.72f, h * 0.47f)
                ),
                style = Fill
            )

            // "B" Lower Loop Highlight (Metallic Chiseled Bevel)
            val bLowerHighlight = Path().apply {
                moveTo(w * 0.50f, h * 0.52f)
                lineTo(w * 0.62f, h * 0.52f)
                cubicTo(w * 0.70f, h * 0.52f, w * 0.73f, h * 0.57f, w * 0.72f, h * 0.64f)
                cubicTo(w * 0.70f, h * 0.72f, w * 0.64f, h * 0.75f, w * 0.54f, h * 0.75f)
                lineTo(w * 0.46f, h * 0.75f)
                lineTo(w * 0.50f, h * 0.68f)
                lineTo(w * 0.56f, h * 0.68f)
                cubicTo(w * 0.62f, h * 0.68f, w * 0.64f, h * 0.63f, w * 0.63f, h * 0.59f)
                cubicTo(w * 0.62f, h * 0.55f, w * 0.58f, h * 0.53f, w * 0.52f, h * 0.53f)
                close()
            }
            drawPath(
                path = bLowerHighlight,
                brush = Brush.linearGradient(
                    listOf(Color(0xFFCBD5E1), Color(0xFF64748B), Color(0xFF334155)),
                    start = Offset(w * 0.46f, h * 0.52f),
                    end = Offset(w * 0.73f, h * 0.75f)
                ),
                style = Fill
            )

            // "B" Inner Holes
            val bHole1 = Path().apply {
                moveTo(w * 0.52f, h * 0.36f)
                lineTo(w * 0.61f, h * 0.36f)
                cubicTo(w * 0.65f, h * 0.36f, w * 0.65f, h * 0.41f, w * 0.61f, h * 0.41f)
                lineTo(w * 0.52f, h * 0.41f)
                close()
            }
            drawPath(path = bHole1, color = Color(0xFF0A0407), style = Fill)

            val bHole2 = Path().apply {
                moveTo(w * 0.51f, h * 0.57f)
                lineTo(w * 0.61f, h * 0.57f)
                cubicTo(w * 0.65f, h * 0.57f, w * 0.65f, h * 0.66f, w * 0.61f, h * 0.66f)
                lineTo(w * 0.51f, h * 0.66f)
                close()
            }
            drawPath(path = bHole2, color = Color(0xFF0A0407), style = Fill)

            // 4. The Intertwined "P" Monogram (Upper Left / Foreground)
            // "P" Outer Bevel & Chiseled Shading
            val pOuterPath = Path().apply {
                moveTo(w * 0.22f, h * 0.22f) // Gothic flared top-left tip
                lineTo(w * 0.36f, h * 0.26f)
                lineTo(w * 0.36f, h * 0.45f)
                lineTo(w * 0.48f, h * 0.45f)
                cubicTo(w * 0.62f, h * 0.45f, w * 0.64f, h * 0.26f, w * 0.48f, h * 0.26f)
                lineTo(w * 0.36f, h * 0.26f)
                lineTo(w * 0.36f, h * 0.22f)
                lineTo(w * 0.50f, h * 0.22f)
                cubicTo(w * 0.68f, h * 0.22f, w * 0.70f, h * 0.50f, w * 0.50f, h * 0.50f)
                lineTo(w * 0.36f, h * 0.50f)
                lineTo(w * 0.36f, h * 0.72f)
                lineTo(w * 0.29f, h * 0.80f) // Sharp dagger blade tip
                lineTo(w * 0.29f, h * 0.48f)
                lineTo(w * 0.22f, h * 0.48f)
                close()
            }
            drawPath(
                path = pOuterPath,
                brush = Brush.linearGradient(
                    listOf(Color(0xFF64748B), Color(0xFF334155), Color(0xFF1E293B)),
                    start = Offset(w * 0.22f, h * 0.22f),
                    end = Offset(w * 0.68f, h * 0.80f)
                ),
                style = Fill
            )

            // "P" Facing Platinum Highlight (Brushed Metallic Titanium)
            val pFacePath = Path().apply {
                moveTo(w * 0.24f, h * 0.24f)
                lineTo(w * 0.48f, h * 0.24f)
                cubicTo(w * 0.63f, h * 0.24f, w * 0.64f, h * 0.45f, w * 0.48f, h * 0.45f)
                lineTo(w * 0.35f, h * 0.45f)
                lineTo(w * 0.35f, h * 0.72f)
                lineTo(w * 0.30f, h * 0.77f)
                lineTo(w * 0.30f, h * 0.45f)
                lineTo(w * 0.24f, h * 0.45f)
                close()
            }
            drawPath(
                path = pFacePath,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White,
                        Color(0xFFE2E8F0),
                        Color(0xFF94A3B8),
                        Color(0xFF64748B)
                    ),
                    startY = h * 0.22f,
                    endY = h * 0.77f
                ),
                style = Fill
            )

            // "P" Gothic Flare Serif at Top Left
            val pSerif = Path().apply {
                moveTo(w * 0.22f, h * 0.22f)
                lineTo(w * 0.32f, h * 0.26f)
                lineTo(w * 0.32f, h * 0.34f)
                lineTo(w * 0.26f, h * 0.28f)
                close()
            }
            drawPath(path = pSerif, color = Color(0xFFF8FAFC), style = Fill)

            // "P" Inner Negative Cutout
            val pHole = Path().apply {
                moveTo(w * 0.36f, h * 0.28f)
                lineTo(w * 0.47f, h * 0.28f)
                cubicTo(w * 0.56f, h * 0.28f, w * 0.56f, h * 0.40f, w * 0.47f, h * 0.40f)
                lineTo(w * 0.36f, h * 0.40f)
                close()
            }
            drawPath(path = pHole, color = Color(0xFF090407), style = Fill)

            // 5. Chiseled Fissures / Surface Cracks across the Metal Face
            val crack1 = Path().apply {
                moveTo(w * 0.43f, h * 0.24f)
                lineTo(w * 0.40f, h * 0.31f)
                lineTo(w * 0.46f, h * 0.36f)
            }
            drawPath(
                path = crack1,
                color = PortalRedLightning.copy(alpha = 0.85f * pulse),
                style = Stroke(width = 0.9.dp.toPx(), cap = StrokeCap.Round)
            )

            val crack2 = Path().apply {
                moveTo(w * 0.60f, h * 0.48f)
                lineTo(w * 0.56f, h * 0.56f)
                lineTo(w * 0.63f, h * 0.61f)
            }
            drawPath(
                path = crack2,
                color = PortalRedLightning.copy(alpha = 0.85f * pulse),
                style = Stroke(width = 0.9.dp.toPx(), cap = StrokeCap.Round)
            )

            // 6. High-Voltage Crimson Lightning Strike (⚡) Slicing Through Center Rift
            val boltPath = Path().apply {
                moveTo(w * 0.72f, h * 0.16f) // Top apex
                lineTo(w * 0.56f, h * 0.38f) // First fracture
                lineTo(w * 0.63f, h * 0.38f) // Step
                lineTo(w * 0.44f, h * 0.65f) // Deep fracture
                lineTo(w * 0.52f, h * 0.65f) // Step
                lineTo(w * 0.34f, h * 0.88f) // Exit spark
                lineTo(w * 0.42f, h * 0.70f)
                lineTo(w * 0.36f, h * 0.70f)
                lineTo(w * 0.52f, h * 0.45f)
                lineTo(w * 0.45f, h * 0.45f)
                close()
            }

            // Outer Crimson Plasma Glow on Bolt
            drawPath(
                path = boltPath,
                color = PortalRedLightning.copy(alpha = 0.9f * pulse),
                style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Core Lightning Bolt (Electric Red with White-Hot Accent)
            drawPath(
                path = boltPath,
                brush = Brush.linearGradient(
                    listOf(
                        Color.White,
                        Color(0xFFFFA3B5),
                        PortalRedLightning,
                        PortalCrimson
                    ),
                    start = Offset(w * 0.72f, h * 0.16f),
                    end = Offset(w * 0.34f, h * 0.88f)
                ),
                style = Fill
            )

            // High-voltage center energy sparks
            drawCircle(
                color = Color.White.copy(alpha = 0.95f * pulse),
                radius = 1.5.dp.toPx(),
                center = Offset(w * 0.44f, h * 0.65f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.9f * pulse),
                radius = 1.2.dp.toPx(),
                center = Offset(w * 0.56f, h * 0.38f)
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
