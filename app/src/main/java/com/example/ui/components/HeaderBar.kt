package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.model.Phase
import com.example.theme.DisasterNavy
import com.example.theme.Navy

@Composable
fun HeaderBar(
    phase: Phase,
    currentLanguage: Language,
    onSelectLanguage: (Language) -> Unit,
    modifier: Modifier = Modifier
) {
    val headerBg by animateColorAsState(
        targetValue = if (phase == Phase.HASSAI) DisasterNavy else Navy,
        label = "header_bg"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(headerBg)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("app_header")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Logo Icon
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CrisisAlert,
                    contentDescription = "Logo",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Brand Title
            Column {
                Text(
                    text = "ソナエル",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "SONAERU",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF9CC3DC),
                    letterSpacing = 2.5.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Language Pills
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Language.values().forEach { lang ->
                    val isSelected = lang == currentLanguage
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) Color.White else Color.Transparent)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { onSelectLanguage(lang) }
                            .padding(horizontal = 7.dp, vertical = 4.dp)
                            .testTag("lang_btn_${lang.code}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = lang.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Navy else Color(0xFFCFE0EC)
                        )
                    }
                }
            }
        }
    }
}
