package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FacilityCategory
import com.example.model.FacilityItem
import com.example.theme.*

@Composable
fun FacilityMapCanvas(
    userLat: Double,
    userLon: Double,
    facilities: List<FacilityItem>,
    category: FacilityCategory,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 18f,
        targetValue = 48f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_radius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )

    val pinColor = when (category) {
        FacilityCategory.SHELTER -> RedAlert
        FacilityCategory.WATER -> BlueRecover
        FacilityCategory.SUPPLY -> GreenOk
        FacilityCategory.FOOD -> Amber
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, LineBorder, RoundedCornerShape(12.dp))
            .background(Color(0xFFEBF1F5))
            .testTag("facility_map_canvas")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val center = Offset(w / 2f, h / 2f)

            // Grid lines (streets pattern)
            val gridColor = Color(0xFFDCE5EC)
            for (x in 0..(w.toInt()) step 50) {
                drawLine(gridColor, Offset(x.toFloat(), 0f), Offset(x.toFloat(), h), strokeWidth = 1.5f)
            }
            for (y in 0..(h.toInt()) step 50) {
                drawLine(gridColor, Offset(0f, y.toFloat()), Offset(w, y.toFloat()), strokeWidth = 1.5f)
            }

            // Diagonal road
            drawLine(
                Color(0xFFCFDCE6),
                Offset(0f, h * 0.8f),
                Offset(w, h * 0.2f),
                strokeWidth = 6f
            )

            // Concentric range circles
            val dashedEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            drawCircle(
                color = Color(0xFF90A4AE).copy(alpha = 0.45f),
                radius = w * 0.25f,
                center = center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f, pathEffect = dashedEffect)
            )
            drawCircle(
                color = Color(0xFF90A4AE).copy(alpha = 0.3f),
                radius = w * 0.42f,
                center = center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f, pathEffect = dashedEffect)
            )

            // Pulse ring around user
            drawCircle(
                color = TealAccent.copy(alpha = pulseAlpha),
                radius = pulseRadius,
                center = center
            )

            // User location pin (center)
            drawCircle(
                color = Color.White,
                radius = 8f,
                center = center
            )
            drawCircle(
                color = TealAccent,
                radius = 5.5f,
                center = center
            )

            // Render facility markers distributed relative to user location
            facilities.take(4).forEachIndexed { index, facility ->
                // Calculate pseudo visual offset based on lat/lon deltas
                val dx = ((facility.lon - userLon) * 28000f).toFloat().coerceIn(-w * 0.4f, w * 0.4f)
                val dy = -((facility.lat - userLat) * 28000f).toFloat().coerceIn(-h * 0.38f, h * 0.38f)

                // If facilities are clustered, add staggered spread
                val angle = (index * 90.0 + 35.0) * (Math.PI / 180.0)
                val distSpread = (w * 0.28f) * (0.6f + index * 0.22f)
                val fx = (center.x + kotlin.math.cos(angle).toFloat() * distSpread).coerceIn(24f, w - 24f)
                val fy = (center.y + kotlin.math.sin(angle).toFloat() * distSpread * 0.6f).coerceIn(24f, h - 24f)

                val facilityPos = Offset(fx, fy)

                // Pin circle shadow
                drawCircle(
                    color = Color.Black.copy(alpha = 0.18f),
                    radius = 11f,
                    center = facilityPos.copy(y = facilityPos.y + 2f)
                )

                // Pin circle
                drawCircle(
                    color = pinColor,
                    radius = 10f,
                    center = facilityPos
                )
                drawCircle(
                    color = Color.White,
                    radius = 4f,
                    center = facilityPos
                )
            }
        }

        // Location Tag overlay top-left
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White.copy(alpha = 0.92f))
                .border(1.dp, LineBorder, RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "📍 現在地周辺（半径1km圏内）",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
        }
    }
}
