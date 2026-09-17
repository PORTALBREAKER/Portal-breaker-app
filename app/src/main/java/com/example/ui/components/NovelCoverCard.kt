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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PortalCyan
import com.example.ui.theme.PortalGold
import com.example.ui.theme.PortalPurple
import com.example.ui.theme.PortalSky
import com.example.ui.theme.VoidBorder
import com.example.ui.theme.VoidDark

@Composable
fun NovelCoverCard(
    modifier: Modifier = Modifier,
    onLongPressSecret: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "coverRift")
    val riftPulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "riftPulse"
    )

    Box(
        modifier = modifier
            .shadow(16.dp, RoundedCornerShape(20.dp), spotColor = PortalCyan)
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFF090D1A),
                        Color(0xFF130D2B),
                        Color(0xFF070B14)
                    )
                )
            )
            .border(
                1.5.dp,
                Brush.linearGradient(
                    listOf(PortalCyan.copy(alpha = 0.6f), PortalPurple.copy(alpha = 0.4f), Color.Transparent)
                ),
                RoundedCornerShape(20.dp)
            )
    ) {
        // Procedural Portal Rift Graphic
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Cosmic nebulas
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(PortalPurple.copy(alpha = 0.45f * riftPulse), Color.Transparent),
                    center = Offset(w * 0.5f, h * 0.45f),
                    radius = w * 0.65f
                )
            )
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(PortalCyan.copy(alpha = 0.35f * riftPulse), Color.Transparent),
                    center = Offset(w * 0.5f, h * 0.5f),
                    radius = w * 0.45f
                )
            )

            // Outer portal rings
            drawCircle(
                color = PortalCyan.copy(alpha = 0.3f),
                radius = w * 0.38f,
                center = Offset(w * 0.5f, h * 0.45f),
                style = Stroke(width = 1.5.dp.toPx())
            )
            drawCircle(
                color = PortalPurple.copy(alpha = 0.5f),
                radius = w * 0.28f,
                center = Offset(w * 0.5f, h * 0.45f),
                style = Stroke(width = 2.dp.toPx())
            )

            // Shattered Spatial Fracture lightning lines
            val riftPath = Path().apply {
                moveTo(w * 0.5f, h * 0.15f)
                lineTo(w * 0.46f, h * 0.32f)
                lineTo(w * 0.54f, h * 0.42f)
                lineTo(w * 0.48f, h * 0.55f)
                lineTo(w * 0.55f, h * 0.68f)
                lineTo(w * 0.50f, h * 0.82f)
            }
            drawPath(
                path = riftPath,
                color = Color.White.copy(alpha = riftPulse),
                style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
            )
            drawPath(
                path = riftPath,
                color = PortalCyan.copy(alpha = 0.7f),
                style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round)
            )

            // Floating crystalline shards
            drawCircle(PortalCyan, 3.dp.toPx(), Offset(w * 0.28f, h * 0.35f))
            drawCircle(PortalSky, 2.dp.toPx(), Offset(w * 0.72f, h * 0.42f))
            drawCircle(PortalPurple, 3.5.dp.toPx(), Offset(w * 0.35f, h * 0.62f))
            drawCircle(PortalGold, 2.5.dp.toPx(), Offset(w * 0.65f, h * 0.28f))
        }

        // Title and Author typography overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "WEBNOVEL SERIES",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = PortalCyan,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "PORTAL",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
            )
            Text(
                text = "BREAKER",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Black,
                    color = PortalCyan,
                    letterSpacing = 3.sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "ARUN",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = PortalSky,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.5.sp
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
