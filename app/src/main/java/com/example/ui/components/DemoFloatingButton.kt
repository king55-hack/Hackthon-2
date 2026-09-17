package com.example.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.model.Localization
import com.example.model.Phase
import com.example.theme.Amber
import com.example.theme.Navy

@Composable
fun DemoFloatingButton(
    phase: Phase,
    lang: Language,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDisaster = phase == Phase.HASSAI
    val text = if (isDisaster) {
        Localization.t("demoStop", lang)
    } else {
        Localization.t("demoStart", lang)
    }

    Button(
        onClick = onToggle,
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isDisaster) Navy else Amber,
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
        modifier = modifier
            .padding(bottom = 12.dp)
            .testTag("demo_toggle_button")
    ) {
        Icon(
            imageVector = if (isDisaster) Icons.Default.RestartAlt else Icons.Default.Bolt,
            contentDescription = null,
            tint = Color.White
        )
        Text(
            text = text,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 6.dp)
        )
    }
}
