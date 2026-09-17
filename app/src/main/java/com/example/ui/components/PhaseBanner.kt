package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppTab
import com.example.model.Language
import com.example.model.Localization
import com.example.model.Phase
import com.example.theme.BlueRecover
import com.example.theme.RedAlert
import com.example.theme.TealAccent

@Composable
fun PhaseBanner(
    phase: Phase,
    tab: AppTab,
    lang: Language,
    modifier: Modifier = Modifier
) {
    val isDisaster = phase == Phase.HASSAI

    val bannerColor by animateColorAsState(
        targetValue = when {
            isDisaster -> RedAlert
            tab == AppTab.RECOVER -> BlueRecover
            else -> TealAccent
        },
        label = "banner_color"
    )

    val title = when {
        isDisaster -> Localization.t("hassai_t", lang)
        tab == AppTab.RECOVER -> Localization.t("nav_recover", lang)
        else -> Localization.t("heiji_t", lang)
    }

    val subtitle = when {
        isDisaster -> Localization.t("hassai_s", lang)
        tab == AppTab.RECOVER -> Localization.t("langInfo", lang)
        else -> Localization.t("heiji_s", lang)
    }

    // Pulse animation for disaster dot
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = if (isDisaster) 0.35f else 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_alpha"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(bannerColor)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("phase_banner"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Dot indicator
        Box(
            modifier = Modifier
                .size(10.dp)
                .alpha(dotAlpha)
                .clip(CircleShape)
                .background(Color.White)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.92f)
            )
        }
    }
}
